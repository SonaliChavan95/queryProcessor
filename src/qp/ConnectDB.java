/**
 *
 */
package qp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class ConnectDB {
  Connection connection;
  public HashMap<String, String> infoSchema = new HashMap<String, String>();

  ConnectDB() {
    connection = null;
  }

  public Connection getConnection() {
    // JDBC driver name and database URL
    // final String JDBC_DRIVER = "org.postgresql.Driver";
    String username = System.getenv("DB_USER");
    String pass = System.getenv("DB_PASS");
    String tableName = System.getenv("TABLE");

    final String DB_URL = "jdbc:postgresql://localhost:5432/"+tableName;

    // Database credentials
    final String USER = username;
    final String PASS = pass;

    try {

      // Open a connection
      System.out.println("Connecting to database....");
      connection = DriverManager.getConnection(DB_URL, USER, PASS);

      if(connection != null) {
        System.out.println("Successful DB connection");
      } else {
        System.out.println("DB connection failed");
      }

      setInfoSchema();

    } catch(Exception e) {
      e.printStackTrace();
    }
    return connection;
  }

  void setInfoSchema() {
    String SQLQuery = "SELECT column_name, data_type "
        + "FROM information_schema.columns "
        + "WHERE table_name = 'sales'";

    try {
      Statement st = connection.createStatement();
      ResultSet rs = st.executeQuery(SQLQuery);

      String col, type;

      while(rs.next()) {
        col = rs.getString("column_name");
        type = columnDataType(rs.getString("data_type"));
        infoSchema.put(col, type);
      }
    }
    catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  private String columnDataType(String type) {
    switch(type) {
    case "character varying":
    case "character":
      return "String";
    case "integer":
      return "int";
    default:
      return "";
    }
  }
}
