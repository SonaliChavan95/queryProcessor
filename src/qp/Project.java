package qp;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.Scanner;

/**
* This project is to build a query processing engine for Ad-Hoc OLAP queries.
* The query construct is based on an extended SQL syntax known as MF and EMF queries
* (i.e., MultiFeature and Extended MultiFeature queries).
* @author SonaliChavan, AbhinavGarg
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
		  String inputFile = "";
		  
		  // take input from the user whether he wants to perform mf/emf query
		  Scanner takeInput = new Scanner(System.in);
		  System.out.println("Please specify whether you want to perform an MF, EMF or standard query?");
		  System.out.println("Specify:\n1 for EMF\n2 for MF\n3 for Standard\n4 for Complex MF");
		  String queryType = takeInput.nextLine();
		  takeInput.close();
		  
		  String type;
		// Check for the input and read file accordingly.
		  if (queryType.toLowerCase().trim().equals("1")) {
			  inputFile = "./sample_queries/query1.txt";
			  type="EMF";
		  } else if (queryType.toLowerCase().trim().equals("2")) {
			  inputFile = "./sample_queries/query2.txt";
			  type="MF";
		  } else if (queryType.toLowerCase().trim().equals("3")){ 
			  inputFile = "./sample_queries/query3.txt";
			  type="Standard";
		  } else if (queryType.toLowerCase().trim().equals("4")){ 
			  inputFile = "./sample_queries/query4.txt";
			  type="Complex MF";
		  } else {
			  throw new Error("Invalid input. Please specify either 'MF' or 'EMF' or 'Standard'");
		  }
		  
		  System.out.println("You've selected: " + type);
		  
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
		  String code = codeGenerator.generateMainCode();
		
		  // Create ConnectDB class
		  writeToFile(connectDB, "./src/qp/output/ConnectDB.java");
		
		  // Create MfStruct class
		  writeToFile(mfStructClass, "./src/qp/output/MfStruct.java");
		
		  // CreateQuery Class
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