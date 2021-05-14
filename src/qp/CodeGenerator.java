package qp;

import java.util.HashMap;
import java.util.StringJoiner;

public class CodeGenerator {
	private InputQuery input_query;
	private HashMap<String, String> infoSchema;

	public CodeGenerator(InputQuery inputQ, HashMap<String, String> infoSchema) {
		this.input_query = inputQ;
		this.infoSchema = infoSchema;
	}

	public String generateConnectDB() {
		String connectDB = "/**\n"
				+ " *\n"
				+ " */\n"
				+ "package qp.output;\n"
				+ "\n"
				+ "import java.sql.Connection;\n"
				+ "import java.sql.DriverManager;\n"
				+ "import java.sql.ResultSet;\n"
				+ "import java.sql.SQLException;\n"
				+ "import java.sql.Statement;\n"
				+ "import java.util.HashMap;\n"
				+ "\n"
				+ "public class ConnectDB {\n"
				+ "	Connection connection;\n"
				+ "	public HashMap<String, String> infoSchema = new HashMap<String, String>();\n"
				+ "\n"
				+ "	ConnectDB() {\n"
				+ "		connection = null;\n"
				+ "	}\n"
				+ "\n"
				+ "	public Connection getConnection() {\n"
				+ "		// JDBC driver name and database URL\n"
				+ "		// final String JDBC_DRIVER = \"org.postgresql.Driver\";\n"
				+ "		String username = System.getenv(\"DB_USER\");\n"
				+ "		String pass = System.getenv(\"DB_PASS\");\n"
				+ "		String tableName = System.getenv(\"TABLE\");\n"
				+ "		\n"
				+ "		final String DB_URL = \"jdbc:postgresql://localhost:5432/\"+tableName;\n"
				+ "		\n"
				+ "		// Database credentials\n"
				+ "		final String USER = username;\n"
				+ "		final String PASS = pass;\n"
				+ "\n"
				+ "		try {\n"
				+ "\n"
				+ "			// Open a connection\n"
				+ "			System.out.println(\"Connecting to database....\");\n"
				+ "			connection = DriverManager.getConnection(DB_URL, USER, PASS);\n"
				+ "\n"
				+ "			if(connection != null) {\n"
				+ "				System.out.println(\"Successful DB connection\");\n"
				+ "			} else {\n"
				+ "				System.out.println(\"DB connection failed\");\n"
				+ "			}\n"
				+ "\n"
				+ "			setInfoSchema();\n"
				+ "\n"
				+ "		} catch(Exception e) {\n"
				+ "			e.printStackTrace();\n"
				+ "		}\n"
				+ "		return connection;\n"
				+ "	}\n"
				+ "\n"
				+ "	void setInfoSchema() {\n"
				+ "		String SQLQuery = \"SELECT column_name, data_type \"\n"
				+ "				+ \"FROM information_schema.columns \"\n"
				+ "				+ \"WHERE table_name = 'sales'\";\n"
				+ "\n"
				+ "		try {\n"
				+ "			Statement st = connection.createStatement();\n"
				+ "			ResultSet rs = st.executeQuery(SQLQuery);\n"
				+ "\n"
				+ "			String col, type;\n"
				+ "\n"
				+ "			while(rs.next()) {\n"
				+ "				col = rs.getString(\"column_name\");\n"
				+ "				type = columnDataType(rs.getString(\"data_type\"));\n"
				+ "				infoSchema.put(col, type);\n"
				+ "			}\n"
				+ "		}\n"
				+ "		catch (SQLException ex) {\n"
				+ "			ex.printStackTrace();\n"
				+ "		}\n"
				+ "	}\n"
				+ "\n"
				+ "	private String columnDataType(String type) {\n"
				+ "		switch(type) {\n"
				+ "		case \"character varying\":\n"
				+ "		case \"character\":\n"
				+ "			return \"String\";\n"
				+ "		case \"integer\":\n"
				+ "			return \"int\";\n"
				+ "		default:\n"
				+ "			return \"\";\n"
				+ "		}\n"
				+ "	}\n"
				+ "}\n"
				+ "";
		return connectDB;
	}

	public String generateMfStructClass() {
		String mfStructClass = generateMfStruct(this.input_query, this.infoSchema);
		return mfStructClass;
	}

	public String generateCode() {
		InputQuery input_query = this.input_query;
		HashMap<String, String> infoSchema = this.infoSchema;

		String importCommands = "package qp.output;\n"
				+ "import java.sql.*;\n"
				+ "import java.util.ArrayList;\n"
				+ "import java.util.HashSet;\n"
				+ "import java.util.Set;\n"
				+ "import java.sql.Connection;\n\n";

		// Generate MFStruct class


		String generatedCodeClass = "public class Query {\n";
		String dbConnectionObj = "\tstatic Connection conn;\n";

		// array of MFStruct objects
		String mfStructObj = "\tstatic ArrayList<MfStruct> mfStruct = new ArrayList<MfStruct>();\n";

		String startQuery = "\tpublic static void main(String[] args) {\n"
				+ "\t\ttry {\n"
				+ "\t\t\tSystem.out.println(\"----Generated Code-----\");\n"
				+ "\t\t\tConnectDB newConnection = new ConnectDB();\n"
				+ "\t\t\tconn = newConnection.getConnection();\n"
				+ "\t\t\tString queryStr = \"SELECT * FROM sales\";\n"
				+ "\t\t\tStatement st = conn.createStatement();\n"
				+ "\t\t\tResultSet rs;\n";

		String firstScan = populateGroupingAttributes(input_query, infoSchema);

		String furtherScans = performOpOnGV(input_query, infoSchema);

		String printOutput = generatePrintOutput(input_query);

		String endCode = "\n"
				+ "\t\t\t"
				+ "conn.close();\n"
				+ "\t\t} catch(Exception e) {\n\n"
				+ "\t\t}\n"
				+ "\t}\n}";

		String code = importCommands + generatedCodeClass + dbConnectionObj + mfStructObj + startQuery
				+ firstScan + furtherScans + printOutput + endCode;

		// write to file "/Output/FinalQuery.java"
		return code;
	}

