package qp;

import java.sql.*;
import java.util.*;


class MfStruct {
  String cust;
  String prod;
  int sum_1_quant;
//	int 1_avg_quant;
  int sum_2_quant;
  int sum_3_quant;

  MfStruct(String cust, String prod) {
    this.cust = cust;
    this.prod = prod;
    this.sum_1_quant = 0;
    this.sum_2_quant = 0;
    this.sum_3_quant = 0;
  }

  public String toString() {
        return (
          this.cust + " || "
            + this.prod + " || "
            + this.sum_1_quant + " || "
            + this.sum_2_quant + " || "
            + this.sum_3_quant
         );
  }
}


public class Project {
  static Connection conn;
  static ArrayList<MfStruct> mfStruct = new ArrayList<MfStruct>();

  public static void main(String[] args) {
    try {
      System.out.println("----Generated Code-----");
      ConnectDB newConnection = new ConnectDB();
      conn = newConnection.getConnection();
      String queryStr = "SELECT * FROM sales";
      Statement st = conn.createStatement();
      ResultSet rs;

      // STEP 1: Populate Grouping attributes
      rs = st.executeQuery(queryStr);
      Set<String> uniqueGAttr = new HashSet<String>();
      MfStruct newRow;
      System.out.println("----STEP 1: Perform 0th Scan-------");
      String cust, prod, uniqueKey;
      while(rs.next()) {
        cust = rs.getString("cust");
        prod = rs.getString("prod");
        uniqueKey = (cust + prod).toLowerCase();

        if(!uniqueGAttr.contains(uniqueKey)) {
          uniqueGAttr.add(uniqueKey);
          newRow = new MfStruct(cust, prod);
          mfStruct.add(newRow);
        }
      }

      // STEP 2: Perform operations for first grouping variable
      rs = st.executeQuery(queryStr);
      while(rs.next()) {
        if(rs.getString("state").equals("NY")) {
          cust = rs.getString("cust");
          prod = rs.getString("prod");
          for(MfStruct row: mfStruct) {
            if (row.cust.equals(cust) && row.prod.equals(prod)) {
              row.sum_1_quant += rs.getInt("quant");
            }

          }
        }
      }

      // STEP 2: Perform operations for second grouping variable
      rs = st.executeQuery(queryStr);
      while(rs.next()) {
        if(rs.getString("state").equals("NJ")) {
          cust = rs.getString("cust");
          prod = rs.getString("prod");
          for(MfStruct row: mfStruct) {
            if (row.cust.equals(cust) && row.prod.equals(prod)) {
              row.sum_2_quant += rs.getInt("quant");
            }

          }
        }
      }

      // STEP 2: Perform operations for third grouping variable
      rs = st.executeQuery(queryStr);
      while(rs.next()) {
        if(rs.getString("state").equals("CT")) {
          cust = rs.getString("cust");
          prod = rs.getString("prod");
          for(MfStruct row: mfStruct) {
            if (row.cust.equals(cust) && row.prod.equals(prod)) {
              row.sum_3_quant += rs.getInt("quant");
            }

          }
        }
      }

      // STEP 3: Print results
      System.out.println("Customer||Product||sum_NY||sum_NJ||sum_CT");
      System.out.println("==========================================");

      for(MfStruct row: mfStruct) {
          System.out.println(row.toString());  // Will invoke overrided `toString()` method
      }

      conn.close();
    } catch(SQLException e) {
      e.printStackTrace();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
