package su.vistar.Openstreetmaps;

import org.json.JSONArray;
import org.json.JSONObject;
import su.vistar.Openstreetmaps.models.LocalPlaceBusStop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main {
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

    private static void processOverpassResponse(String response) {
        JSONObject jsonResponse = new JSONObject(response);

        System.out.println(jsonResponse);

        JSONArray elements = jsonResponse.getJSONArray("elements");
        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            long id = element.getLong("id");
            double lat = element.getDouble("lat");
            double lon = element.getDouble("lon");

            LocalPlaceBusStop localPlaceBusStop = new LocalPlaceBusStop();
            localPlaceBusStop.setBusStopId(id);
            localPlaceBusStop.setLatitude(lon);
            localPlaceBusStop.setLongitude(lat);
            JSONObject tags = element.getJSONObject("tags");
            localPlaceBusStop.setName(tags.getString("name"));
            if (tags.has("bus")) {
                System.out.println(element);
            }
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
