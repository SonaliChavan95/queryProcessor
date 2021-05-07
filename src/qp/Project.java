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
import java.util.HashMap;

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
			newConnection.getConnection();
			// newConnection.retrieve();

			CodeGenerator file_generator = new CodeGenerator();
			file_generator.generateCode(input_query, newConnection.infoSchema);

			conn.close();
		} catch(Exception e) {

		}
	}
}
