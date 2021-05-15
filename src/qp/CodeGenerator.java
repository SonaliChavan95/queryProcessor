package qp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;


/**
 * This class is responsible for generating three classes,
 * 1. MfStruct.java - through generateMfStructClass() function.
 * 2. ConnectDB.java - through generateConnectDB() function.
 * 3. Query.java - through generateMainCode() function.
 * 
 * Once these files are generated through this Class,
 * Go to Query.java, and run the class to see the output on the console.
 * 
 */
public class CodeGenerator {
  static private InputQuery inputQuery;
  static private HashMap<String, String> infoSchema;
  static private Map<String, String> aggregateFunctions = new HashMap<String, String>();

  /*
   * Constructor for this class, 
   * Accepts argument 1 of InputQuery type and argument 2 as infoSchema of Hashmap type
   * */
  public CodeGenerator(InputQuery inputQuery, HashMap<String, String> infoSchema) {
    CodeGenerator.inputQuery = inputQuery;
    CodeGenerator.infoSchema = infoSchema;
  }

  /*
   * This function generates a ConnectDB class that's responsible 
   * for making connection with the database and perform MF/EMF operations
   * For running it on your local, you can change the DB user-name and password and
   * database name  below at line number 57, 58 and 59 respectively. 
   * */
  public String generateConnectDB() {
    String connectDB = "/**\n"
    		+ " * This class is responsible for making connection to the database\n"
    		+ " * For the main Project.java, this file provides infoSchema regarding the \n"
    		+ " * sales table\n"
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
    		+ "  Connection connection;\n"
    		+ "  public HashMap<String, String> infoSchema = new HashMap<String, String>();\n"
    		+ "  private final static String USER = \"abhinavgarg\";\n"
    		+ "  private final static String PASS = \"hello123\";\n"
    		+ "  private final static String DB_NAME = \"sales\";\n"
    		+ "\n"
    		+ "  ConnectDB() {\n"
    		+ "    connection = null;\n"
    		+ "  }\n"
    		+ "\n"
    		+ "  public Connection getConnection() {\n"
    		+ "    // JDBC driver name and database     \n"
    		+ "    final String DB_URL = \"jdbc:postgresql://localhost:5432/\"+DB_NAME;\n"
    		+ "\n"
    		+ "    try {\n"
    		+ "\n"
    		+ "      // Open a connection\n"
    		+ "      System.out.println(\"Connecting to database....\");\n"
    		+ "      connection = DriverManager.getConnection(DB_URL, USER, PASS);\n"
    		+ "\n"
    		+ "      if(connection != null) {\n"
    		+ "        System.out.println(\"Successful DB connection\");\n"
    		+ "      } else {\n"
    		+ "        System.out.println(\"DB connection failed\");\n"
    		+ "      }\n"
    		+ "\n"
    		+ "      setInfoSchema();\n"
    		+ "\n"
    		+ "    } catch(Exception e) {\n"
    		+ "      e.printStackTrace();\n"
    		+ "    }\n"
    		+ "    return connection;\n"
    		+ "  }\n"
    		+ "\n"
    		+ "  void setInfoSchema() {\n"
    		+ "    String SQLQuery = \"SELECT column_name, data_type \"\n"
    		+ "        + \"FROM information_schema.columns \"\n"
    		+ "        + \"WHERE table_name = 'sales'\";\n"
    		+ "\n"
    		+ "    try {\n"
    		+ "      Statement st = connection.createStatement();\n"
    		+ "      ResultSet rs = st.executeQuery(SQLQuery);\n"
    		+ "\n"
    		+ "      String col, type;\n"
    		+ "\n"
    		+ "      while(rs.next()) {\n"
    		+ "        col = rs.getString(\"column_name\");\n"
    		+ "        type = columnDataType(rs.getString(\"data_type\"));\n"
    		+ "        infoSchema.put(col, type);\n"
    		+ "      }\n"
    		+ "    }\n"
    		+ "    catch (SQLException ex) {\n"
    		+ "      ex.printStackTrace();\n"
    		+ "    }\n"
    		+ "  }\n"
    		+ "\n"
    		+ "  private String columnDataType(String type) {\n"
    		+ "    switch(type) {\n"
    		+ "    case \"character varying\":\n"
    		+ "    case \"character\":\n"
    		+ "      return \"String\";\n"
    		+ "    case \"integer\":\n"
    		+ "      return \"int\";\n"
    		+ "    default:\n"
    		+ "      return \"\";\n"
    		+ "    }\n"
    		+ "  }\n"
    		+ "}\n"
    		+ "";
    return connectDB;
  }

