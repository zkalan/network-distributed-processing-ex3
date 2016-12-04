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
		} else if(passwd != resultSet.getString(3)){
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
	 * 通过会议标题查询会议的信息
	 * @param username
	 * @return
	 */
	public ResultSet searchMeetingByTitle(String title){
		sql = "select * from meeting where meeting_title='" + title + "';";
		return connection.executeSQL(sql);
	}
	/**
	 * 将会议创建并返回1或-1
	 * 然后将创建人插入到记录表中
	 * @param username
	 * @param starttime
	 * @param endtime
	 * @param title
	 * @return
	 * @throws SQLException
	 */
	public int conferenceInsert(String username,String starttime,String endtime,String title) throws SQLException{
		//首先创建会议
		//先要获得登录用户的id
		resultSet = this.searchUserByUserName(username);
		String user_id = null;
		while(resultSet.next()){
			user_id = resultSet.getString(1);
		}
		//构造将要执行的插入语句
		sql = "insert into meeting (meeting_title,meeting_createrId,meeting_startDatetime,meeting_endDatetime) "
				+ "values ('" + title +"','" + user_id + "','" + starttime + "','" + endtime +"');";
		int conferenceInsertResult = connection.updateSQL(sql);
		
		//通过查询获得会议的id
		resultSet = this.searchMeetingByTitle(title);
		String meeting_id = null;
		while (resultSet.next()){
			meeting_id = resultSet.getString(1);
		}
		
		//将创建会议的用户本身插入到记录表里去
		this.recordInsert(user_id, meeting_id);
		
		//返回会议创建结果
		return conferenceInsertResult;
	}
	/**
	 * 将用户插入到记录表里去
	 * @param user_id
	 * @param meeting_id
	 * @return
	 */
	public int recordInsert(String user_id,String meeting_id){
		//将创建会议的用户本身插入到记录表里去
		sql = "insert into record (record_user_id,record_meeting_id) "
				+ "values ('" + user_id + "','" + meeting_id + "');";
		return connection.updateSQL(sql);
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
		
		int result = this.userInsert(username, passwd);
		
		if (-1 == result){
			System.out.println("User name already exists");
		} else {
			System.out.println("Register success\nUser information:");
			resultSet = searchUserByUserName(username);
			while(resultSet.next()){
				System.out.println("User-id:" + resultSet.getString(1));
				System.out.println("User-name:" + resultSet.getString(2));
				System.out.println("User-passwd:" + resultSet.getString(3));
			}
		}
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
		if(false == resultSet.next()){
			return false;
		} else {
			return true;
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
