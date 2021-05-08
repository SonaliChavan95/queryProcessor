import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.sql.Connection;

// MFStruct created using Grouping Attributes and F vectors 
class MfStruct { 
	String cust; 
	String prod; 
	int sum_1_quant; 
	int avg_1_quant; 
	int min_2_quant; 
	int sum_3_quant; 
	int avg_3_quant; 

	MfStruct(String cust, String prod) {
		this.cust = cust;
		this.prod = prod;
		this.sum_1_quant = 0;
		this.avg_1_quant = 0;
		this.min_2_quant = 0;
		this.sum_3_quant = 0;
		this.avg_3_quant = 0;
	}

	public String toString() { 
		 return (
			this.cust + "\t"+ this.prod + "\t"+ this.sum_1_quant + "\t"+ this.min_2_quant + "\t"+ this.sum_3_quant
		);
	}
}
public class GeneratedCode {
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
						}
					}
				}
			}

			//Scan mf struct and print out the results
			System.out.println("Customer  Product  sum_1_quant  min_2_quant  sum_3_quant  ");
			System.out.println("-------------------------------------------------");
			for(MfStruct row: mfStruct) {
				System.out.println(row.toString());
			}

			conn.close();
		} catch(Exception e) {

		}
	}
}