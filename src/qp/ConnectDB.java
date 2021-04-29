/**
 * 
 */
package qp;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * @author SonaliChavan, AbhinavGarg
 *
 */
public class ConnectDB {
	static Connection conn;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("Reading Query 1 query...");
			File myObj = new File("./sample_queries/query1.txt");

			if (myObj.exists()) {
				Scanner myReader = new Scanner(myObj);
				String input;
				while (myReader.hasNextLine()) {
					input = myReader.nextLine();
					 System.out.println(input);

					switch(input) {
					case "SELECT ATTRIBUTE(S):":
						input = myReader.nextLine();
						break;
					case "NUMBER OF GROUPING VARIABLES(n):":
						break;
					case "GROUPING ATTRIBUTES(V):":
						break;
					case "F-VECT([F]):":
						break;
					case "SELECT CONDITION-VECT([σ]):":
						break;
					case "HAVING_CONDITION(G):":
						break;
					}
				}


				myReader.close();
			} else {
				System.out.println("The file does not exist.");
			}
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		try {
			ConnectDB newConnection = new ConnectDB();
			conn = newConnection.get_connection();
//			newConnection.retrieve();
//			System.out.println(conn);

			conn.close();

			// newConnection.connect();
			// newConnection.retreive();

		} catch(Exception e) {

		}
	}
	
	public Connection get_connection() {
		// JDBC driver name and database URL
		final String JDBC_DRIVER = "org.postgresql.Driver";
		final String DB_URL = "jdbc:postgresql://localhost:5432/sales";

		// Database credentials
		final String USER = "postgres";
		final String PASS = "hello123";

		Connection connection = null;
		try {

			// Open a connection
			System.out.println("Connecting to database....");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);

			if(connection != null) {
				System.out.println("Successful DB connection");
			} else {
				System.out.println("DB connection failed");
			}

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
				ResultSet rs;
				//resultset object gets the set of values retrieved from the database
				boolean more;
				int i=1, j=0;
				Statement st = conn.createStatement();
				//statement created to execute the query
				String ret = "select * from sales";
				rs = st.executeQuery(ret);
				//executing the query
				more=rs.next();
				//checking if more rows available
				System.out.printf("%-8s","Customer  ");             //left aligned
				System.out.printf("%-7s","Product  ");              //left aligned
				System.out.printf("%-5s","Day    " +"");            //left aligned
				System.out.printf("%-10s","Month    ");          	//left aligned
				System.out.printf("%-5s","Year   ");                //left aligned
				System.out.printf("%-10s","State    ");          	//left aligned
				System.out.printf("%-5s%n","Quant  ");              //left aligned
				System.out.println("========  =======  =====  ========  =====  ========  =====");

				while(more) {
					System.out.printf("%-8s  ",rs.getString(1));            //left aligned
					System.out.printf("%-7s  ",rs.getString(2));            //left aligned
					System.out.printf("%5s  ",rs.getInt(3));             //right aligned
					System.out.printf("%8s  ",rs.getInt(4));            //right aligned
					System.out.printf("%5s  ",rs.getInt(5));             //right aligned
					System.out.printf("%-8s  ",rs.getString(6));            //right aligned
					System.out.printf("%5s%n",rs.getString(7));   		//rightaligned
					more = rs.next();
				}
			} catch(SQLException e) {
				System.out.println("Connection URL or username or password errors!");
				e.printStackTrace();
			}
		}
}
