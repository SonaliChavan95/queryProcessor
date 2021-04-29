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

			String finalQuery = "import java.sql.*;\n"
					+ "import java.util.ArrayList;\n"
					+ "public class GeneratedCode {\n"
					+ "static Connection conn;\n"
					+ "public static void main(String[] args) {\n"
					+ "try {\n"
					+ "System.out.println(\"----Generated Code-----\");\n"
					+ "ConnectDB newConnection = new ConnectDB();\n"
					+ "newConnection.get_connection();\n"
					+ "String queryStr = \"SELECT * FROM sales\";\n"
					+ "			Statement st = conn.createStatement();\n"
					+ "			ResultSet rs = st.executeQuery(queryStr);\n"
					+ "System.out.println(\"Testing overwrite\");\n"
					+ "while(rs.next()) {\n"
					+ "				\n"
					+ "			}"
					+ "conn.close();\n"
					+ "		} catch(Exception e) {\n"
					+ "\n"
					+ "		}\n"
					+ "	}\n"
					+ "}";




			print(finalQuery);
			conn.close();
		} catch(Exception e) {

		}
	}


	static void print(String code) {
		try {
			FileWriter myWriter = new FileWriter("./Output/FinalQuery.java");
			myWriter.write(code);
			myWriter.close();
			System.out.println("Successfully created file in Java");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