	String generateMfStruct(InputQuery input_query, HashMap<String, String> infoSchema) {
		// input_query.V; - GA
		// input_query.F; - FV

		String mfStruct = "\n// MFStruct created using Grouping Attributes and F vectors \n"
				+ "package qp.output;\n\n"
				+ "class MfStruct { \n";

		// Add Grouping attributes in
		for (String var : input_query.V) {
			if (infoSchema.containsKey(var)) {
				mfStruct += "\t" + infoSchema.get(var) + " " + var + ";\n";
			}
		}

		// Add F vectors
		for (String var : input_query.F) {
			mfStruct += "\tint " + var + ";\n";
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
			if (var.contains("min")) {
				constrVarAssignment += "\t\t" + "this." + var + " = Integer.MAX_VALUE;\n";
			}
		}

		i = 0;
		for (String var : input_query.S) {
			printVars[i++] = "this." + var;
		}

		mfStruct += String.join(", ", constructorArgs) + ") {\n";
		mfStruct += constrVarAssignment;

//		// Override toString()
//		mfStruct += "\t}\n\n\tpublic String toString() { \n\t\t return (\n\t\t\t";
//
//		mfStruct += String.join(" + \"\\t\"+ ", printVars);
//		 "\n\t\t);\n\t}"
		mfStruct += "\t}\n}\n";

		return mfStruct;
	}

	String captalizeFirstLetter(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	// STEP 1
	String populateGroupingAttributes(InputQuery input_query, HashMap<String, String> infoSchema) {
		String groupingAttrCode = "\t\t\t"
				+ "// STEP 1: Populate Grouping attributes\n"
				+ "\t\t\t"
				+ "rs = st.executeQuery(queryStr);\n"
				+ "\t\t\t"
				+ "Set<String> uniqueGAttr = new HashSet<String>();\n"
				+ "\t\t\t"
				+ "MfStruct newRow;\n"
				+ "\t\t\t"
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
		groupingAttrCode += "\t\t\t\t"
				+ "if(!uniqueGAttr.contains(uniqueKey)) {\n"
				+ "\t\t\t\t\t"
				+ "uniqueGAttr.add(uniqueKey);\n"
				+ "\t\t\t\t\t"
				+ "newRow = new MfStruct"
				+ uniqKey.toString().replace("+", ",")
				+ "; \n\t\t\t\t\t"
				+ "mfStruct.add(newRow);\n\t\t\t\t"
				+ "}\n\t\t\t}\n";

		return groupingAttrCode;
	}

	// STEP 2: run loop on selected aggregate functions
	String performOpOnGV(InputQuery input_query, HashMap<String, String> infoSchema) {
		// "\n// STEP 1: Perform operations on grouping variable\n";
		String operationOnGV = "", condi = "", gvCondition = "";
		StringJoiner avgs = new StringJoiner("\t\t\t");

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

							switch (arr[0]) {
							case "sum":
								operationOnGV += "row." + var2 + " += rs.getInt(\"" + arr[2] + "\");";
								break;
							case "avg":
								avgs.add("\trow." + var2 + " = " + "row." + var2.replace("avg", "count") + " > 0 ?" + " row." + var2.replace("avg", "sum") + " / " + "row." + var2.replace("avg", "count") + " : 0" + ";\n");
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
								operationOnGV += "row." + var2 + "++;";
							}
							operationOnGV += "\n";
						}
					}

					operationOnGV += "\n\t\t\t\t\t\t}\n"
							+ "\t\t\t\t\t}\n\t\t\t\t"
							+ "}\n"
							+ "\t\t\t}\n";

				}
			}
		}

		// Calculate avg
		operationOnGV += "\n\t\t\tfor(MfStruct row: mfStruct) {\n\t\t\t";
		operationOnGV += avgs.toString();
		operationOnGV += "\t\t\t}\n";
		return operationOnGV;
	}

	String generatePrintOutput(InputQuery input_query) {
		String selectAttrs = "";
		String separator = "";
		String output = "";
		String values = "";

		// Table Header
		for (String var : input_query.S) {
			String attr = "";
			if (var.equals("cust")) {
				attr = "\"%-10s\",\"Customer\"";
				separator += "========= ";
			} else if (var.equals("prod")) {
				attr = "\"%-10s\",\"Product\"";
				separator += "========= ";
			} else {
				attr = "\"%-12s\",\"" + var + "  \"";
				separator += "============ ";
			}
			selectAttrs += "\t\t\tSystem.out.printf("+attr+");\n";
		}

		output += "\n\t\t\t" + "//Scan mf struct and print out the results\n";
		output += selectAttrs
				+ "\t\t\tSystem.out.println(\""+"\\n"+ separator +"\");\n\n";

		for (String var : input_query.S) {
			String value = "";
			if (var.equals("cust")) {
				value = "\"%-10s\", row."+ var;
			} else if (var.equals("prod")) {
				value = "\"%-10s\", row."+ var;
			} else {
				value = "\"%12s\", row."+ var;
			}
			values += "\t\t\t\tSystem.out.printf("+value+");\n";
		}

		output += "\t\t\t"
			   + "for(MfStruct row: mfStruct) {\n"
			   + values
			   + "\t\t\t\tSystem.out.print('\\n');\n"
			   + "\t\t\t" + "}\n";
		return output;
	}
}
