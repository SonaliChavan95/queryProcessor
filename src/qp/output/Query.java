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
				for(MfStruct row: mfStruct) {
					if(row.cust.equals(cust) && row.prod.equals(prod)){
							row.sum_0_quant += rs.getInt("quant");
							
							row.count_0_quant++;
					}
				}
			}

			Iterator<MfStruct> itr = mfStruct.iterator();

			double avg;
			while (itr.hasNext()) {
				MfStruct row = itr.next();
				//Calculate Average
			
				avg = row.count_0_quant > 0 ? (double) row.sum_0_quant / row.count_0_quant : 0;
				row.avg_0_quant = Math.round(avg * 100) / 100D;
				//Apply Having Condition
				if (!(row.avg_0_quant > 1000)) {
					itr.remove();
				}
			}
			if(mfStruct.size() > 0) {
				//Scan mf struct and print out the results
				System.out.printf("%-10s","Customer");
				System.out.printf("%-10s","Product");
				System.out.printf("%-12s","avg_0_quant  ");
				System.out.printf("%-12s","sum_0_quant  ");
				System.out.println("\n========= ========= ============ ============ ");

				for(MfStruct row: mfStruct) {
					System.out.printf("%-10s", row.cust);
					System.out.printf("%-10s", row.prod);
					System.out.printf("%12s", row.avg_0_quant);
					System.out.printf("%12s", row.sum_0_quant);
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