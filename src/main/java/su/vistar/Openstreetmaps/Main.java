package su.vistar.Openstreetmaps;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        // new function for test
        try {
            String overpassEndpoint = "http://overpass-api.de/api/interpreter";
            String query = "[out:json];" +
                    "(node[barrier=gate](around:10000,51.6589507,39.2032023);" +
                    "way[barrier=lift_gate](around:10000,51.6589507,39.2032023);" +
                    "relation[barrier=gate](around:10000,51.6589507,39.2032023););" +
                    "out;";
            String query2 = "[out:json];" +
                    "(node[barrier=gate](around:10000,51.6589507,39.2032023);" +
                    "way[barrier=gate](around:10000,51.6589507,39.2032023);" +
                    "relation[barrier=gate](around:10000,51.6589507,39.2032023););" +
                    "out;";

            URL url = new URL(overpassEndpoint + "?data=" + query);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();

            // Далее обработайте JSON-ответ и извлеките необходимую информацию о шлагбаумах
            // Например, можно использовать библиотеку для работы с JSON, такую как Gson или org.json

            System.out.println(response.toString()); // Вывод ответа
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
