package qp;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class CodeGenerator {
  static private InputQuery inputQuery;
  static private HashMap<String, String> infoSchema;
  static private Map<String, String> aggregateFunctions = new HashMap<String, String>();

  public CodeGenerator(InputQuery inputQuery, HashMap<String, String> infoSchema) {
    CodeGenerator.inputQuery = inputQuery;
    CodeGenerator.infoSchema = infoSchema;
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
    StringBuilder mfStruct = new StringBuilder();
    StringJoiner classVars = new StringJoiner(";\n", "", ";");
    StringJoiner constructorArgs = new StringJoiner(", ", "(", ")");
    StringJoiner constructorVars = new StringJoiner(";\n", "", ";");

    /**
    * ----------------------------------------
    * Declare Variables in MfStruct class
    * ----------------------------------------
    */
    // Add Grouping attributes in
    for (String var : inputQuery.V) {
      if (infoSchema.containsKey(var)) {
        classVars.add("\t" + infoSchema.get(var) + " " + var);
      }
    }
    // Add F vectors
    for (String var : inputQuery.F) {
    	if(var.startsWith("avg")) {
    		classVars.add("\tdouble " + var);
    	} else {
    		classVars.add("\tint " + var);
    	}
    }

    /**
    * ----------------------------------------
    * Create constructor MfStruct class
    * MfStruct(String cust, String prod) {
    *   this.cust = cust;
    *   this.prod = prod;
    *   this.sum_1_quant = 0;
    *   this.sum_2_quant = 0; }
    * ----------------------------------------
    */

    for (String var : inputQuery.V) {
      constructorArgs.add(infoSchema.get(var) + " " + var);
      constructorVars.add("\t\tthis." + var + " = " + var);
    }

    for (String var : inputQuery.F) {
      if (var.contains("min")) {
        constructorVars.add("\t\tthis." + var + " = Integer.MAX_VALUE");
      }
      // else if(var.contains("max")) {
      //   constructorVars.add("\t\tthis." + var + " = Integer.MIN_VALUE");
      // }
    }

    mfStruct.append("\n// MFStruct created using Grouping Attributes and F vectors \n");
    mfStruct.append("package qp.output;\n\n");
    mfStruct.append("class MfStruct { \n");
    mfStruct.append(classVars);
    mfStruct.append("\n\n\tMfStruct");
    mfStruct.append(constructorArgs);
    mfStruct.append(" {\n");
    mfStruct.append(constructorVars);
    mfStruct.append("\n\t}\n}\n");

    return mfStruct.toString();
  }

  public String generateCode() {
    String importCommands = "package qp.output;\n"
        + "import java.sql.*;\n"
        + "import java.util.ArrayList;\n"
        + "import java.util.HashSet;\n"
        + "import java.util.Iterator;\n"
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

    generateAggregateFunctions();
    String firstScan = populateGroupingAttributes();

    String furtherScans = performOpOnGV();

    String printOutput = generatePrintOutput();

    String endCode = "\n"
        + "\t\t\t"
        + "conn.close();\n"
        + "\t\t} catch(Exception e) {\n\n"
        + "\t\te.printStackTrace();\n"
        + "\t\t}\n"
        + "\t}\n}";

    String code = importCommands + generatedCodeClass + dbConnectionObj + mfStructObj + startQuery
        + firstScan + furtherScans + printOutput + endCode;

    // write to file "/Output/FinalQuery.java"
    return code;
  }

  String captalizeFirstLetter(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  String populateGroupingAttributes() {
    StringBuilder generateStep1 = new StringBuilder();
    StringJoiner buildUniqKey = new StringJoiner(" + ", "(", ")");
    StringBuilder fetchGroupingAttr = new StringBuilder();
    StringJoiner conditions = new StringJoiner(" && ", "(", ")");

    generateStep1.append("\t\t\t// STEP 1: Populate Grouping attributes\n");
    generateStep1.append("\t\t\tSystem.out.println(\"----STEP 1: Perform 0th Scan-------\");\n");
    generateStep1.append("\t\t\trs = st.executeQuery(queryStr);\n");
    generateStep1.append("\t\t\tSet<String> uniqueGAttr = new HashSet<String>();\n");
    generateStep1.append("\t\t\tMfStruct newRow;\n");
    generateStep1.append("\t\t\tString uniqueKey;\n");

    // Add Grouping attributes V
    for (String var : inputQuery.V) {
      if (infoSchema.containsKey(var)) {
        // Unique Key used to identify if record is present in mfStruct table i.e. "EmilyMilk"
        buildUniqKey.add(var);

        // Declare Grouping attributes
        generateStep1.append("\t\t\t");
        generateStep1.append((infoSchema.get(var) + " " + var + "; \n"));

        // e.g. cust = rs.getString("cust");
        fetchGroupingAttr.append("\t\t\t\t");
        fetchGroupingAttr.append(var + " = " + rsGetColumnValue(var) + ";\n");
      }
    }

    generateStep1.append("\n\t\t\twhile(rs.next()) {\n");
    generateStep1.append(fetchGroupingAttr);
    generateStep1.append("\t\t\t\tuniqueKey = " + buildUniqKey + ".toLowerCase();\n");
    generateStep1.append("\t\t\t\tif(!uniqueGAttr.contains(uniqueKey)) {\n");
    generateStep1.append("\t\t\t\t\tuniqueGAttr.add(uniqueKey);\n");
    generateStep1.append("\t\t\t\t\tnewRow = new MfStruct");
    generateStep1.append(buildUniqKey.toString().replace("+", ","));
    generateStep1.append(";\n\t\t\t\t\t");
    generateStep1.append("mfStruct.add(newRow);\n\t\t\t\t");
    generateStep1.append("}\n\t\t\t");

    for (String groupingAttribute : inputQuery.V) {
      conditions.add("row." + groupingAttribute + ".equals(" + groupingAttribute + ")");
    }

    generateStep1.append("\tfor(MfStruct row: mfStruct) {\n");
    generateStep1.append("\t\t\t\t\tif");
    generateStep1.append(conditions);
    generateStep1.append("{\n");

    // aggregateFunctions = generateAggregateFunctions(i);
    generateStep1.append(aggregateFunctions.get("0"));

    generateStep1.append("\t\t\t\t\t}\n\t\t\t\t}\n\t\t\t}\n");
    return generateStep1.toString();
  }

  // Return rs.getString("cust");
  String rsGetColumnValue(String column) {
    if (infoSchema.containsKey(column)) {
      return "rs.get" + captalizeFirstLetter(infoSchema.get(column)) + "(\"" + column + "\")";
    }
    return null;
  }

  String parseConditionVector(String conditionVector, int groupingVariableNum) {
    StringBuilder gvCondition = new StringBuilder();
    String[] conditions = conditionVector.split(" ");
    String[] splittedCondition = new String[3];
    String operator = "";
    boolean equalsFlag = false;

    for (String condition : conditions) {
      switch (condition.toLowerCase()) {
      case "and":
        gvCondition.append(" && ");
        break;
      case "or":
        gvCondition.append(" || ");
        break;
      default:
        // Remove grouping variable number from condition
        condition = condition.replace(groupingVariableNum + ".", "");
        equalsFlag = false;

        if(condition.contains("<=")) {
          operator = "<=";
          splittedCondition = condition.split("<=");
        } else if(condition.contains(">=")) {
          operator = ">=";
          splittedCondition = condition.split(">=");
        } else if(condition.contains("!=")) {
          operator = "!=";
          splittedCondition = condition.split("!=");
        } else if(condition.contains("<>")) {
          operator = "<>";
          splittedCondition = condition.split("<>");
        } else if (condition.contains("=")) {
          equalsFlag = true;
          operator = ".equals(";
          splittedCondition = condition.split("=");
        } else if (condition.contains("<")){
          operator = "<";
          splittedCondition = condition.split("<");
        } else if (condition.contains(">")){
          operator = ">";
          splittedCondition = condition.split(">");
        }

        gvCondition.append(rsGetColumnValue(splittedCondition[0]));
        gvCondition.append(operator);
        gvCondition.append(splittedCondition[1]);

        if(equalsFlag) {
          gvCondition.append(")");
        }
      }
    }
    return gvCondition.toString();
  }

  void generateAggregateFunctions() {
    // String[] aggregateFunctions = new String[2];
    StringJoiner avgs = new StringJoiner("\t\t\t\t");;
    StringBuilder aggregates;
    String[] arr;

    for(int i = 0; i <= inputQuery.n; i++) {

      aggregates =  new StringBuilder();
      for (String fVector : inputQuery.F) {
        if (fVector.contains(i + "")) {
          arr = fVector.split("_");
          aggregates.append("\t\t\t\t\t\t\t");

          switch (arr[0]) {
          case "sum":
            aggregates.append("row." + fVector + " += " + rsGetColumnValue(arr[2]) + ";");
            break;
          case "avg":
            avgs.add("\tavg = "
              + "row." + fVector.replace("avg", "count") + " > 0 ? "
              + "(double) row." + fVector.replace("avg", "sum") + " / "
              + "row." + fVector.replace("avg", "count") + " : 0;\n"
            );
            avgs.add("row." + fVector + " = Math.round(avg * 100) / 100D;");
            break;
          case "min":
            aggregates.append("row." + fVector + " = Math.min(row." + fVector + ", " + rsGetColumnValue(arr[2]) + ");");
            break;
          case "max":
            aggregates.append("row." + fVector + " = Math.max(row." + fVector + ", " + rsGetColumnValue(arr[2]) + ");");
            break;
          case "count":
            aggregates.append("row." + fVector + "++;");
          }
          aggregates.append("\n");
        }
      }

      aggregateFunctions.put(i + "", aggregates.toString());

    }

    aggregateFunctions.put("avgs", avgs.toString());
    // return aggregateFunctions;
  }

  // run loop on selected aggregate functions
  String performOpOnGV() {
    StringBuilder operationOnGV = new StringBuilder();
    // STEP 1: Perform operations on grouping variable\n";
    StringJoiner conditions = new StringJoiner(" && ", "(", ")");
//    String[] aggregateFunctions = new String[2];

    for (int i = 1; i <= inputQuery.n; i++) {
      operationOnGV.append("\n\t\t\trs = st.executeQuery(queryStr);\n");
      operationOnGV.append("\t\t\twhile(rs.next()) {\n");

      for (String var : inputQuery.CV) {
        if (var.startsWith("" + i)) {
          // Parse condition vector for nth grouping variable
          // 1.state='NY' and 1.quant>30
          operationOnGV.append("\t\t\t\tif(");
          operationOnGV.append(parseConditionVector(var, i));
          operationOnGV.append(") {\n");

          for (String groupingAttribute : inputQuery.V) {
            if (infoSchema.containsKey(groupingAttribute)) {
              operationOnGV.append("\t\t\t\t\t");
              operationOnGV.append(groupingAttribute);
              operationOnGV.append(" = ");
              operationOnGV.append(rsGetColumnValue(groupingAttribute));
              operationOnGV.append(";\n");
            }

            conditions.add("row." + groupingAttribute + ".equals(" + groupingAttribute + ")");
          }

          operationOnGV.append("\t\t\t\t\tfor(MfStruct row: mfStruct) {\n");
          operationOnGV.append("\t\t\t\t\t\tif");
          operationOnGV.append(conditions);
          operationOnGV.append("{\n");

          // aggregateFunctions = generateAggregateFunctions(i);
          operationOnGV.append(aggregateFunctions.get(i + ""));

          operationOnGV.append("\n\t\t\t\t\t\t}\n\t\t\t\t\t}\n\t\t\t\t}\n\t\t\t}\n");
        }
      }
    }

    // Calculate avg
    operationOnGV.append("\n\t\t\tIterator<MfStruct> itr = mfStruct.iterator();\n");
    operationOnGV.append("\n\t\t\tdouble avg;\n");
    operationOnGV.append("\t\t\twhile (itr.hasNext()) {\n");
    operationOnGV.append("\t\t\t\tMfStruct row = itr.next();\n\t\t\t\t");
    operationOnGV.append("//Calculate Average\n\t\t\t");
    operationOnGV.append(aggregateFunctions.get("avgs"));
    operationOnGV.append("\n\t\t\t\t//Apply Having Condition");
    operationOnGV.append("\n\t\t\t\tif (!(");

    // Apply Having condition
    StringJoiner havingCondition = new StringJoiner(" ");
    String[] hc = inputQuery.G.split(" ");
    for(String con: hc) {
      if(inputQuery.F.contains(con)) {
        havingCondition.add("row." + con);
      } else if(con.equals("and")) {
        havingCondition.add("&&");
      } else if(con.equals("or")) {
        havingCondition.add("||");
      } else {
        havingCondition.add(con);
      }
    }

    operationOnGV.append(havingCondition.toString());
    operationOnGV.append(")) {\n");
    operationOnGV.append("\t\t\t\t\titr.remove();\n");
    operationOnGV.append("\t\t\t\t}\n");
    operationOnGV.append("\t\t\t}\n");
    return operationOnGV.toString();
  }

  String generatePrintOutput() {
	StringBuilder selectAttrs = new StringBuilder();
	StringBuilder separator = new StringBuilder();
    StringBuilder attrValues = new StringBuilder();
	StringBuilder printOutput = new StringBuilder();
	StringBuilder printZero = new StringBuilder();

    // Table Header
    for (String var : inputQuery.S) {
      String attr = "";
      if (var.equals("cust")) {
        attr = "\"%-10s\",\"Customer\"";
        separator.append("========= ");
      } else if (var.equals("prod")) {
        attr = "\"%-10s\",\"Product\"";
        separator.append("========= ");
      } else {
        attr = "\"%-12s\",\"" + var + "  \"";
        separator.append("============ ");
      }
      selectAttrs.append("\t\t\tSystem.out.printf("+attr+");\n");
    }

    printOutput.append("\n\t\t\t" + "//Scan mf struct and print out the results\n");
    printOutput.append(selectAttrs);
    printOutput.append("\t\t\tSystem.out.println(\""+"\\n"+ separator +"\");\n\n");

    // Table Rows
    for (String var : inputQuery.S) {
      String value = "";
      if (var.equals("cust")) {
        value = "\"%-10s\", row."+ var;
      } else if (var.equals("prod")) {
        value = "\"%-10s\", row."+ var;
      } else if (var.contains("min")) {
    	String varName = "print_"+var;
        value = "\"%12s\", "+varName;

        // check for min value, if record value not present for selected group, print 0
        printZero.append("\n\t\t\t\t");
        printZero.append("int "+varName+ " = row."+ var +";\n");
        printZero.append("\t\t\t\t");
        printZero.append("if ("+ varName +" == Integer.MAX_VALUE) {\n");
        printZero.append("\t\t\t\t\t");
        printZero.append(varName + "= 0;\n");
        printZero.append("\t\t\t\t");
        printZero.append("}\n");
      } else {
        value = "\"%12s\", row."+ var;
      }
      attrValues.append("\t\t\t\tSystem.out.printf("+value+");\n");
    }

    printOutput.append("\t\t\tfor(MfStruct row: mfStruct) {\n");
    printOutput.append(printZero);
    printOutput.append(attrValues);
    printOutput.append("\t\t\t\tSystem.out.print('\\n');\n");
    printOutput.append("\t\t\t" + "}\n");

    return printOutput.toString();
  }
}
