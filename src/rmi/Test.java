package rmi;

import java.sql.SQLException;

public class Test {

	public static void main(String[] args) throws SQLException {
		Service service = new Service();
		
//		service.register("user2", "user2");
//		service.register("user3", "user3");
		
		//ResultSet resultSet = service.connection.executeSQL("select * from user where user_name='user1',");
		
		service.createConference("user1","user1","user2;user3","2016-11-2 10:00:00","2016-12-3 11:00:00","English First Meeting");
		
		service.close();
	}

}