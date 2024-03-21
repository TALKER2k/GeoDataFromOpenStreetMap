package su.vistar.Openstreetmaps;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.json.JSONArray;
import org.json.JSONObject;
import su.vistar.Openstreetmaps.models.LocalPlaceGate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddToDataBaseLiftGateInfo {
    public static void main(String[] args) {
        String overpassUrl = "https://overpass-api.de/api/interpreter";
        //примерно центр Воронежа
        double latitudeCurrentNode = 51.661535;
        double longitudeCurrentNode = 39.200287;
        //14 км радиуса, чтоб охватить весь Воронеж
        int radiusMeters = 15000;
        List<String> barriersType = new ArrayList<>();
        barriersType.add("lift_gate");
        barriersType.add("gate");
        for (String barrier : barriersType) {
            String query = "[out:json];" +
                    "(node[barrier=" + barrier + "](around:" + radiusMeters + "," +
                    latitudeCurrentNode + "," + longitudeCurrentNode + ");" +
                    "way[barrier=" + barrier + "](around:" + radiusMeters + "," +
                    latitudeCurrentNode + "," + longitudeCurrentNode + ");" +
                    "relation[barrier=" + barrier + "](around:" + radiusMeters + "," +
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
        try {
            databaseUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jsonResponse = new JSONObject(response);

        JSONArray elements = jsonResponse.getJSONArray("elements");
        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            long id = element.getLong("id");

            double lat = element.getDouble("lat");
            double lon = element.getDouble("lon");

            LocalPlaceGate localPlaceGate = new LocalPlaceGate();
            localPlaceGate.setGatesId(id);
            localPlaceGate.setLatitude(lon);
            localPlaceGate.setLongitude(lat);
            localPlaceGate.setPhoneNumber("+79001234567");

            JSONObject tags = element.getJSONObject("tags");
            if (tags.has("barrier")) {
                localPlaceGate.setName(tags.getString("barrier"));
            }
            persistDataBaseGate(localPlaceGate);
        }
    }

    private static void persistDataBaseGate(LocalPlaceGate localPlaceGate) {
        final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("br.com.fredericci.pu");
        final EntityManager entityManager = entityManagerFactory.createEntityManager();

        LocalPlaceGate entityLocalPlace = entityManager.find(LocalPlaceGate.class, localPlaceGate.getGatesId());

        entityManager.getTransaction().begin();
        if (entityLocalPlace == null) {
            entityManager.persist(localPlaceGate);
        } else {
            entityManager.merge(localPlaceGate);
        }

        entityManager.getTransaction().commit();
        entityManager.close();
        entityManagerFactory.close();
    }

    private static void databaseUpdate() throws LiquibaseException, SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/osm", "postgres", "postgres")) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            try (Liquibase liquibase = new Liquibase("db/changelog/master-changelog.yaml", new ClassLoaderResourceAccessor(), database)) {
                liquibase.update(new Contexts(), new LabelExpression());
            }
        } catch (SQLException | LiquibaseException e) {
            throw e;
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

