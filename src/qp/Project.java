package qp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
 * @author SonaliChavan, AbhinavGarg
 *
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Project {
	static Connection conn;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("Reading Query 1 query...");

			InputQuery input_query = new InputQuery();
			input_query.readFile("./sample_queries/query1.txt");

			ConnectDB newConnection = new ConnectDB();
			newConnection.get_connection();
			// newConnection.retrieve();

			Generator file_generator = new Generator();
			file_generator.generateCode(input_query);

			conn.close();
		} catch(Exception e) {

		}
	}
}
