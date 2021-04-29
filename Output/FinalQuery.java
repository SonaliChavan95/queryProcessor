import java.sql.*;
import java.util.ArrayList;
public class GeneratedCode {
static Connection conn;
public static void main(String[] args) {
try {
System.out.println("----Generated Code-----");
ConnectDB newConnection = new ConnectDB();
newConnection.get_connection();
String queryStr = "SELECT * FROM sales";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(queryStr);
System.out.println("Testing overwrite");
while(rs.next()) {
				
			}conn.close();
		} catch(Exception e) {

		}
	}
}