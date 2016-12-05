package rmi;

import java.sql.ResultSet;
import java.sql.SQLException;

import sql.operateDB;
/**
 * 
 * @author zk
 *
 */
public class Service {
	
	operateDB connection;
	
	String sql = null;
	
	ResultSet resultSet = null;
	
	/**
	 * Service的构造函数
	 * 首先初始化一个全局的与服务器的连接
	 */
	public Service(){
		this.connection = new operateDB();
	}
	/**
	 * 验证是否登录成功
	 * @param username
	 * @param passwd
	 * @return
	 * @throws SQLException
	 */
	public boolean verifyLogin(String username,String passwd) throws SQLException{
		resultSet = this.searchUserByUserName(username);
		if (false == resultSet.next()){
			System.out.println("Login failure:unknown user name or bad password");
			return false;
		} else if(!resultSet.getString(3).equals(passwd)){
			System.out.println("Login failure:unknown user name or bad password");
			return false;
		} else {
			return true;
		}
	}
	
	
	/**
	 * 通过用户名查询用户的信息
	 * @param username
	 * @return
	 */
	public ResultSet searchUserByUserName(String username){
		sql = "select * from user where user_name='" + username + "';";
		return connection.executeSQL(sql);
	}
	/**
	 * 向用户表插入新的值
	 * @param username
	 * @param passwd
	 * @return
	 */
	public int userInsert(String username,String passwd){
		sql = "insert into user (user_name,user_passwd) "
				+ "values ('"+ username +"','" + passwd +"');";
		return connection.updateSQL(sql);
	}
	/**
	 * 通过用户名判断某个用户是否存在
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public boolean userExist(String username) throws SQLException{
		ResultSet tempresultSet = this.searchUserByUserName(username);
		if (false == tempresultSet.next()){
			return false;
		} else {
			return true;
		}
	}
	
	
	/**
	 * 通过会议标题查询会议的信息
	 * @param username
	 * @return
	 */
	public ResultSet searchMeetingByTitle(String title){
		sql = "select * from meeting where meeting_title='" + title + "';";
		return connection.executeSQL(sql);
	}
	/**
	 * 当会议被创建、查询、删除时判断会议是否存在
	 * @param username
	 * @param title
	 * @return
	 * @throws SQLException
	 */
	public boolean conferenceExist(String username,String title) throws SQLException{
		resultSet = this.searchUserByUserName(username);
		while (resultSet.next()){
			sql = "select * from meeting where meeting_createrId='" + resultSet.getString(1) + "';";
		}
		resultSet = connection.executeSQL(sql);
		while (resultSet.next()){
			if(resultSet.getString(2).equals(title)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 向会议表中插入新的会议
	 * 将会议创建并返回1或-1
	 * 然后将创建人插入到记录表中
	 * @param username
	 * @param starttime
	 * @param endtime
	 * @param title
	 * @return
	 * @throws SQLException
	 */
	public int conferenceInsert(String user_id,String starttime,String endtime,String title) throws SQLException{
		
		//构造将要执行的插入语句
		sql = "insert into meeting (meeting_title,meeting_createrId,meeting_startDatetime,meeting_endDatetime) "
				+ "values ('" + title +"','" + user_id + "','" + starttime + "','" + endtime +"');";
		int conferenceInsertResult = connection.updateSQL(sql);
		
		//返回会议创建结果
		return conferenceInsertResult;
	}
	/**
	 * 通过标题删除指定会议
	 * 注意
	 * 会议的相关记录也会被删除
	 * @param title
	 * @return
	 */
	public boolean deleteConferenceByTitle(String title){
		sql = "delete meeting from meeting where meeting_title='" + title + "';";
		try {
			connection.executeSQL(sql);
			return true;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 删除指定用户名下所有创建的会议
	 * @param user_id
	 * @return
	 */
	public boolean deleteConferenceByUserId(String user_id){
		sql = "delete meeting from meeting where meeting_createrId='" + user_id + "';";
		try {
			connection.executeSQL(sql);
			return true;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 删除某个用户名下指定的某个会议
	 * @param user_id
	 * @param meeting_title
	 * @return
	 */
	public boolean deleteConferenceByTitleOfUser(String user_id,String meeting_title){
		sql = "delete meeting from meeting where meeting_createrId='" + user_id + "' and meeting_title='" + meeting_title + "';";
		try {
			connection.executeSQL(sql);
			return true;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 通过用户id查询获取记录
	 * 通过会议id查询获取记录
	 * @param user_id
	 * @return
	 */
	public ResultSet searchRecordByUserId(String user_id){
		sql = "select * from record where record_user_id='" + user_id + "';";
		return connection.executeSQL(sql);
	}
	public ResultSet searchRecordByConferenceId(String meeting_id){
		sql = "select * from record where record_meeting_id='" + meeting_id + "';";
		return connection.executeSQL(sql);
	}
	/**
	 * 某个人是否参加了某个会议
	 * @param meeting_id
	 * @param user_id
	 * @return
	 */
	public boolean recordOfUserInConferenceExist(String meeting_id,String user_id){
		sql = "select * from record where record_user_id='" + user_id + "' and record_meeting_id='" + meeting_id + "';";
		try {
			ResultSet result = connection.executeSQL(sql);
			if (false == result.next()){
				return false;
			} else {
				return true;
			}
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 添加记录
	 * @param user_id
	 * @param meeting_id
	 * @return
	 */
	public int recordInsert(String user_id,String meeting_id){
		sql = "insert into record (record_user_id,record_meeting_id) "
				+ "values ('" + user_id + "','" + meeting_id + "');";
		return connection.updateSQL(sql);
	}
	/**
	 * 通过用户的id删除其名下的参加的所有会议
	 * @param user_id
	 * @return
	 */
	public boolean deleteRecordByUser(String user_id){
		sql = "delete record from record where record_user_id='" + user_id + "';";
		try {
			connection.updateSQL(sql);
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 删除某个会议名下的某个参与者
	 * 也可以称之为退出某个会议
	 * @param user_id
	 * @param meeting_id
	 * @return
	 */
	public boolean deleteRecordByUserOfConference(String user_id,String meeting_id){
		sql = "delete record from record where record_user_id='" + user_id + "' and record_meeting_id='" + meeting_id + "';";
		try {
			connection.updateSQL(sql);
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 注册账户
	 * 能够检测用户名是否存在
	 * 不返回内容
	 * @param username
	 * @param passwd
	 * @throws SQLException 
	 * 
	 */
	public void register(String username,String passwd) throws SQLException{
		
		//首先检测用户名是否已被占用
		boolean flag = this.userExist(username);
		
		if (flag){
			System.out.println("User name already exists");
		} else {
			try {
				this.userInsert(username, passwd);
				resultSet = searchUserByUserName(username);
				System.out.println("Register success\nUser information:");
				while(resultSet.next()){
					System.out.println("User-id:" + resultSet.getString(1));
					System.out.println("User-name:" + resultSet.getString(2));
					System.out.println("User-passwd:" + resultSet.getString(3));
				}
			} catch (Exception e){
				e.printStackTrace();
				System.out.println("Register failure");
			}
		}
	}
	/**
	 * 插入会议
	 * @param username
	 * @param passwd
	 * @param otheruser
	 * @param starttime
	 * @param endtime
	 * @param title
	 * @throws SQLException
	 */
	public void addConference(String username,String passwd,String otheruser,String starttime,String endtime,String title)throws SQLException{
		//首先判断用户是否登录成功
		boolean loginflag = this.verifyLogin(username,passwd);
		//然后判断该用户名下是否有同名会议
		boolean meetingflag = this.conferenceExist(username, title);
		if(loginflag && !meetingflag){
			ResultSet rs = this.searchUserByUserName(username);
			String user_id = null;
			while (rs.next()){
				user_id = rs.getString(1);
			}
			int insertflag = this.conferenceInsert(user_id, starttime, endtime, title);
			//会议已经创建成功
			if(-1 != insertflag){
				System.out.println("conference create success");
				//获得会议的id
				ResultSet meetingSet = this.searchMeetingByTitle(title);
				String meeting_id = null;
				while (meetingSet.next()){
					meeting_id = meetingSet.getString(1);
				}
				//为会议添加参与者
				this.addConferenceAttender(meeting_id, otheruser);
			} else {
				System.out.println("conference create failure");
			}
		}
		if (true == meetingflag){
			System.out.println("Conference " + title + "already exists");
		}
	}
	/**
	 * 创建会议参与者
	 * @param meeting_id
	 * @param otheruser
	 * @throws SQLException 
	 */
	public void addConferenceAttender(String meeting_id,String otheruser) throws SQLException{
		//获得参与者名单
		String[] otherUser = otheruser.split(";");
		for (int i = 0 ;i < otherUser.length;i++){
			//首先判断这个用户是否存在
			boolean userflag = this.userExist(otherUser[i]);
			//如果不存在就输出提示信息，并且i++
			//如果存在就进行插入操作，并且i++
			if (!userflag){
				//输出用户不存在的提示信息
				System.out.println("User " + otherUser[i] + " not exists");
			} else {
				//获得用户的id
				ResultSet rs = this.searchUserByUserName(otherUser[i]);
				String user_id = null;
				while (rs.next()){
					user_id = rs.getString(1);
				}
				//该用户是否已经加入了该会议
				boolean alreadyAttend = this.recordOfUserInConferenceExist(meeting_id,user_id);
				if (alreadyAttend){
					System.out.println("User " + otherUser[i] + " already join in this Conference");
				} else {
					//执行插入操作
					int insertflag = this.recordInsert(user_id, meeting_id);
					if (-1 == insertflag){
						System.out.println("User " + otherUser[i] + " join failure");
					} else {
						System.out.println("User " + otherUser[i] + " join success");
					}
				}
			}
		}
	}
	

	/**
	 * 
	 * @param username
	 * @param passwd
	 * @param otheruser
	 * @param starttime
	 * @param endtime
	 * @param title
	 * @throws SQLException
	 */
	public void createConference(String username,String passwd,String otheruser,String starttime,String endtime,String title) throws SQLException{
		//首先判断用户是否登录成功
		boolean login = this.verifyLogin(username, passwd);
		//判断会议是否存在
		boolean conferenceExist = this.conferenceExist(username, title);
		if(login && conferenceExist){
			System.out.println("Conference already exists");
		} else {
			//创建临时变量-结果集合
			ResultSet rs = null;
			//创建会议
			this.conferenceInsert(username, starttime, endtime, title);
			//获得用户输入的需要添加的参与会议的人
			//获得会议的id
			String meeting_id = null;
			rs = this.searchMeetingByTitle(title);
			while(rs.next()){
				meeting_id = rs.getString(1);
			}
			String[] otherUser = otheruser.split(";");
			//通过循环将参与会议的人的记录插入到记录表中去
			for (int i = 0 ;i < otherUser.length;i++){
				//首先判断这个用户是否存在
				//如果不存在就输出提示信息，并且i++
				//如果存在就进行插入操作，并且i++
				if (false == this.searchUserByUserName(otherUser[i]).next()){
					//输出用户不存在的提示信息
					System.out.println("User " + otherUser[i] + " not exists");
				} else {
					//执行插入操作
					rs = this.searchUserByUserName(otherUser[i]);
					while (rs.next()){
						this.recordInsert(rs.getString(1), meeting_id);
					}
				}
			}
		}
	}
	
	public void close(){
		this.connection.closeConnecetion();
	}

}
