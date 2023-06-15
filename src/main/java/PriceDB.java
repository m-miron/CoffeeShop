import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

public class PriceDB {
    private static final String QUERY = """
            SELECT Price
            FROM Beverage
            WHERE SizeType = ? AND DrinkType = ?
            """;
    private final DataSource dataSource;
    private final PreparedStatement preparedStatement;
    private Connection connection;

    public PriceDB() {
        Properties config = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/config.properties")) {
            config.load(input);
            /* A NEW PROPERTIES FILE WILL BE NEEDED WITH:
               db.url=jdbc:mysql://localhost:3306/database_name
               db.username=root
               db.password=your_password
            */
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file.", e);
        }

        String dbUrl = config.getProperty("db.url");
        String dbUsername = config.getProperty("db.username");
        String dbPassword = config.getProperty("db.password");

        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL(dbUrl);
        mysqlDataSource.setUser(dbUsername);
        mysqlDataSource.setPassword(dbPassword);
        this.dataSource = mysqlDataSource;

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(QUERY);
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed.", e);
        }
    }

    public double findPrice(SizeType size, DrinkType type) {
        double value = 0.0;
        try {
            preparedStatement.setString(1, size.toString());
            preparedStatement.setString(2, type.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    value = resultSet.getDouble("Price");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute query.", e);
        } finally {
            closeConnection();
        }
        return value;
    }

    public void closeConnection() {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close database resources.", e);
        }
    }
}
