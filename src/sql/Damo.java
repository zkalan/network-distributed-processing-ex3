package sql;

import java.sql.ResultSet;
import java.sql.SQLException;


public class Damo {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		
		String sql = "select * from meeting;";
		
		//String createTable = "CREATE TABLE meeting (id int not null, name varchar(20) not null, age int null, primary key (id));";
		
		ConnectDB connection  = new ConnectDB(); 
		
		//ResultSet rscreateTable = connection.executeSQL(createTable);
		
		ResultSet rssql = connection.executeSQL(sql);
		
		while(rssql.next()){
			System.out.println(rssql.getString(1));
		}
//		
//		while(rscreateTable.next()){
//			System.out.println(rscreateTable.getString(1));
//		}
		
		connection.closeConnecetion();
	}

}
