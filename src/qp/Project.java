package qp;

/**
 * @author SonaliChavan, AbhinavGarg
 *
 */

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;


public class Project {
	static Connection conn;

	/**
	 * @param args
	 */

	// TODO: LOGIC: 80%
	// TODO: Check the type of input queries
	// TODO: Figure out conditions for condition vector
	// TODO: Handle average and count aggregate functions
	// TODO: Take input interactively and through file
	
	
	// TODO: Styling: 20%
	// TODO: Header Comment (Overall comments about the project) - 10 points
	// TODO: Function Comments - 20 points
	// TODO: Line Comment - 20 points 
	// TODO: Meaningful Names (for functions, variables, etc.) - 10 points
	// TODO: Strings – Left Justified - 15 points
	// TODO: Numbers – Right Justified - 15 points
	// TODO: Modular design (use of classes, methods/functions) - 10 points
	
	public static void main(String[] args) {
		try {
			String inputFile = "./sample_queries/query1.txt"; 
			System.out.println("Reading "+ inputFile);

			// Read the input query
			InputQuery input_query = new InputQuery();
			input_query.readFile(inputFile);
			
			// Make the connection
			ConnectDB newConnection = new ConnectDB();
			newConnection.getConnection();
			
			// Generate the code
			CodeGenerator file_generator = new CodeGenerator();
			String code = file_generator.generateCode(input_query, newConnection.infoSchema);
			
			// Write the code into file
			writeToFile(code, "./Output/GeneratedCode.java");
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	static void writeToFile(String code, String fileName) {
		try {
			// Create a file with filename
			FileWriter myWriter = new FileWriter(fileName);
			
			// Write code into the file
			myWriter.write(code);
			
			// close file writer and print a message indicating that file has been generated
			myWriter.close();
			System.out.println("Successfully created file in Java");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}