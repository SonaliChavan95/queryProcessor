package qp;

import java.sql.DriverManager;
import java.util.HashMap;
import java.util.StringJoiner;

public class CodeGenerator {
	public String generateCode(InputQuery input_query, HashMap<String, String> infoSchema) {
		String importCommands = "import java.sql.*;\n" + "import java.util.ArrayList;\n" + "import java.util.HashSet;\n"
				+ "import java.util.Set;\n" + "import java.sql.Connection;\n\n";

		// Generate MFStruct class
		String mfStructClass = generateMfStruct(input_query, infoSchema);

		String generatedCodeClass = "public class GeneratedCode {\n";
		String dbConnectionObj = "\tstatic Connection conn;\n";

		// array of MFStruct objects
		String mfStructObj = "\tstatic ArrayList<MfStruct> mfStruct = new ArrayList<MfStruct>();\n";

		String startQuery = "\tpublic static void main(String[] args) {\n" + "\t\ttry {\n"
				+ "\t\t\tSystem.out.println(\"----Generated Code-----\");\n"
				+ "\t\t\tConnectDB newConnection = new ConnectDB();\n" + "\t\t\tconn = newConnection.getConnection();\n"
				+ "\t\t\tString queryStr = \"SELECT * FROM sales\";\n"
				+ "\t\t\tStatement st = conn.createStatement();\n" + "\t\t\tResultSet rs;\n";

		String firstScan = populateGroupingAttributes(input_query, infoSchema);

		String furtherScans = performOpOnGV(input_query, infoSchema);

		String printOutput = generatePrintOutput(input_query);

		String endCode = "\n" + "\t\t\t" + "conn.close();\n" + "\t\t} catch(Exception e) {\n" + "\n" + "\t\t}\n"
				+ "\t}\n" + "}";

		String code = importCommands + mfStructClass + generatedCodeClass + dbConnectionObj + mfStructObj + startQuery
				+ firstScan + furtherScans + printOutput + endCode;

		// write to file "/Output/FinalQuery.java"
		return code;
	}

	String generateMfStruct(InputQuery input_query, HashMap<String, String> infoSchema) {
		// input_query.V; // GA
		// input_query.F; // FV

		String mfStruct = "// MFStruct created using Grouping Attributes and F vectors \n" + "class MfStruct { \n";

		// Add Grouping attributes in
		for (String var : input_query.V) {
			if (infoSchema.containsKey(var)) {
				mfStruct += "\t" + infoSchema.get(var) + " " + var + "; \n";
			}
		}

		// Add F vectors
		for (String var : input_query.F) {
			mfStruct += "\tint " + var + "; \n";
		}

		// Create a Constructor
		// MfStruct(String cust, String prod) {
		// this.cust = cust;
		// this.prod = prod;
		// this.sum_1_quant = 0;
		// this.sum_2_quant = 0;
		// }
		mfStruct += "\n\tMfStruct(";

		// For Construtor variable assignment
		String constructorArgs[] = new String[input_query.V.size()];
		String constrVarAssignment = "";

		// For toString()
		String[] printVars = new String[input_query.S.size()];

		int i = 0;

		for (String var : input_query.V) {
			constructorArgs[i++] = infoSchema.get(var) + " " + var;

			constrVarAssignment += "\t\t" + "this." + var + " = " + var + ";\n";
		}

		for (String var : input_query.F) {
//			int defaultValue = 0;
			if (var.contains("min")) {
//				defaultValue = Integer.MAX_VALUE;
				constrVarAssignment += "\t\t" + "this." + var + " = "+ Integer.MAX_VALUE + ";\n";
			} 
			
		}

		i = 0;
		for (String var : input_query.S) {
			printVars[i++] = "this." + var;
		}

		mfStruct += String.join(", ", constructorArgs) + ") {\n";
		mfStruct += constrVarAssignment;

		// Override toString()
		mfStruct += "\t}\n\n\tpublic String toString() { \n\t\t return (\n\t\t\t";

		mfStruct += String.join(" + \"\\t\"+ ", printVars);

		mfStruct += "\n\t\t);\n\t}\n}\n";

		return mfStruct;
	}

