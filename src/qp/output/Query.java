package qp.output;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
			System.out.println("----STEP 1: Perform 0th Scan-------");
			rs = st.executeQuery(queryStr);
			Set<String> uniqueGAttr = new HashSet<String>();
			MfStruct newRow;
			String uniqueKey;
			String cust; 

			while(rs.next()) {
				cust = rs.getString("cust");
				uniqueKey = (cust).toLowerCase();
				if(!uniqueGAttr.contains(uniqueKey)) {
					uniqueGAttr.add(uniqueKey);
					newRow = new MfStruct(cust);
					mfStruct.add(newRow);
				}
			}

			rs = st.executeQuery(queryStr);
			while(rs.next()) {
				if(rs.getString("state").equals("NY")) {
					cust = rs.getString("cust");
					for(MfStruct row: mfStruct) {
						if(row.cust.equals(cust)){
							row.count_1_quant++;
							row.sum_1_quant += rs.getInt("quant");
							

						}
					}
				}
			}

			rs = st.executeQuery(queryStr);
			while(rs.next()) {
				if(rs.getString("state").equals("NJ")) {
					cust = rs.getString("cust");
					for(MfStruct row: mfStruct) {
						if(row.cust.equals(cust) && row.cust.equals(cust)){
							row.count_2++;

						}
					}
				}
			}

			rs = st.executeQuery(queryStr);
			while(rs.next()) {
				if(rs.getString("state").equals("CT")) {
					cust = rs.getString("cust");
					for(MfStruct row: mfStruct) {
						if(row.cust.equals(cust) && row.cust.equals(cust) && row.cust.equals(cust)){
							row.count_3_quant++;
							
							row.sum_3_quant += rs.getInt("quant");

						}
					}
				}
			}

			Iterator<MfStruct> itr = mfStruct.iterator();
			while (itr.hasNext()) {
				MfStruct row = itr.next();
				//Calculate Average
				row.avg_1_quant = row.count_1_quant > 0 ? row.sum_1_quant / row.count_1_quant : 0;
				row.avg_3_quant = row.count_3_quant > 0 ? row.sum_3_quant / row.count_3_quant : 0;

				//Apply Having Condition
				if (row.sum_1_quant > 2 * row.count_2 || row.avg_1_quant > row.avg_3_quant) {
					itr.remove();
				}
			}

			//Scan mf struct and print out the results
			System.out.printf("%-10s","Customer");
			System.out.printf("%-12s","sum_1_quant  ");
			System.out.printf("%-12s","count_2_quant  ");
			System.out.printf("%-12s","sum_3_quant  ");
			System.out.println("\n========= ============ ============ ============ ");

			for(MfStruct row: mfStruct) {
				System.out.printf("%-10s", row.cust);
				System.out.printf("%12s", row.sum_1_quant);
				System.out.printf("%12s", row.count_2_quant);
				System.out.printf("%12s", row.sum_3_quant);
				System.out.print('\n');
			}

			conn.close();
		} catch(Exception e) {

		e.printStackTrace();
		}
	}
}