  /**
   * This function is the main function that generates the Query.java main class
   * 
   * MfStruct generates an object with all the necessary values. For average, it declares 
   * it as a double, for all other integer related, int type and for others, String.
   * */
  public String generateMainCode() {
	// Import necessary files and classes
	String importCommands = "package qp.output;\n"			
    // Import necessary files and classes
        + "import java.sql.*;\n"
        + "import java.util.ArrayList;\n"
        + "import java.util.HashSet;\n"
        + "import java.util.Iterator;\n"
        + "import java.util.Set;\n"
        + "import java.sql.Connection;\n\n";

    // Generate Query class with main function and
    String generatedCodeClass = "public class Query {\n";
    String dbConnectionObj = "\tstatic Connection conn;\n";

    // array of MFStruct objects
    String mfStructObj = "\tstatic ArrayList<MfStruct> mfStruct = new ArrayList<MfStruct>();\n";

    // declaring main function that calls ConnectDB class which was generated in one of the 
    // above mentioned functions
    String startQuery = "\tpublic static void main(String[] args) {\n"
        + "\t\ttry {\n"
        + "\t\t\tConnectDB newConnection = new ConnectDB();\n"
        + "\t\t\tconn = newConnection.getConnection();\n"
        + "\t\t\tString queryStr = \"SELECT * FROM sales\";\n"
        + "\t\t\tStatement st = conn.createStatement();\n"
        + "\t\t\tResultSet rs;\n";

    // generate aggregate functions that we are going to use for calculating for 
    // MF and EMF queries
    generateAggregateFunctions();
    
    // Performing first scan, populate mf-structure list with grouping attributes
    // values and setting default values for other attributes in mf-structure
    String firstScan = populateGroupingAttributes();

    // For each grouping variable, scan the table, and 
    // update value into the mfStructure row after fulfilling condition 
    // and making calculation depending upon the aggregate functions
    String furtherScans = performOpOnGV();

    // this function generates the Final loop that iterates over the mf-structure list 
    // and prints out the output on the console
    String printOutput = generatePrintOutput();

    // Query class closing statements
    String endCode = "\n"
        + "\t\t\t"
        + "conn.close();\n"
        + "\t\t} catch(Exception e) {\n\n"
        + "\t\te.printStackTrace();\n"
        + "\t\t}\n"
        + "\t}\n}";

    // combining all the code generated in the function and returning it back to the Project.java class
    // to write it into the file
    String code = importCommands + generatedCodeClass + dbConnectionObj + mfStructObj + startQuery
        + firstScan + furtherScans + printOutput + endCode;
    return code;
  }