	String captalizeFirstLetter(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	String populateGroupingAttributes(InputQuery input_query, HashMap<String, String> infoSchema) {
		String groupingAttrCode = "\t\t\t" + "// STEP 1: Populate Grouping attributes\n" + "\t\t\t"
				+ "rs = st.executeQuery(queryStr);\n" + "\t\t\t" + "Set<String> uniqueGAttr = new HashSet<String>();\n"
				+ "\t\t\t" + "MfStruct newRow;\n" + "\t\t\t"
				+ "System.out.println(\"----STEP 1: Perform 0th Scan-------\");\n" 
				+ "\t\t\t" 
				+ "String uniqueKey;\n";

		String xyz = "";
		StringJoiner uniqKey = new StringJoiner(" + ", "(", ")");

		// Add Grouping attributes in
		for (String var : input_query.V) {
			if (infoSchema.containsKey(var)) {
				groupingAttrCode += "\t\t\t" + infoSchema.get(var) + " " + var + "; \n";
				xyz += "\t\t\t\t" + var + " = rs.get" + captalizeFirstLetter(infoSchema.get(var)) + "(\"" + var
						+ "\");\n";
				uniqKey.add(var);
			}
		}

		groupingAttrCode += "\n\t\t\twhile(rs.next()) {\n";
		groupingAttrCode += xyz;
		groupingAttrCode += "\t\t\t\t" + "uniqueKey = " + uniqKey + ".toLowerCase();\n";
		groupingAttrCode += "\t\t\t\t" + "if(!uniqueGAttr.contains(uniqueKey)) {\n" + "\t\t\t\t\t"
				+ "uniqueGAttr.add(uniqueKey);\n" + "\t\t\t\t\t" + "newRow = new MfStruct"
				+ uniqKey.toString().replace("+", ",") + "; \n" + "\t\t\t\t\t" + "mfStruct.add(newRow);\n" + "\t\t\t\t"
				+ "}\n\t\t\t" + "}\n";

		return groupingAttrCode;
	}

	// run loop on selected aggregate functions
	String performOpOnGV(InputQuery input_query, HashMap<String, String> infoSchema) {
		// "\n// STEP 1: Perform operations on grouping variable\n";
		String operationOnGV = "", condi = "", gvCondition = "";

		for (int i = 1; i <= input_query.n; i++) {
			operationOnGV += "\n\t\t\t" + "rs = st.executeQuery(queryStr);\n" + "\t\t\t" + "while(rs.next()) {\n";

			for (String var : input_query.CV) {
				if (var.startsWith("" + i)) {
					// Parse condition vector for nth grouping variable
					// 1.state='NY' and 1.quant>30
					String[] arr = var.split(" ");
					for (String c1 : arr) {
						switch (c1) {
						case "and":

//							operationOnGV += " && ";
							break;
						case "or":
//							operationOnGV += " || ";
							break;
						default:
							condi = c1.replace(i + ".", "");
//							if(condi.contains("<=")) {
//
//							}
//							if(condi.contains(">=")) {
//
//							}
//							if(condi.contains("!=") || condi.contains("<>")) {
//
//							}
							String[] arr2 = condi.split("=");
							if (condi.contains("=")) {
								arr2 = condi.split("=");
								gvCondition = "rs.getString(\"" + arr2[0] + "\").equals(" + arr2[1] + ")";
							}
						}
					}

					// Add Grouping attributes in

					operationOnGV += "\t\t\t\t" + "if(" + gvCondition + ") {\n";

					condi = "";
					for (String var2 : input_query.V) {
						if (infoSchema.containsKey(var2)) {
							condi += "\t\t\t\t\t" + var2 + " = rs.get" + captalizeFirstLetter(infoSchema.get(var2))
									+ "(\"" + var2 + "\");\n";
						}
					}

					operationOnGV += condi;

					operationOnGV += "\t\t\t\t\t" + "for(MfStruct row: mfStruct) {\n";
					StringJoiner condi2 = new StringJoiner(" && ", "(", ")");

					for (String var2 : input_query.V) {
						condi2.add("row." + var2 + ".equals(" + var2 + ")");
					}

					operationOnGV += "\t\t\t\t\t\t" + "if" + condi2 + "{\n";

					for (String var2 : input_query.F) {

						if (var2.contains(i + "")) {
							arr = var2.split("_");
							operationOnGV += "\t\t\t\t\t\t\t";
							
							operationOnGV += "if (row." + var2 + " == 0) row." + var2 + " = 0;\n";
							operationOnGV += "\t\t\t\t\t\t\t";
							
							switch (arr[0]) {
							case "sum":
								operationOnGV += "row." + var2 + " += rs.getInt(\"" + arr[2] + "\");";
								break;
							case "avg":

								break;
							case "min":
								operationOnGV += "row." + var2 + " = Math.min(row." + var2 + ", rs.getInt(\"" + arr[2]
										+ "\"));";
								break;
							case "max":
								operationOnGV += "row." + var2 + " = Math.max(row." + var2 + ", rs.getInt(\"" + arr[2]
										+ "\"));";
								break;
							case "count":
								operationOnGV += "row." + var2 + " = count++;";
							}
							operationOnGV += "\n";
						}
					}

					operationOnGV += "\n" + "\t\t\t\t\t\t" + "}\n" + "\t\t\t\t\t" + "}\n" + "\t\t\t\t" + "}\n"
							+ "\t\t\t" + "}\n";

				}
			}
		}
		return operationOnGV;
	}

	String generatePrintOutput(InputQuery input_query) {
		String selectAttrs = "";
		String separator = "";
		String output = "";

		// Table Header
		for (String var : input_query.S) {
			String attr = "";
			if (var.equals("cust")) {
				attr = "\"%-8s\",\"Customer  \"";
				separator += "========  ";
			} else if (var.equals("prod")) {
				attr = "\"%-7s\",\"Product   \"";
				separator += "========  ";
			} else {
				attr = "\"%-10s\",\"" + var + "  \"";
				separator += "===========  ";
			}
			selectAttrs += "\t\t\tSystem.out.printf("+attr+");\n";
		}
	
		output += "\n\t\t\t" + "//Scan mf struct and print out the results\n";
		output += selectAttrs
				+ "\t\t\tSystem.out.println(\""+"\\n"+ separator +"\");\n\n";
		

		output += "\t\t\t" 
			   + "for(MfStruct row: mfStruct) {\n" 
			   + "\t\t\t\t"
			   + "System.out.println(row.toString());\n"
			   + "\t\t\t" + "}\n";
		return output;
	}
}
