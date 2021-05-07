package qp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class CodeGenerator {
	public void generateCode(InputQuery input_query, HashMap<String, String> infoSchema) {
		String importCommands =
				"import java.sql.*;\n"
				+ "import java.util.ArrayList;\n";

		// Generate MFStruct class
		String mfStructClass = generateMfStruct(input_query, infoSchema);

		String generatedCodeClass = "public class GeneratedCode {\n";
		String dbConnectionObj = "static Connection conn;\n";

		// array of MFStruct objects
		String mfStructObj = "static ArrayList<MfStruct> mfStruct = new ArrayList<MfStruct>();\n";

		String startQuery =
				"public static void main(String[] args) {\n"
				+ "try {\n"
				+ "System.out.println(\"----Generated Code-----\");\n"
				+ "ConnectDB newConnection = new ConnectDB();\n"
				+ "conn = newConnection.get_connection();\n"
				+ "String queryStr = \"SELECT * FROM sales\";\n"
				+ "Statement st = conn.createStatement();\n"
				+ "ResultSet rs;\n";

		String firstScan = populateGroupingAttributes(input_query, infoSchema);

		String secondScan = performOpOnGV(input_query, infoSchema);


		// Get all unique values of group by attributes
		String gByAttrs = "for(String var: mfStructObj) {\n"
				+"\n"
				+ "}";

//		for(String var: mfStructObj) {
//			if(infoSchema.containsKey(var)) {
//				mfStruct += "\t" + infoSchema.get(var) + " " + var + "; \n";
//			}
//		}
//







		String endCode = "			}\n"
				+ "\tconn.close();\n"
				+ "		} catch(Exception e) {\n"
				+ "\n"
				+ "		}\n"
				+ "	}\n"
				+ "}";

//		print(importCommands + startQuery + endCode);
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


	String generateMfStruct(InputQuery input_query, HashMap<String, String> infoSchema) {
		// input_query.V; // GA
		// input_query.F; // FV

		String mfStruct = "// MFStruct created using Grouping Attributes and F vectors \n "
				+ "class MfStruct { \n";

		// Add Grouping attributes in
		for(String var: input_query.V) {
			if(infoSchema.containsKey(var)) {
				mfStruct += "\t" + infoSchema.get(var) + " " + var + "; \n";
			}
		}

		// Add F vectors
		for(String var: input_query.F) {
			mfStruct += "\tint " + var + "; \n";
		}

		// Create a Constructor
		// MfStruct(String cust, String prod) {
		//	this.cust = cust;
		//	this.prod = prod;
		//	this.sum_1_quant = 0;
		//	this.sum_2_quant = 0;
		//}
		mfStruct += "\n\tMfStruct(";

		// For Construtor variable assignment
		String constructorArgs[] = new String[input_query.V.size()];
		String constrVarAssignment  = "";

		// For toString()
		String[] printVars = new String[input_query.V.size() + input_query.F.size()];

		int i = 0;

		for(String var: input_query.V) {
			printVars[i] = "this." + var;
			constructorArgs[i++] = infoSchema.get(var)+ " "  + var;

			constrVarAssignment += "\t\tthis." + var + " = " + var + ";\n";
		}

		for(String var: input_query.F) {
			constrVarAssignment += "\t\tthis." + var + " = 0;\n";

			printVars[i++] = "this." + var;
		}


		mfStruct +=  String.join(", ", constructorArgs) + ") {\n";
		mfStruct += constrVarAssignment;

		// Override toString()
		mfStruct += "\t}\n\n\tpublic String toString() { \n\t\t return (\n\t\t\t";

		mfStruct +=  String.join(" + \"\\t\"+ ", printVars);

		mfStruct += "\n\t\t);\n\t}\n}\n";

		System.out.println("----------MF Struct---------------");
		System.out.println(mfStruct);
		System.out.println("----------------------------------");

		return mfStruct;
	}


	String captalizeFirstLetter(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}


	String populateGroupingAttributes(InputQuery input_query, HashMap<String, String> infoSchema) {
		String abc =
				"// STEP 1: Populate Grouping attributes\n"
				+ "rs = st.executeQuery(queryStr);\n"
				+ "Set<String> uniqueGAttr = new HashSet<String>();\n"
				+ "MfStruct newRow;\n"
				+ "System.out.println(\"----STEP 1: Perform 0th Scan-------\");\n"
				+ "String uniqueKey;\n";

		String xyz = "";
		StringJoiner uniqKey = new StringJoiner(" + ", "(", ")");

		// Add Grouping attributes in
		for(String var: input_query.V) {
			if(infoSchema.containsKey(var)) {
				abc += "\t" + infoSchema.get(var) + " " + var + "; \n";
				xyz += "\t" + var + " = rs.get" + captalizeFirstLetter(infoSchema.get(var)) + "(\"" + var + "\");\n";
				uniqKey.add(var);
			}
		}

		abc += "while(rs.next()) {\n";
		abc += xyz;
		abc += "uniqueKey = " + uniqKey + ".toLowerCase();\n";
		abc += "if(!uniqueGAttr.contains(uniqueKey)) {\n"
				+ "\tuniqueGAttr.add(uniqueKey);\n"
				+ "\tnewRow = new MfStruct"
				+ uniqKey.toString().replace("+", ",")
				+ "; \n"
				+ "mfStruct.add(newRow);\n"
				+ "}}";

		System.out.println("----------First Scan Output---------------");
		System.out.println(abc);
		System.out.println("------------------------------------------");


		return abc;
	}

	String performOpOnGV(InputQuery input_query, HashMap<String, String> infoSchema) {
		// "// STEP 1: Perform operations on grouping variable\n";
		String abc = "", condi = "";

		for(int i = 1; i <= input_query.n; i++) {
			abc += "rs = st.executeQuery(queryStr);\n"
				+ "while(rs.next()) {;\n";


			System.out.println("------in progress------------");
			abc += "if(";
			for(String var: input_query.CV) {
				if(var.startsWith("" + i)) {

					// Parse condition vector for nth grouping variable
					// 1.state='NY' and 1.quant>30
					String[] arr = var.split(" ");
					for(String c1: arr) {
						switch(c1) {
						case "and":
							abc += " && "
							break;
						case "or":
							abc += " || ";
							break;
						default:
							condi = c1.replace(i + ".", "");
							if(condi.contains("<=")) {

							}
							if(condi.contains(">=")) {

							}
							if(condi.contains("!=") || condi.contains("<>")) {

							}
							if(condi.contains("=")) {
								String[] arr2 = condi.split("=");
								abc += "rs.getString(\"" + arr2[0] +"\").equals( " + arr2[1] +")";
							} else {
								// TODO
								abc += "";
							}
						}


						condi = var.replace(i + ".", "");
						String[] arr2 = condi.split("and")[0].split("=");
						if(condi.contains("=")) {
							abc += "if(rs.getString(\"" + arr[0] +"\").equals( " + arr[1] +")) {\n";
						} else {
							// TODO
							abc += "";
						}
					}




//					// Add Grouping attributes in
//					condi = "";
//					for(String var2: input_query.V) {
//						if(infoSchema.containsKey(var2)) {
//							condi += "\t" + var2 + " = rs.get" + captalizeFirstLetter(infoSchema.get(var2)) + "(\"" + var2 + "\");\n";
//						}
//					}

					abc += condi;
					abc += "for(MfStruct row: mfStruct) {\n";
					StringJoiner condi2 = new StringJoiner(" && ", "(", ")");

					for(String var2: input_query.V) {
						condi2.add("row." + var2 + ".equals(" + var2 + ")");
					}

					abc += "if" + condi2 + "{\n";

					// abc += var; // row.sum_1_quant += rs.getInt("quant");

					// System.out.println("======");
					for(String var2: input_query.F) {

						if(var2.contains(i+"")) {
							arr = var2.split("_");
							// System.out.println(arr[0]);
							switch(arr[0]) {
							case "sum":
								abc += "row." + var2 + " += rs.getInt(\"" + arr[2] + "\");\n";
								break;
							case "avg":

								break;
							case "min":
								abc += "row." + var2 + " = Math.min(row." + var2 + ", rs.getInt(\"" + arr[2] + "\");\n";
								break;
							case "max":
								abc += "row." + var2 + " = Math.max(row." + var2 + ", rs.getInt(\"" + arr[2] + "\");\n";
								break;
							case "count":
							}
						}
					}


					abc += "\n}\n}\n}\n}\n";



				}
			}
		}

		System.out.println("----------performOpOnGV---------------");
		System.out.println(abc);
		System.out.println("--------------------------------------");


		return abc;

	}
}
