/**
 *
 */
package qp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
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
		final String DB_URL = "jdbc:postgresql://localhost:5432/sales";

		// Database credentials
		final String USER = "postgres";
		final String PASS = "hello123";

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

	//Function to retrieve from the database and process on the resultset received
	void retrieve() {
		try {
			//Connection con = DriverManager.getConnection(url, usr, pwd);
			//connect to the database using the password and username
			System.out.println("Success connecting server!");

			System.out.printf("%-8s","Customer  ");             //left aligned
			System.out.printf("%-7s","Product  ");              //left aligned
			System.out.printf("%-5s","Day    " +"");            //left aligned
			System.out.printf("%-10s","Month    ");          	//left aligned
			System.out.printf("%-5s","Year   ");                //left aligned
			System.out.printf("%-10s","State    ");          	//left aligned
			System.out.printf("%-5s%n","Quant  ");              //left aligned
			System.out.println("========  =======  =====  ========  =====  ========  =====");

			Statement st = connection.createStatement(); //statement created to execute the query
			//resultset object gets the set of values retrieved from the database
			ResultSet rs = st.executeQuery("select * from sales"); //executing the query
			boolean more = rs.next(); //checking if more rows available

			while(more) {
				System.out.printf("%-8s  ", rs.getString(1));            //left aligned
				System.out.printf("%-7s  ", rs.getString(2));            //left aligned
				System.out.printf("%5s  ", rs.getInt(3));             //right aligned
				System.out.printf("%8s  ", rs.getInt(4));            //right aligned
				System.out.printf("%5s  ", rs.getInt(5));             //right aligned
				System.out.printf("%-8s  ", rs.getString(6));            //right aligned
				System.out.printf("%5s%n", rs.getString(7));   		//rightaligned
				more = rs.next();
			}
		} catch(SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
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
//			System.out.println("-------Sales Table Information----------");
//			System.out.println(Collections.singletonList(infoSchema));
//			System.out.println("----------------------------------------");
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
