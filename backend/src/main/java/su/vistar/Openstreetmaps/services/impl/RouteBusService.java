package su.vistar.Openstreetmaps.services.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import su.vistar.Openstreetmaps.models.RouteBus.Point;
import su.vistar.Openstreetmaps.models.RouteBus.Route;
import su.vistar.Openstreetmaps.models.RouteBus.RouteStop;
import su.vistar.Openstreetmaps.models.RouteBus.Stop;
import su.vistar.Openstreetmaps.repositories.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class RouteBusService {
    private final PointRepository pointRepository;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final StopRepository stopRepository;
    private final LineStringRepository lineStringRepository;

    public RouteBusService(PointRepository pointRepository, RouteRepository routeRepository, RouteStopRepository routeStopRepository, StopRepository stopRepository, LineStringRepository lineStringRepository) {
        this.pointRepository = pointRepository;
        this.routeRepository = routeRepository;
        this.routeStopRepository = routeStopRepository;
        this.stopRepository = stopRepository;
        this.lineStringRepository = lineStringRepository;
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public List<String> getPointsByRouteId(Long id) {
        return pointRepository.findByRouteId(id);
    }

    public List<String> getWaysByRouteId(Long id) {
        return lineStringRepository.findByRouteId(id);
    }

    public void updateAllBusStop() {
        String overpassUrl = "https://overpass-api.de/api/interpreter";
        //примерно центр Воронежа
        double latitudeCurrentNode = 51.661535;
        double longitudeCurrentNode = 39.200287;
        //14 км радиуса, чтоб охватить весь Воронеж
        int radiusMeters = 12000;
        List<String> barriersType = new ArrayList<>();
        barriersType.add("stop_position");
        for (String barrier : barriersType) {
            String query = "[out:json];" +
                    "(relation[route=bus](around:" + radiusMeters + "," +
                    latitudeCurrentNode + "," + longitudeCurrentNode + ");" +
                    "way[public_transport=" + barrier + "](around:" + radiusMeters + "," +
                    latitudeCurrentNode + "," + longitudeCurrentNode + ");" +
                    "relation[public_transport=" + barrier + "](around:" + radiusMeters + "," +
                    latitudeCurrentNode + "," + longitudeCurrentNode + "););" +
                    "out;";
            try {
                String response = sendOverpassQuery(overpassUrl, query);
                processOverpassResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Coordinate ResponseNode(Stop stop, Long Id) throws Exception {
        String overpassUrl = "https://overpass-api.de/api/interpreter";
        String query = "[out:json];\n" +
                "node(" + Id + ");\n" +
                "out;";
        String response = sendOverpassQuery(overpassUrl, query);
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray elements = jsonResponse.getJSONArray("elements");
        JSONObject element = elements.getJSONObject(0);
        Coordinate coordinate = new Coordinate(element.getDouble("lon"), element.getDouble("lat"));
        return coordinate;
        //stop.setLon(element.getDouble("lon"));
        //stop.setLat(element.getDouble("lat"));
        /*JSONArray tags = element.getJSONArray("tags");
        JSONObject tag = tags.getJSONObject(0);
        stop.setName(tag.getString("name"));*/
    }

    public List<Coordinate> ResponseWay(long Id) throws Exception {
        String overpassUrl = "https://overpass-api.de/api/interpreter";
        String query = "[out:json];\n" +
                "way(id:" + Id + ");\n" +
                "node(w);\n" +
                "out;";
        String response = sendOverpassQuery(overpassUrl, query);
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray elements = jsonResponse.getJSONArray("elements");
        List<Coordinate> coordinates = new ArrayList<>();
        if (elements.length() > 0) {
            for (int i = 0; i < elements.length(); i++) {
                JSONObject element = elements.getJSONObject(i);
                if (element.has("lon") && element.has("lat"))
                    coordinates.add(new Coordinate(element.getDouble("lon"), element.getDouble("lat")));
            }
        }

        coordinates.sort((c1, c2) -> {
            double distance1 = calculateDistance(c1, coordinates);
            double distance2 = calculateDistance(c2, coordinates);
            return Double.compare(distance2, distance1);
        });

        Coordinate fixedPoint = coordinates.get(0);

        coordinates.subList(1, coordinates.size()).sort((c1, c2) -> {
            double distance1 = calculateDistance(fixedPoint, c1);
            double distance2 = calculateDistance(fixedPoint, c2);
            return Double.compare(distance1, distance2);
        });

        return coordinates;
    }

    private double calculateDistance(Coordinate c, List<Coordinate> coordinates) {
        double sum = 0;
        for (Coordinate other : coordinates) {
            double xDiff = c.getX() - other.getX();
            double yDiff = c.getY() - other.getY();
            sum += Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        }
        return sum;
    }

    private double calculateDistance(Coordinate c1, Coordinate c2) {
        double xDiff = c1.getX() - c2.getX();
        double yDiff = c1.getY() - c2.getY();
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    private void processOverpassResponse(String response) {
        String overpassUrl = "https://overpass-api.de/api/interpreter";
        JSONObject jsonResponse = new JSONObject(response);

        Gson gson = new Gson();
        JsonObject osmObject = gson.fromJson(response, JsonObject.class);
        JsonArray elements = osmObject.getAsJsonArray("elements");

        for (JsonElement element : elements) {
            JsonObject jsonElement = element.getAsJsonObject();
            JsonArray members = jsonElement.get("members").getAsJsonArray();

            int count = 0;
            for (var member : members) {

                String type = member.getAsJsonObject().get("type").getAsString();
                Route route = new Route();
                su.vistar.Openstreetmaps.models.RouteBus.LineString lineStringEntity =
                        new su.vistar.Openstreetmaps.models.RouteBus.LineString();
                RouteStop routeStop = new RouteStop();
                Stop stop = new Stop();
                Point point = new Point();


                JsonElement tagsElement = jsonElement.get("tags");
                if (tagsElement != null && tagsElement.isJsonObject()) {
                    JsonObject tagsObject = tagsElement.getAsJsonObject();
                    route
                            .setId(jsonElement.get("id").getAsLong());
                    if (tagsObject.has("name") && !tagsObject.get("name").isJsonNull()) {
                        route.setName(tagsObject.get("name").getAsString());
                    }
                    if (tagsObject.has("from") && !tagsObject.get("from").isJsonNull()) {
                        route.setFrom(tagsObject.get("from").getAsString());
                    }
                    if (tagsObject.has("network") && !tagsObject.get("network").isJsonNull()) {
                        route.setNetwork(tagsObject.get("network").getAsString());
                    }
                    if (tagsObject.has("to") && !tagsObject.get("to").isJsonNull()) {
                        route.setTo(tagsObject.get("to").getAsString());
                    }
                    if (tagsObject.has("public_transport:version") && !tagsObject.get("public_transport:version").isJsonNull()) {
                        route.setPublicTransport(tagsObject.get("public_transport:version").getAsInt());
                    }
                    if (tagsObject.has("ref") && !tagsObject.get("ref").isJsonNull()) {
                        route.setRef(tagsObject.get("ref").getAsString());
                    }
                    if (tagsObject.has("route") && !tagsObject.get("route").isJsonNull()) {
                        route.setRoute(tagsObject.get("route").getAsString());
                    }
                }
                routeRepository.save(route);

                if ("way".equals(type)) {
                    Long wayId = member.getAsJsonObject().get("ref").getAsLong();
                    // Создание LineString из массива координат

                    try {
                        List<Coordinate> coordinates = ResponseWay(wayId);
                        Coordinate[] coordinates1 = coordinates.toArray(Coordinate[]::new);

                        org.locationtech.jts.geom.GeometryFactory geometryFactory =
                                new org.locationtech.jts.geom.GeometryFactory(new PrecisionModel(), 3857);

                        LineString lineString = geometryFactory.createLineString(coordinates1);
                        lineString.setSRID(3857);

                        lineStringEntity
                                .setIdLine(UUID.randomUUID())
                                .setId(wayId)
                                .setRouteId(route.getId())
                                .setGeom(lineString);
                        lineStringRepository.save(lineStringEntity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if ("node".equals(type)) {
                    if ("stop".equals(member.getAsJsonObject().get("role").getAsString())) {
                        stop
                                .setId(member.getAsJsonObject().get("ref").getAsLong())
                                .setName(member.getAsJsonObject().get("role").getAsString());
                        try {
                            Coordinate coordinate = ResponseNode(stop, stop.getId());
                            stop.setLat(coordinate.getX());
                            stop.setLon(coordinate.getY());

                            org.locationtech.jts.geom.GeometryFactory geometryFactory = new org.locationtech.jts.geom.GeometryFactory(new PrecisionModel(), 3857);
                            org.locationtech.jts.geom.Point point_ = geometryFactory.createPoint(new Coordinate(coordinate.getX(), coordinate.getY()));
                            point_.setSRID(3857);
                            stopRepository.save(stop);

                            point.setId(UUID.randomUUID());
                            point.setStopId(stop.getId());
                            point.setPoint(point_);
                            point.setRouteId(route.getId());
                            pointRepository.save(point);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        routeStop.setId(UUID.randomUUID());
                        routeStop.setRoute(route);
                        routeStop.setStop(stop);
                        routeStop.setSequence(count++);

                        routeStopRepository.save(routeStop);
                    }
                }
            }
        }
    }


    private String sendOverpassQuery(String overpassUrl, String query) throws Exception {
        URL url = new URL(overpassUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        connection.getOutputStream().write(("data=" + query).getBytes("UTF-8"));

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        connection.disconnect();

        return response.toString();
    }
}