  /**
   * This function is used to capitalize first letter of the string passed to it.
   * for example, 'string' becomes 'String'.
   * */
  String captalizeFirstLetter(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  /**
   * This function generates the MfStructure for our input query
   * in file name MfStruct.java based on the type of attribute from infoSchema
   * MfStruct generates an object with all the necessary values. For average, it declares 
   * it as a double, for all other integer related, int type and for others, String.
   * 
   * This is STEP 1 for our project workflow
   * */
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
  
  /**
   * This function is the step 2 in our project,
   * i.e. Scan through the table and take out values based on grouping attributes
   * If not present in the mfStruct array, add it, if present ignore.
   * 
   * This function is also responsible for calculating the aggregates on columns outside the
   * group. i.e. performing EMF aggregate calculation
   * */
  String populateGroupingAttributes() {
    StringBuilder generateStep1 = new StringBuilder();
    StringJoiner buildUniqKey = new StringJoiner(" + ", "(", ")");
    StringBuilder fetchGroupingAttr = new StringBuilder();
    StringJoiner conditions = new StringJoiner(" && ", "(", ")");

    generateStep1.append("\t\t\t// STEP 1: Populate Grouping attributes\n");
    generateStep1.append("\t\t\t//----STEP 1: Perform 0th Scan-------\n");
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

  
  /**
   * This function is parsing the condition vector, i.e. generating java expressions from 
   * the input query conditions that are passed to the input query. This function even 
   * takes care of the AND and OR conditions between each condition vector, for. e.g. '1.state = "NY" and 1.quant > 30'
   * */
  String parseConditionVector(String conditionVector, int groupingVariableNum) {
    StringBuilder gvCondition = new StringBuilder();
    String[] conditions = conditionVector.split(" ");
    String[] splittedCondition = new String[3];
    String operator = "";
    boolean equalsFlag = false;
    
    // for each condition from the condition vector, do the following
    // if it's and replace it with '&&', if it's or replace it with '||'
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
        
        // Set operator based on the condition
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

        // append together to parse the condition vector and generate equivalent java expression
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

  /**
   * This function is generating aggregate functions as provided in the input query
   * */
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
            avgs.add("\n\t\t\t\tavg = "
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

  /**
   * This function is STEP 3 of our project, 
   * i.e. for each grouping variable, generate a while loop to scan the table and
   * do the needful based on the condition
   * */
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
    
    // apply having condition if any
    operationOnGV.append("\n\t\t\t\t//Apply Having Condition");
    operationOnGV.append("\n\t\t\t\tif (!(");
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
    // remove unmatched row based on havingCondition
    operationOnGV.append("\t\t\t\t\titr.remove();\n");
    operationOnGV.append("\t\t\t\t}\n");
    operationOnGV.append("\t\t\t}\n");
    return operationOnGV.toString();
  }

  /**
   * This function is STEP 4 of our project, 
   * i.e. generate the final loop to iterate over each row in mfStruct and print the output on the console
   * Strings are left aligned, and integer values are right aligned
   * */
  String generatePrintOutput() {
	StringBuilder selectAttrs = new StringBuilder();
	StringBuilder separator = new StringBuilder();
    StringBuilder attrValues = new StringBuilder();
	StringBuilder printOutput = new StringBuilder();
	StringBuilder printZero = new StringBuilder();

	printOutput.append("\t\t\tif(mfStruct.size() > 0) {");
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
      selectAttrs.append("\t\t\t\tSystem.out.printf("+attr+");\n");
    }

    printOutput.append("\n\t\t\t\t" + "//Scan mf struct and print out the results\n");
    printOutput.append(selectAttrs);
    printOutput.append("\t\t\t\tSystem.out.println(\""+"\\n"+ separator +"\");\n\n");

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
        printZero.append("\n\t\t\t\t\t");
        printZero.append("int "+varName+ " = row."+ var +";\n");
        printZero.append("\t\t\t\t\t");
        printZero.append("if ("+ varName +" == Integer.MAX_VALUE) {\n");
        printZero.append("\t\t\t\t\t\t");
        printZero.append(varName + "= 0;\n");
        printZero.append("\t\t\t\t\t");
        printZero.append("}\n");
      } else {
        value = "\"%12s\", row."+ var;
      }
      attrValues.append("\t\t\t\t\tSystem.out.printf("+value+");\n");
    }

    printOutput.append("\t\t\t\tfor(MfStruct row: mfStruct) {\n");
    printOutput.append(printZero);
    printOutput.append(attrValues);
    printOutput.append("\t\t\t\t\tSystem.out.print('\\n');\n");
    printOutput.append("\t\t\t\t" + "}\n");
    printOutput.append("\t\t\t" + "}\n");
    printOutput.append("\t\t\telse {\n");
    printOutput.append("\t\t\t\tSystem.out.println(\"No Results found!!\");\n");
    printOutput.append("\t\t\t}\n");

    return printOutput.toString();
  }
}