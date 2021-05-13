package qp;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;

// TODO: LOGIC: 80%
// TODO: Check the type of input queries
// TODO: Figure out conditions for condition vector
// TODO: Handle average function
// TODO: Handle count aggregate function
// TODO: Take input interactively and through file
// TODO: Handle Having Condition 

// TODO: Styling: 20%
// TODO: Header Comment (Overall comments about the project) - 10 points
// TODO: Function Comments - 20 points
// TODO: Line Comment - 20 points 
// TODO: Meaningful Names (for functions, variables, etc.) - 10 points
// TODO: Strings – Left Justified - 15 points
// TODO: Numbers – Right Justified - 15 points
// TODO: Modular design (use of classes, methods/functions) - 10 points - Done


/**
* This project project is to build a query processing engine for Ad-Hoc OLAP queries. 
* The query construct is based on an extended SQL syntax known as MF and EMF queries 
* (i.e., MultiFeature and Extended MultiFeature queries).
* @author  @author SonaliChavan, AbhinavGarg
* @version 1.0
* @since   2021-04-29
*/
public class Project {
	static Connection conn;
	/**
	  * This is the main method which makes use of addNum method.
	  * @param args Unused.
	  * @return Nothing.
	  * @exception Exception error.
	  */
	public static void main(String[] args) {
		try {
			String inputFile = "./sample_queries/query1.txt";
			System.out.println("Reading "+ inputFile);

			// Read the input query
			InputQuery input_query = new InputQuery();
			input_query.readFile(inputFile);
			
			// Make the connection and take out schema information
			ConnectDB newConnection = new ConnectDB();
			conn = newConnection.getConnection();
			

			// Generate the code
			CodeGenerator codeGenerator = new CodeGenerator(input_query, newConnection.infoSchema);
			
			String mfStructClass = codeGenerator.generateMfStructClass(); 
			String connectDB = codeGenerator.generateConnectDB();
			String code = codeGenerator.generateCode();
			
			// create MfStruct class
			writeToFile(connectDB, "./src/qp/output/ConnectDB.java");
			
			// create MfStruct class
			writeToFile(mfStructClass, "./src/qp/output/MFStruct.java");
			
			// createQuery Class
			writeToFile(code, "./src/qp/output/Query.java");
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This function is writing out the generated code in the file.
	 * 
	 * This function expects two arguments
	 * @param code is the generated code that we want to write into the file.
	 * @param fileName is the name of the file  where we want to print the code into.
	 * @return nothing
	 * @exception IOException On input error.
	 * @see IOException
	 */
	static void writeToFile(String code, String fileName) {
		try {
			// Create a file with filename
			FileWriter myWriter = new FileWriter(fileName);
			
			// Write code into the file
			myWriter.write(code);
			
			// close file writer and print a message indicating that file has been generated
			myWriter.close();
			System.out.println("Successfully created file in: "+ "\"" + fileName + "\"");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}