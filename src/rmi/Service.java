package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sql.operateDB;
/**
 * 
 * @author zk
 *让客户机与服务器对象实例建立一对一的连接
 */
public class Service extends UnicastRemoteObject implements ServiceInterface{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2069495573148807859L;

	operateDB connection;
	
	String sql = null;
	
	ResultSet resultSet = null;
	
	/**
	 * Service的构造函数
	 * 首先初始化一个全局的与服务器的连接
	 */
	public Service() throws RemoteException{
		this.connection = new operateDB();
		this.sql = null;
		this.resultSet = null;
	}
	/**
	 * 验证是否登录成功
	 * @param username
	 * @param passwd
	 * @return
	 * @throws SQLException
	 */
	public boolean verifyLogin(String username,String passwd) throws SQLException,RemoteException{
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
	///////////////////////////////11111111111111111111111/////////////////////////////////////////
	
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
	 * 通过用户的名字返回它的id
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public String getUserIdByName(String username) throws SQLException{
		ResultSet rs = searchUserByUserName(username);
		while (rs.next()){
			return rs.getString(1);
		}
		return null;
	}
	/**
	 * 通过用户的id查询获得用户的名字
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public String getUserNameById(String id) throws SQLException{
		
		sql = "select * from user where user_id='"+ id + "';";
		ResultSet rs = connection.executeSQL(sql);
		while(rs.next()){
			return rs.getString(2);
		}
		return null;
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
	////////////////////////////////222222222222222222////////////////////////////////////////////////
	
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
	 * 通过id查询会议
	 * 判断会议是否存在
	 * @param meeting_id
	 * @return
	 * @throws SQLException
	 */
	public ResultSet searchMeetingById(String meeting_id) throws SQLException{
		sql = "select * from meeting where meeting_id='" + meeting_id + "';";
		ResultSet rs = connection.executeSQL(sql);
		return rs;
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
	 * 通过查询指定id的会议数量
	 * 判断会议是否存在
	 * @param meeting_id
	 * @return
	 * @throws SQLException
	 */
	public boolean conferenceExistById(String meeting_id) throws SQLException{
		sql = "select count(meeting_id) from meeting where meeting_id='" + meeting_id + "';";
		ResultSet rs = connection.executeSQL(sql);
		while (rs.next()){
			if(1 == rs.getInt(1)){
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
	 * 通过id删除指定会议
	 * 注意
	 * 会议的相关记录也会被删除
	 * @param title
	 * @return
	 */
	public boolean deleteConferenceById(String meeting_id){
		sql = "delete meeting from meeting where meeting_id='" + meeting_id + "';";
		try {
			connection.updateSQL(sql);
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
			connection.updateSQL(sql);
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
			connection.updateSQL(sql);
			return true;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	////////////////////////////////////333333333333333333333/////////////////////////////////////////
	
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
	public int getNumberOfConferenceAttender(String meeting_id) throws SQLException{
		sql = "select count(record_user_id) from record where record_meeting_id='" + meeting_id + "';";
		ResultSet rs = connection.executeSQL(sql);
		while (rs.next()){
			return rs.getInt(1);
		}
		return -1;
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
	/////////////////////////////////////////注册用户////////////////////////////////////////////////////
	
	/**
	 * 注册账户
	 * 能够检测用户名是否存在
	 * 不返回内容
	 * @param username
	 * @param passwd
	 * @throws SQLException 
	 * 
	 */
	public String register(String username,String passwd) throws SQLException{
		
		StringBuilder sb = new StringBuilder();
		
		//首先检测用户名是否已被占用
		boolean flag = this.userExist(username);
		
		if (flag){
			System.out.println("User name already exists");
			sb.append("User name already exists\n");
		} else {
			try {
				this.userInsert(username, passwd);
				resultSet = searchUserByUserName(username);
				System.out.println("Register success\nUser information:");
				sb.append("Register success\nUser information:\n");
				while(resultSet.next()){
					System.out.println("User-id:" + resultSet.getString(1));
					sb.append("User-id:" + resultSet.getString(1) + "\n");
					System.out.println("User-name:" + resultSet.getString(2));
					sb.append("User-name:" + resultSet.getString(2) + "\n");
					System.out.println("User-passwd:" + resultSet.getString(3));
					sb.append("User-passwd:" + resultSet.getString(3) + "\n");
				}
			} catch (Exception e){
				e.printStackTrace();
				System.out.println("Register failure");
				sb.append("Register failure");
			}
		}
		return sb.toString();
	}
/////////////////////////////////////////添加会议////////////////////////////////////////////////////
	/**
	 * 插入会议
	 * @param username
	 * @param passwd
	 * @param otheruser
	 * @param starttime
	 * @param endtime
	 * @param title
	 * @return 
	 * @throws SQLException
	 * @throws ParseException 
	 * @throws RemoteException 
	 */
	public String addConference(String username,String passwd,String otheruser,String starttime,String endtime,String title)throws SQLException, ParseException, RemoteException{
		
		StringBuilder sb = new StringBuilder();
		
		//首先判断用户是否登录成功
		boolean loginflag = this.verifyLogin(username,passwd);
		if (!loginflag){
			sb.append("Login failure:unknown user name or bad password\n");
		}
		//然后判断该用户名下是否有同名会议
		boolean meetingflag = this.conferenceExist(username, title);
		if(loginflag && !meetingflag){
			//获取用户的id
			String user_id = this.getUserIdByName(username);
			//首先判断这个用户是否有时间创建这个会议
			if(haveUserTimeConflict(user_id,starttime,endtime)){
				System.out.println("User time conflict, conference create failure");
				sb.append("User time conflict, conference create failure\n");
			} else {
				//创建会议
				int insertflag = this.conferenceInsert(user_id, starttime, endtime, title);
				//会议已经创建成功
				if(-1 != insertflag){
					System.out.println("conference create success");
					sb.append("conference create success\n");
					//获得会议的id
					ResultSet meetingSet = this.searchMeetingByTitle(title);
					String meeting_id = null;
					while (meetingSet.next()){
						meeting_id = meetingSet.getString(1);
					}
					//为会议添加参与者
					if(otheruser.equals(null) || otheruser.equals("") || otheruser.matches(";+")){
						System.out.println("please input attender, at least one person");
						sb.append("please input attender, at least one person\n");
					} else {
						this.recordInsert(user_id, meeting_id);
						sb.append(this.addConferenceAttender(meeting_id, otheruser,starttime,endtime));
					}
				} else {
					System.out.println("conference create failure");
					sb.append("conference create failure\n");
				}
			}
		}
		if (true == meetingflag){
			System.out.println("Conference " + title + " already exists");
			sb.append("Conference " + title + " already exists\n");
		}
		return sb.toString();
	}
///////////////////////////////////////////添加会议参与者//////////////////////////////////////////////////
	/**
	 * 创建会议参与者
	 * @param meeting_id
	 * @param otheruser
	 * @throws SQLException 
	 * @throws ParseException 
	 */
	public String addConferenceAttender(String meeting_id,String otheruser,String starttime,String endtime) throws SQLException, ParseException{
		
		StringBuilder sb = new StringBuilder();
		
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
				sb.append("User " + otherUser[i] + " not exists\n");
			} else {
				//获得用户的id
				String user_id = this.getUserIdByName(otherUser[i]);
				//该用户是否已经加入了该会议
				boolean alreadyAttend = this.recordOfUserInConferenceExist(meeting_id,user_id);
				if (alreadyAttend){
					System.out.println("User " + otherUser[i] + " already join in this Conference");
					sb.append("User " + otherUser[i] + " already join in this Conference\n");
				} else {
					//首先判断这个用户是否存在时间冲突
					if (haveUserTimeConflict(user_id,starttime,endtime)){
						System.out.println("User " + otherUser[i] + " has time conflict");
						sb.append("User " + otherUser[i] + " has time conflict\n");
					} else {
						//执行插入操作
						int insertflag = this.recordInsert(user_id, meeting_id);
						if (-1 == insertflag){
							System.out.println("User " + otherUser[i] + " join failure");
							sb.append("User " + otherUser[i] + " join failure\n");
						} else {
							System.out.println("User " + otherUser[i] + " join success");
							sb.append("User " + otherUser[i] + " join success\n");
						}
					}
				}
			}
		}
		//如果创建会议的参与人数小于2
		//删除这个会议并且输出提示信息
		int attendernumber = this.getNumberOfConferenceAttender(meeting_id);
		if(attendernumber < 2){
			//删除这个会议输出信息
			boolean deleteflag = this.deleteConferenceById(meeting_id);
			if(deleteflag){
				System.out.println("Conference deleted, because number of attenders less than 2");
				sb.append("Conference deleted, because number of attenders less than 2\n");
			} else {
				System.out.println("Number of attenders less than 2 but conference deleted failure");
				sb.append("Number of attenders less than 2 but conference deleted failure\n");
			}
		}
		return sb.toString();
	}
/////////////////////////////////////////////查询指定时间段内的会议////////////////////////////////////////////////
	
	/**
	 * 查询指定时间段内的会议
	 * 按照时间排序 升序
	 * @param user_name
	 * @param starttime
	 * @param endtime
	 * @return 
	 * @throws SQLException
	 * @throws ParseException
	 * @throws RemoteException 
	 */
	public String conferenceSearch(String user_name,String passwd,String starttime,String endtime) throws SQLException, ParseException, RemoteException{
		
		StringBuilder sb = new StringBuilder();
		
		//首先验证登录
		if(this.verifyLogin(user_name, passwd)){
			//统计有多少会议满足时间要求
			int i = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date Start = sdf.parse(starttime);
			Date End = sdf.parse(endtime);
			Date cStart = null,cEnd = null;
			String user_id = this.getUserIdByName(user_name);
			//构造查询的特殊sql语句
			//包括外键的使用
			//表的别名
			//left join
			//按照时间排序
			//desc为降续排列
			sql = "select * from record r1 left join meeting m1 on r1.record_meeting_id=m1.meeting_id "
					+ "where r1.record_user_id='" + user_id +"' order by meeting_startDatetime;";
			ResultSet conferenceSet = this.connection.executeSQL(sql);
			System.out.println("id | title | CId | meeting_startDatetime | meeting_endDatetime | attender");
			sb.append("id | title | CId | meeting_startDatetime | meeting_endDatetime | attender\n");
			//循环判断组合查询记录表中的指定用户参与的会议
			while(conferenceSet.next()){
				String starttimestr = null;
				String endtimestr = null;
				starttimestr = conferenceSet.getString(7);
				endtimestr = conferenceSet.getString(8);
				
				cStart = sdf.parse(starttimestr);
				cEnd = sdf.parse(endtimestr);
				if(cStart.getTime() >= Start.getTime() && cEnd.getTime() <= End.getTime()){
					System.out.print(conferenceSet.getString(4) + " | ");
					sb.append(conferenceSet.getString(4) + " | ");
					System.out.print(conferenceSet.getString(5) + " | ");
					sb.append(conferenceSet.getString(5) + " | ");
					System.out.print(conferenceSet.getString(6) + " | ");
					sb.append(conferenceSet.getString(6) + " | ");
					System.out.print(conferenceSet.getString(7) + " | ");
					sb.append(conferenceSet.getString(7) + " | ");
					System.out.print(conferenceSet.getString(8) + " | ");
					sb.append(conferenceSet.getString(8) + " | ");
					sb.append(this.printConferenceAttender(conferenceSet.getString(4)));
					i++;
				}
			}
			if (0 == i){
				System.out.println("Can not find any conference");
				sb.append("Can not find any conference\n");
			}
		} else {
			sb.append("Login failure:unknown user name or bad password\n");
		}
		
		return sb.toString();
	}
/////////////////////////////////////////////打印指定会议的信息////////////////////////////////////////////////
	/**
	 * 打印会议的信息以及参与者的id
	 * @param meeting_id
	 * @throws SQLException
	 */
	public String printConferenceAttender(String meeting_id) throws SQLException{
		
		StringBuilder sb = new StringBuilder();
		//查询meeting得到制定会议的参与者
		sql = "select * from meeting where meeting_id='" + meeting_id + "';";
		ResultSet rs = connection.executeSQL(sql);
		rs = this.searchRecordByConferenceId(meeting_id);
		while (rs.next()){
			System.out.print(rs.getString(2) + ", ");
			sb.append(this.getUserNameById(rs.getString(2)) + ", ");
		}
		System.out.print("\n");
		sb.append("\n");
		return sb.toString();
	}

	///////////////////////////////////删除指定会议///////////////////////////////////////////////////
	/**
	 * 删除指定的会议
	 * 能够判断指定的会议是否存在
	 * @param meeting_id
	 * @return 
	 * @throws SQLException
	 * @throws RemoteException 
	 */
	public String deleteConference(String user_name,String passwd,String meeting_id) throws SQLException, RemoteException{
		
		StringBuilder sb = new StringBuilder();
		
		//首先验证登录
		if(this.verifyLogin(user_name, passwd)){
			//验证指定会议是否存在
			boolean conferenceflag = false;
			conferenceflag = this.conferenceExistById(meeting_id);
			if (conferenceflag){
				int deleteflag = -1;
				//构造删除sql语句
				String user_id = this.getUserIdByName(user_name);
				sql = "delete meeting from meeting where meeting_createrId='" + user_id + "' and meeting_id='" + meeting_id + "';";
				deleteflag = this.connection.updateSQL(sql);
				if (1 == deleteflag){
					//会议删除成功的提示信息
					System.out.println("Delete conference success");
					sb.append("Delete conference success\n");
				} else {
					//会议删除失败的提示信息
					System.out.println("Delete conference failure, Insufficient permissions or parameter errors");
					sb.append("Delete conference failure, Insufficient permissions or parameter errors\n");
				}
			} else {
				//会议不存在，输出提示信息
				System.out.println("Conference does not exist");
				sb.append("Conference does not exist");
			}
		} else {
			sb.append("Login failure:unknown user name or bad password\n");
		}
		
		return sb.toString();
	}
/////////////////////////////////////////////清除用户的全部会议////////////////////////////////////////////////
	/**
	 * 通过用户名删除这个人创建的所有会议
	 * 这个函数会判断是否存在这个人
	 * @param user_name
	 * @return 
	 * @throws SQLException
	 * @throws RemoteException 
	 */
	public String clearConference(String user_name,String passwd) throws SQLException, RemoteException{
		
		StringBuilder sb = new StringBuilder();
		
		//首先验证登录
		if(this.verifyLogin(user_name, passwd)){
			//获得用户的id
			String user_id = this.getUserIdByName(user_name);
			//用户存在，进行删除会议的操作
			boolean clearflag = this.deleteConferenceByUserId(user_id);
			if(clearflag){
				//会议删除成功的提示信息
				System.out.println("All of conference deleted success");
				sb.append("All of conference deleted success\n");
			} else {
				//会议删除失败的提示信息
				System.out.println("All of conference deleted failure");
				sb.append("All of conference deleted failure\n");
			}
		} else {
			sb.append("Login failure:unknown user name or bad password\n");
		}
		return sb.toString();
	}
/////////////////////////////////////////////用户在某时间段是否有冲突////////////////////////////////////////////////
	/**
	 * 判断用户在指定的时间段内是否有空闲
	 * @param user_id
	 * @param starttime
	 * @param endtime
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public boolean haveUserTimeConflict(String user_id,String starttime,String endtime) throws SQLException, ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date Start = null,End = null;
		Date cStart = null,cEnd = null;
		try {
			Start = sdf.parse(starttime);
			End = sdf.parse(endtime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		sql = "select * from record r1 left join meeting m1 on r1.record_meeting_id=m1.meeting_id "
				+ "where r1.record_user_id='" + user_id +"' order by meeting_startDatetime;";
		ResultSet conferenceSet = this.connection.executeSQL(sql);
		
		//循环判断组合查询记录表中的指定用户参与的会议
		while(conferenceSet.next()){
			String starttimestr = null;
			String endtimestr = null;
			starttimestr = conferenceSet.getString(7);
			endtimestr = conferenceSet.getString(8);
			
			cStart = sdf.parse(starttimestr);
			cEnd = sdf.parse(endtimestr);
			if((Start.getTime() <= cEnd.getTime() && Start.getTime() >= cStart.getTime())
					|| (End.getTime() >= cStart.getTime() && End.getTime() <= cEnd.getTime())){
				return true;
			}
		}
		return false;
	}
	/**
	 * 关闭与数据库的连接
	 */
	public void close()throws RemoteException{
		this.connection.closeConnecetion();
	}

}
