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
				for(MfStruct row: mfStruct) {
					if(row.cust.equals(cust)){
							row.sum_0_quant += rs.getInt("quant");
							row.max_0_quant = Math.max(row.max_0_quant, rs.getInt("quant"));
							
							row.count_0_quant++;
					}
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
							row.sum_2_quant += rs.getInt("quant");

						}
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
				if (!(0.1 * row.sum_1_quant < row.max_0_quant)) {
					itr.remove();
				}
			}

			//Scan mf struct and print out the results
			System.out.printf("%-10s","Customer");
			System.out.printf("%-12s","sum_1_quant  ");
			System.out.printf("%-12s","sum_2_quant  ");
			System.out.printf("%-12s","count_1_quant  ");
			System.out.printf("%-12s","max_0_quant  ");
			System.out.printf("%-12s","avg_0_quant  ");
			System.out.println("\n========= ============ ============ ============ ============ ============ ");

			for(MfStruct row: mfStruct) {
				System.out.printf("%-10s", row.cust);
				System.out.printf("%12s", row.sum_1_quant);
				System.out.printf("%12s", row.sum_2_quant);
				System.out.printf("%12s", row.count_1_quant);
				System.out.printf("%12s", row.max_0_quant);
				System.out.printf("%12s", row.avg_0_quant);
				System.out.print('\n');
			}

			conn.close();
		} catch(Exception e) {

		e.printStackTrace();
		}
	}
}