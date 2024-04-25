package su.vistar.Openstreetmaps;

import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;
import su.vistar.Openstreetmaps.models.RouteBus.Point;
import su.vistar.Openstreetmaps.models.RouteBus.Route;
import su.vistar.Openstreetmaps.models.RouteBus.RouteStop;
import su.vistar.Openstreetmaps.models.RouteBus.Stop;
import su.vistar.Openstreetmaps.repositories.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class Main {
    private final PointRepository pointRepository;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final StopRepository stopRepository;
    private final LineStringRepository lineStringRepository;

    public Main(PointRepository pointRepository, RouteRepository routeRepository, RouteStopRepository routeStopRepository, StopRepository stopRepository, LineStringRepository lineStringRepository) {
        this.pointRepository = pointRepository;
        this.routeRepository = routeRepository;
        this.routeStopRepository = routeStopRepository;
        this.stopRepository = stopRepository;
        this.lineStringRepository = lineStringRepository;
    }

    public static void main(String[] args) {
        updateAllBusStop();
    }

    public static void updateAllBusStop() {
        String overpassUrl = "https://overpass-api.de/api/interpreter";
        //примерно центр Воронежа
        double latitudeCurrentNode = 51.661535;
        double longitudeCurrentNode = 39.200287;
        //14 км радиуса, чтоб охватить весь Воронеж
        int radiusMeters = 12000;
        List<String> barriersType = new ArrayList<>();
        barriersType.add("stop_position");
//        barriersType.add("stop_position");
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

//    private LineString createLineString(RouteData routeData) {
//        GeometryFactory geometryFactory = new GeometryFactory();
//        List<NodeData> nodes = routeData.getNodes();
//        Coordinate[] coordinates = new Coordinate[nodes.size()];
//        for (int i = 0; i < nodes.size(); i++) {
//            coordinates[i] = nodes.get(i).getCoordinate();
//        }
//        return geometryFactory.createLineString(coordinates);
//    }

    public static Coordinate ResponseNode(Stop stop,Long Id) throws Exception{
        String overpassUrl = "https://overpass-api.de/api/interpreter";
        String query = "[out:json];\n" +
                "node("+ Long.toString(Id)+");\n" +
                "out;";
        String response = sendOverpassQuery(overpassUrl, query);
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray elements = jsonResponse.getJSONArray("elements");
        JSONObject element = elements.getJSONObject(0);
        Coordinate coordinate = new Coordinate(element.getDouble("lon"),element.getDouble("lat"));
        return coordinate;
        //stop.setLon(element.getDouble("lon"));
        //stop.setLat(element.getDouble("lat"));
        /*JSONArray tags = element.getJSONArray("tags");
        JSONObject tag = tags.getJSONObject(0);
        stop.setName(tag.getString("name"));*/
    }

    public static List<Coordinate> ResponseWay(long Id)throws Exception{
        String overpassUrl = "https://overpass-api.de/api/interpreter";
        String query = "[out:json];\n" +
                "way("+ Long.toString(Id)+");\n" +
                "(._;>;);\n" +
                "out;";
        String response = sendOverpassQuery(overpassUrl, query);
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray elements = jsonResponse.getJSONArray("elements");
        List<Coordinate> coordinates = new ArrayList<>();
        if (elements.length() > 0){
            for (int i = 0; i < elements.length();i++){
                JSONObject element = elements.getJSONObject(i);
                if(element.has("lon") && element.has("lat"))
                    coordinates.add(new Coordinate(element.getDouble("lon"),element.getDouble("lat")));
            }
        }
        return coordinates;
    }

    private static void processOverpassResponse(String response) {
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

                if ("way".equals(type)) {
                    Long wayId = member.getAsJsonObject().get("ref").getAsLong();
                    // Создание LineString из массива координат
                    GeometryFactory geometryFactory = new GeometryFactory();

                    try {
                        List<Coordinate> coordinates = ResponseWay(wayId);
                        Coordinate[] coordinates1 = coordinates.stream().toArray(Coordinate[]::new);;
                        LineString lineString = geometryFactory.createLineString(coordinates1);
                        lineStringEntity
                                .setId(wayId)
                                .setRouteId(route.getId())
                                .setGeom(lineString);
                        //lineStringRepository.save(lineStringEntity);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println(lineStringEntity);
                }

                if ("node".equals(type)) {
                    if ("stop".equals(member.getAsJsonObject().get("role").getAsString())) {
                        stop
                                .setId(member.getAsJsonObject().get("ref").getAsLong())
                                .setName(member.getAsJsonObject().get("role").getAsString());
                                try{
                                    Coordinate coordinate = ResponseNode(stop, stop.getId());
                                    stop.setLat(coordinate.getX());
                                    stop.setLon(coordinate.getY());
                                    GeometryFactory geometryFactory = new GeometryFactory();
                                    org.locationtech.jts.geom.Point point_ = geometryFactory.createPoint(coordinate);
                                    point
                                            .setId(UUID.randomUUID())
                                            .setStopId(stop.getId())
                                            .setGeom(point_);
                                    //pointRepository.save(point);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }

                        routeStop
                                .setId(UUID.randomUUID())
                                .setRoute(route)
                                .setStop(stop)
                                .setSequence(count++);

                        //routeStopRepository.save(routeStop);
                        //stopRepository.save(stop);
                        System.out.println(stop);
                        System.out.println(routeStop);
                        System.out.println(point);

                    }
                    //routeRepository.save(route);
                    System.out.println(route);

                }
            }

        }

        JSONArray eeee = jsonResponse.getJSONArray("elements");
        for (
                int i = 0; i < eeee.length(); i++) {
            JSONObject element = eeee.getJSONObject(i);

//            System.out.println(element);
            GeometryFactory geometryFactory = new GeometryFactory();

            JSONArray members = element.getJSONArray("members");
            // тут нод и вей
//            System.out.println(members);

            JsonElement el = JsonParser.parseString(String.valueOf(element));
            JsonObject jsonObject = el.getAsJsonObject();

//            System.out.println(jsonObject);
//            for (int j = 0; j < jsonObject.size(); j++) {
//                JSONObject member = members.getJSONObject(i);

//            System.out.println("=========================================");
//            System.out.println(route);
//            System.out.println("=========================================");


//                System.out.println(member);
//                System.out.println("=========================================");


//                route = routeRepository.save(route);

//                List<LineString> lineString = new ArrayList<>();
//                LineString lineStringEntity = new LineString()
//                        .setRouteId(route.getId())
//                        .setGeom(lineString);
//                entityManager.persist(lineStringEntity);


//                JsonElement el = JsonParser.parseString(e);
//                JsonObject jsonObject = element.getAsJsonObject();
//                JsonArray membersArray = jsonObject.getAsJsonArray("members");

//                for (NodeData nodeData : member.getNodes()) {
//                    if ("stop".equals(nodeData.getRole())) {
//                        Stop stop = new Stop()
//                                .setName("Unnamed Stop") // You may want to change this to actual name
//                                .setLat(nodeData.getLat())
//                                .setLon(nodeData.getLon());
//                        stop = stopRepository.save(stop);
//
//                        Point point = geometryFactory.createPoint(nodeData.getCoordinate());
//                        PointEntity pointEntity = new PointEntity()
//                                .setStopId(stop.getId())
//                                .setGeom(point);
//                        entityManager.persist(pointEntity);
//
//                        RouteStop routeStop = new RouteStop()
//                                .setRouteId(route.getId())
//                                .setStopId(stop.getId());
//                        routeStopRepository.save(routeStop);
//                    }
//                    // Handle other node types if needed
//                }
        }

    }



    private static String sendOverpassQuery(String overpassUrl, String query) throws Exception {
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
