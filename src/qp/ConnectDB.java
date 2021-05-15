/**
 * This class is responsible for making connection to the database
 * For the main Project.java, this file provides infoSchema regarding the 
 * sales table
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
  private final static String DB_NAME = "sales";
  private final static String USER = "postgres";
  private final static String PASS = "hello123";

  ConnectDB() {
    connection = null;
  }
  
  
  /**
   * connecting to the database and returning the connection object back
   * @return connection
   * back to the Project.java class
   */
  public Connection getConnection() {
    // JDBC driver name and database     
    final String DB_URL = "jdbc:postgresql://localhost:5432/"+DB_NAME;

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

  /**
   * sets info schema on the ConnectDB class so that we can fetch this info schema
   */
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
  
  // sets datatype of column based on info schema, i.e. String if character, int if integer.
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
