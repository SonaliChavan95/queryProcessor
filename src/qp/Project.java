package qp;

/**
 * @author SonaliChavan, AbhinavGarg
 *
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Project {
	static Connection conn;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("Reading Query 1 query...");

			MfStructure mf_structure = new MfStructure();
			mf_structure.readFile("./sample_queries/query1.txt");

			ConnectDB newConnection = new ConnectDB();
			newConnection.get_connection();
			// newConnection.retrieve();

			conn.close();
		} catch(Exception e) {

		}
	}
	
	
}
