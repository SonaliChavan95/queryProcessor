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
			ConnectDB newConnection = new ConnectDB();
			conn = newConnection.getConnection();
			String queryStr = "SELECT * FROM sales";
			Statement st = conn.createStatement();
			ResultSet rs;
			// STEP 1: Populate Grouping attributes
			//----STEP 1: Perform 0th Scan-------
			rs = st.executeQuery(queryStr);
			Set<String> uniqueGAttr = new HashSet<String>();
			MfStruct newRow;
			String uniqueKey;
			String prod; 
			int month; 

			while(rs.next()) {
				prod = rs.getString("prod");
				month = rs.getInt("month");
				uniqueKey = (prod + month).toLowerCase();
				if(!uniqueGAttr.contains(uniqueKey)) {
					uniqueGAttr.add(uniqueKey);
					newRow = new MfStruct(prod , month);
					mfStruct.add(newRow);
				}
				for(MfStruct row: mfStruct) {
					if(row.prod.equals(prod) && row.month == month){
					}
				}
			}

			rs = st.executeQuery(queryStr);
			while(rs.next()) {
					prod = rs.getString("prod");
					month = rs.getInt("month");
				if(rs.getString("prod").equals(prod) && rs.getInt("month")>month-1) {
					for(MfStruct row: mfStruct) {
						if(row.prod.equals(prod) && row.month == month){
							row.count_1_quant++;

						}
					}
				}
			}

			rs = st.executeQuery(queryStr);
			while(rs.next()) {
					prod = rs.getString("prod");
					month = rs.getInt("month");
				if(rs.getString("prod").equals(prod) && rs.getInt("month")>month+1) {
					for(MfStruct row: mfStruct) {
						if(row.prod.equals(prod) && row.month == month){
							row.count_2_quant++;

						}
					}
				}
			}

			Iterator<MfStruct> itr = mfStruct.iterator();

			double avg;
			while (itr.hasNext()) {
				MfStruct row = itr.next();
				//Calculate Average
						}
			if(mfStruct.size() > 0) {
				//Scan mf struct and print out the results
				System.out.printf("%-10s","Product");
				System.out.printf("%-12s","month  ");
				System.out.printf("%-12s","count_1_quant  ");
				System.out.printf("%-12s","count_2_quant  ");
				System.out.println("\n========= ============ ============ ============ ");

				for(MfStruct row: mfStruct) {
					System.out.printf("%-10s", row.prod);
					System.out.printf("%12s", row.month);
					System.out.printf("%12s", row.count_1_quant);
					System.out.printf("%12s", row.count_2_quant);
					System.out.print('\n');
				}
			}
			else {
				System.out.println("No Results found!!");
			}

			conn.close();
		} catch(Exception e) {

		e.printStackTrace();
		}
	}
}