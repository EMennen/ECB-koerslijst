import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVtoMariaDB {
    private static final String JDBC_URL = "jdbc:mariadb://localhost:3306/ECB";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "xxxxxx";

    public static void main(String[] args) {
        String csvFile = "C:\\temp\\exchange_rates.csv";
        List<String[]> records = readCSV(csvFile);
        insertIntoDatabase(records);
    }

    private static List<String[]> readCSV(String filePath) {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    private static void insertIntoDatabase(List<String[]> records) {
        String sql = "INSERT INTO exchange_rates (date, currency, rate) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String[] record : records) {
                LocalDate date = LocalDate.parse(record[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String currency = record[1];
                double rate = Double.parseDouble(record[2]);

                pstmt.setDate(1, Date.valueOf(date));
                pstmt.setString(2, currency);
                pstmt.setDouble(3, rate);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("Data successfully inserted into MariaDB.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
