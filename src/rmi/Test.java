package rmi;

import java.sql.SQLException;
import java.text.ParseException;

public class Test {

	public static void main(String[] args) throws SQLException, ParseException {
		Service service = new Service();
		
//		service.register("user2", "user2");
//		service.register("user4", "user4");
		
		//ResultSet resultSet = service.connection.executeSQL("select * from user where user_name='user1',");
		
//		service.addConference("user1","user1","user2;user3","2016-11-2 10:00:00","2016","Math 9 Meeting");
//		service.addConference("user2","user1","user2;user3","2016-11-2 10:00:00","2016-12-3 11:00:00","Math First Meeting");
//		service.addConference("user2","user2","user2;user3","2016-11-2 10:00:00","2016-12-3 11:00:00","Math First Meeting");
////		service.deleteConference("11");
////		service.deleteConference("13");
////		service.deleteConference("20");
//		//System.out.print(service.getNumberOfConferenceAttender("1"));
//		service.clearConference("user");
//		service.clearConference("user2");
//		service.clearConference("user3");
//		service.clearConference("user8");
//		System.out.println(service.haveUserTimeConflict("63", "2016-12-11 10:00:00", "2016-12-20 11:00:00"));
		//service.addConference("user1","user1","user2;user3","2016-12-11 10:00:00","2016-12-20 11:00:00","conference 3");
		service.addConference("user4","user4","user4","2016-11-02 10:00:00","2016-12-11 11:00:00","conference 2");
		service.close();
	}

}