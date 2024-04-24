//package su.vistar.Openstreetmaps.RouteServices;
//
//import su.vistar.Openstreetmaps.models.RouteBus.*;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.util.List;
//
//public class OSMDataProcessor {
//    private static final String DB_URL = "jdbc:postgresql://localhost:5434/routeosm";
//    private static final String USER = "postgres";
//    private static final String PASSWORD = "postgres";
//
//    public static void main(String[] args) {
//        // Подключение к базе данных PostgreSQL
//        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
//            processData(conn);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void processData(Connection conn) throws SQLException {
//        // Получение данных из OSM и обработка
//
//        // Создание таблиц и вставка данных
//
//        createTables(conn);
//        insertData(conn, routes, stops, routeStopRelations, lineStrings, points);
//    }
//
//    private static void createTables(Connection conn) throws SQLException {
//        try (PreparedStatement statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS Route (id BIGINT PRIMARY KEY, name VARCHAR(255) NOT NULL, from VARCHAR(255) NOT NULL, to VARCHAR(255) NOT NULL, network VARCHAR(255), operator VARCHAR(255), public_transport_version INT, ref VARCHAR(255), route VARCHAR(255))")) {
//            statement.executeUpdate();
//        }
//
//        // Создайте таблицы для Stop, RouteStop, LineString, Point
//    }
//
//    private static void insertData(Connection conn, List<Route> routes, List<Stop> stops, List<RouteStop> routeStopRelations, List<LineString> lineStrings, List<Point> points) throws SQLException {
//        // Вставка данных в соответствующие таблицы
//        try (PreparedStatement statement = conn.prepareStatement("INSERT INTO Route (id, name, from, to, network, operator, public_transport_version, ref, route) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
//            // Вставка данных в таблицу Route
//        }
//
//        // Вставьте данные в таблицы Stop, RouteStop, LineString, Point
//    }
//}
