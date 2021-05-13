package qp.output;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.sql.Connection;

public class Query {
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
			String uniqueKey;
			String cust; 
			String prod; 

			while(rs.next()) {
				cust = rs.getString("cust");
				prod = rs.getString("prod");
				uniqueKey = (cust + prod).toLowerCase();
				if(!uniqueGAttr.contains(uniqueKey)) {
					uniqueGAttr.add(uniqueKey);
					newRow = new MfStruct(cust , prod); 
					mfStruct.add(newRow);
				}
			}

			rs = st.executeQuery(queryStr);
			while(rs.next()) {
				if(rs.getString("state").equals("NY")) {
					cust = rs.getString("cust");
					prod = rs.getString("prod");
					for(MfStruct row: mfStruct) {
						if(row.cust.equals(cust) && row.prod.equals(prod)){
							row.sum_1_quant += rs.getInt("quant");
							

						}
					}
				}
			}

			rs = st.executeQuery(queryStr);
			while(rs.next()) {
				if(rs.getString("state").equals("NJ")) {
					cust = rs.getString("cust");
					prod = rs.getString("prod");
					for(MfStruct row: mfStruct) {
						if(row.cust.equals(cust) && row.prod.equals(prod)){
							row.min_2_quant = Math.min(row.min_2_quant, rs.getInt("quant"));
							row.max_2_quant = Math.max(row.max_2_quant, rs.getInt("quant"));
							row.count_2_quant++;

						}
					}
				}
			}

			rs = st.executeQuery(queryStr);
			while(rs.next()) {
				if(rs.getString("state").equals("CT")) {
					cust = rs.getString("cust");
					prod = rs.getString("prod");
					for(MfStruct row: mfStruct) {
						if(row.cust.equals(cust) && row.prod.equals(prod)){
							row.sum_3_quant += rs.getInt("quant");
							
							row.max_3_quant = Math.max(row.max_3_quant, rs.getInt("quant"));

						}
					}
				}
			}

			//Scan mf struct and print out the results
			System.out.printf("%-8s","Customer  ");
			System.out.printf("%-7s","Product   ");
			System.out.printf("%-10s","sum_1_quant  ");
			System.out.printf("%-10s","min_2_quant  ");
			System.out.printf("%-10s","max_2_quant  ");
			System.out.printf("%-10s","count_2_quant  ");
			System.out.printf("%-10s","sum_3_quant  ");
			System.out.printf("%-10s","max_3_quant  ");
			System.out.println("\n========  ========  ===========  ===========  ===========  ===========  ===========  ===========  ");

			for(MfStruct row: mfStruct) {
				System.out.println(row.toString());
			}

			conn.close();
		} catch(Exception e) {

		}
	}
}