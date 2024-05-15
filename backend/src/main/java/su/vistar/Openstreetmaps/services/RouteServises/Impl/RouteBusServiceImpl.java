package su.vistar.Openstreetmaps.services.RouteServises.Impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.vistar.Openstreetmaps.DTO.RouteDTO;
import su.vistar.Openstreetmaps.models.Routes.Point;
import su.vistar.Openstreetmaps.models.Routes.Route;
import su.vistar.Openstreetmaps.models.Routes.RouteStop;
import su.vistar.Openstreetmaps.models.Routes.Stop;
import su.vistar.Openstreetmaps.repositories.GateRepositories.CityRepository;
import su.vistar.Openstreetmaps.repositories.RouteRepositories.*;
import su.vistar.Openstreetmaps.services.RouteServises.RouteBusService;

import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.query.sqm.tree.SqmNode.log;
import static su.vistar.Openstreetmaps.services.GateServises.Impl.UpdateGateServiceImpl.sendOverpassQuery;

@Service
public class RouteBusServiceImpl implements RouteBusService {
    private final PointRepository pointRepository;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final StopRepository stopRepository;
    private final LineStringRepository lineStringRepository;
    private final ModelMapper modelMapper;
    private final CityRepository cityRepository;

    public RouteBusServiceImpl(PointRepository pointRepository, RouteRepository routeRepository, RouteStopRepository routeStopRepository, StopRepository stopRepository, LineStringRepository lineStringRepository, ModelMapper modelMapper, CityRepository cityRepository) {
        this.pointRepository = pointRepository;
        this.routeRepository = routeRepository;
        this.routeStopRepository = routeStopRepository;
        this.stopRepository = stopRepository;
        this.lineStringRepository = lineStringRepository;
        this.modelMapper = modelMapper;
        this.cityRepository = cityRepository;
    }

    @Override
    public List<String> getWaysByRouteIdByOsm(Long routeId) {
        return null;
    }

    @Override
    public List<String> getByRouteIdByOsm(Long routeId) throws Exception {

        return pointRepository.findByRouteId(routeId);
    }

    @Override
    public List<String> getLinesByRouteIdByOsm(Long routeId) throws Exception {
        String overpassUrl = "https://overpass-api.de/api/interpreter";

        String query = "[out:json];" +
                "relation(" + routeId + ");\n" +
                "out;";

        sendQuery(overpassUrl, query);


        return lineStringRepository.findByRouteId(routeId);
    }

    public void sendQuery(String overpassUrl, String query) throws Exception {

        String response = sendOverpassQuery(overpassUrl, query);
        Gson gson = new Gson();
        JsonObject osmObject = gson.fromJson(response, JsonObject.class);
        JsonArray elements = osmObject.getAsJsonArray("elements");
        System.out.println(elements);

        for (JsonElement element : elements) {
            JsonObject jsonElement = element.getAsJsonObject();
            JsonArray members = jsonElement.get("members").getAsJsonArray();
            System.out.println(members);

            int count = 0;
            for (var member : members) {
                System.out.println(member);

                String type = member.getAsJsonObject().get("type").getAsString();
                Route route = new Route();
                su.vistar.Openstreetmaps.models.Routes.LineString lineStringEntity =
                        new su.vistar.Openstreetmaps.models.Routes.LineString();
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
                    long wayId = member.getAsJsonObject().get("ref").getAsLong();

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
//                        resultWays.add(String.format("ST_AsGeoJSON(%s)", lineStringEntity));
                        lineStringRepository.save(lineStringEntity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if ("node".equals(type)) {
                    if ("stop".equals(member.getAsJsonObject().get("role").getAsString())
                    || "platform".equals(member.getAsJsonObject().get("role").getAsString())
                    || "platform_exit_only".equals(member.getAsJsonObject().get("role").getAsString())) {
                        stop
                                .setId(member.getAsJsonObject().get("ref").getAsLong())
                                .setName(member.getAsJsonObject().get("role").getAsString());
                        try {
                            Coordinate coordinate = ResponseNode(stop.getId());
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
//                            resultPoints.add(String.format("ST_AsGeoJSON(%s)", point));
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

    @Override
    public List<RouteDTO> getAllRoutes() {
        List<Route> routes = routeRepository.findAllByOrderByRef();
        return routes.stream()
                .map(route -> modelMapper.map(route, RouteDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RouteDTO> getAllRoutesByCityByOSM(Long cityId) {
        String cityName = cityRepository.findById(cityId).get().getName();
        String overpassUrl = "https://overpass-api.de/api/interpreter";

        String query = "[out:json];" +
                "area[\"name\"=\"" + cityName + "\"];\n" +
                "(relation[\"route\"=\"bus\"](area););\n" +
                "out;";
        Set<RouteDTO> resultRoutes = new HashSet<>();
        String response = null;
        try {
            response = sendOverpassQuery(overpassUrl, query);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Gson gson = new Gson();
        JsonObject osmObject = gson.fromJson(response, JsonObject.class);
        JsonArray elements = osmObject.getAsJsonArray("elements");

        for (JsonElement element : elements) {
            JsonObject jsonElement = element.getAsJsonObject();
            JsonArray members = jsonElement.get("members").getAsJsonArray();
            Route route = new Route();
            int count = 0;
            for (var member : members) {

                String type = member.getAsJsonObject().get("type").getAsString();
                su.vistar.Openstreetmaps.models.Routes.LineString lineStringEntity =
                        new su.vistar.Openstreetmaps.models.Routes.LineString();
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
            }
            resultRoutes.add(new RouteDTO(route.getId(),
                    route.getName(), route.getFrom(), route.getRef()));
        }

        log.info("Method getAllGatesByCityByOSM end");
        return resultRoutes.stream()
                .sorted(Comparator.comparing(RouteDTO::getRef))
                .toList();
    }

    @Override
    public List<String> getPointsByRouteId(Long id) {
        return pointRepository.findByRouteId(id);
    }

    @Override
    public List<String> getWaysByRouteId(Long id) {
        return lineStringRepository.findByRouteId(id);
    }

    @Override
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

    private Coordinate ResponseNode(Long Id) throws Exception {
        String overpassUrl = "https://overpass-api.de/api/interpreter";
        String query = "[out:json];\n" +
                "node(" + Id + ");\n" +
                "out;";
        String response = sendOverpassQuery(overpassUrl, query);
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray elements = jsonResponse.getJSONArray("elements");
        JSONObject element = elements.getJSONObject(0);
        return new Coordinate(element.getDouble("lon"), element.getDouble("lat"));
    }

    private List<Coordinate> ResponseWay(long Id) throws Exception {
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
                su.vistar.Openstreetmaps.models.Routes.LineString lineStringEntity =
                        new su.vistar.Openstreetmaps.models.Routes.LineString();
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
                    long wayId = member.getAsJsonObject().get("ref").getAsLong();

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
                            Coordinate coordinate = ResponseNode(stop.getId());
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

}
