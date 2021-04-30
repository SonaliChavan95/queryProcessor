package qp;

import java.io.FileWriter;
import java.io.IOException;

public class Generator {
	public void generateCode(InputQuery input_query) {
		String importCommands = "import java.sql.*;\nimport java.util.ArrayList;\n";

		String mfStruct = "public class MFstructure {\n";
		String structVar;
		for(String var: input_query.S) {
			 structVar = "String " + var + ";";

		}

		String startQuery = "public class GeneratedCode {\n"
				+ "static Connection conn;\n"
				+ "public static void main(String[] args) {\n"
				+ "try {\n"
				+ "System.out.println(\"----Generated Code-----\");\n"
				+ "ConnectDB newConnection = new ConnectDB();\n"
				+ "newConnection.get_connection();\n"
				+ "String queryStr = \"SELECT * FROM sales\";\n"
				+ "			Statement st = conn.createStatement();\n"
				+ "			ResultSet rs = st.executeQuery(queryStr);\n"
				+ "System.out.println(\"Testing overwrite Blaaaaa blaaa blaaa blaa\");\n"
				+ "while(rs.next()) {\n";

		String endCode = "			}\n"
				+ "\tconn.close();\n"
				+ "		} catch(Exception e) {\n"
				+ "\n"
				+ "		}\n"
				+ "	}\n"
				+ "}";

		print(importCommands + startQuery + endCode);
	}

	void print(String code) {
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
