package rmi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
/////////////////////////////////////////添加会议////////////////////////////////////////////////////
	/**
	 * 插入会议
	 * @param username
	 * @param passwd
	 * @param otheruser
	 * @param starttime
	 * @param endtime
	 * @param title
	 * @throws SQLException
	 * @throws ParseException 
	 */
	public void addConference(String username,String passwd,String otheruser,String starttime,String endtime,String title)throws SQLException, ParseException{
		//首先判断用户是否登录成功
		boolean loginflag = this.verifyLogin(username,passwd);
		//然后判断该用户名下是否有同名会议
		boolean meetingflag = this.conferenceExist(username, title);
		if(loginflag && !meetingflag){
			//获取用户的id
			String user_id = this.getUserIdByName(username);
			//首先判断这个用户是否有时间创建这个会议
			if(haveUserTimeConflict(user_id,starttime,endtime)){
				System.out.println("User time conflict, conference create failure");
			} else {
				//创建会议
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
					if(otheruser.equals(null) || otheruser.equals("") || otheruser.matches(";+")){
						System.out.println("please input attender, at least one person");
					} else {
						this.recordInsert(user_id, meeting_id);
						this.addConferenceAttender(meeting_id, otheruser,starttime,endtime);
					}
				} else {
					System.out.println("conference create failure");
				}
			}
		}
		if (true == meetingflag){
			System.out.println("Conference " + title + " already exists");
		}
	}
///////////////////////////////////////////添加会议参与者//////////////////////////////////////////////////
	/**
	 * 创建会议参与者
	 * @param meeting_id
	 * @param otheruser
	 * @throws SQLException 
	 * @throws ParseException 
	 */
	public void addConferenceAttender(String meeting_id,String otheruser,String starttime,String endtime) throws SQLException, ParseException{
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
				String user_id = this.getUserIdByName(otherUser[i]);
				//该用户是否已经加入了该会议
				boolean alreadyAttend = this.recordOfUserInConferenceExist(meeting_id,user_id);
				if (alreadyAttend){
					System.out.println("User " + otherUser[i] + " already join in this Conference");
				} else {
					//首先判断这个用户是否存在时间冲突
					if (haveUserTimeConflict(user_id,starttime,endtime)){
						System.out.println("User " + otherUser[i] + " has time conflict");
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
		//如果创建会议的参与人数小于2
		//删除这个会议并且输出提示信息
		int attendernumber = this.getNumberOfConferenceAttender(meeting_id);
		if(attendernumber < 2){
			//删除这个会议输出信息
			boolean deleteflag = this.deleteConferenceById(meeting_id);
			if(deleteflag){
				System.out.println("Conference deleted, because number of attenders less than 2");
			} else {
				System.out.println("Number of attenders less than 2 but conference deleted failure");
			}
		}
	}
/////////////////////////////////////////////查询指定时间段内的会议////////////////////////////////////////////////
	
	/**
	 * 查询指定时间段内的会议
	 * 按照时间排序 升序
	 * @param user_name
	 * @param starttime
	 * @param endtime
	 * @throws SQLException
	 * @throws ParseException
	 */
	public void conferenceSearch(String user_name,String starttime,String endtime) throws SQLException, ParseException{
		//统计有多少会议满足时间要求
		int i = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date Start = sdf.parse(starttime);
		Date End = sdf.parse(endtime);
		Date cStart = null,cEnd = null;
		String user_id = this.getUserIdByName(user_name);
		ResultSet conferenceSet = this.searchRecordByUserId(user_id);
		System.out.println("id | title | CId | meeting_startDatetime | meeting_endDatetime | attender");
		//循环获得记录表中的指定用户参与的会议
		while(conferenceSet.next()){
			String meeting_id = conferenceSet.getString(3);
			ResultSet conference = this.searchMeetingById(meeting_id);
			String starttimestr = null,endtimestr = null;
			//判断某一个会议是否满足要求
			while (conference.next()){
				starttimestr = conference.getString(4);
				endtimestr = conference.getString(5);
			}
			cStart = sdf.parse(starttimestr);
			cEnd = sdf.parse(endtimestr);
			if(cStart.getTime() >= Start.getTime() && cEnd.getTime() <= End.getTime()){
				this.printConference(meeting_id);
				i++;
			}
		}
		if (0 == i){
			System.out.println("Can not find any conference");
		}
	}
/////////////////////////////////////////////打印指定会议的信息////////////////////////////////////////////////
	/**
	 * 打印会议的信息以及参与者的id
	 * @param meeting_id
	 * @throws SQLException
	 */
	public void printConference(String meeting_id) throws SQLException{
		sql = "select * from meeting where meeting_id='" + meeting_id + "';";
		ResultSet rs = connection.executeSQL(sql);
		while (rs.next()){
			System.out.print(rs.getString(1) + " | ");
			System.out.print(rs.getString(2) + " | ");
			System.out.print(rs.getString(3) + " | ");
			System.out.print(rs.getString(4) + " | ");
			System.out.print(rs.getString(5) + " | ");
		}
		rs = this.searchRecordByConferenceId(meeting_id);
		while (rs.next()){
			System.out.print(rs.getString(2) + ", ");
		}
		System.out.print("\n");
	}

	///////////////////////////////////删除指定会议///////////////////////////////////////////////////
	/**
	 * 删除指定的会议
	 * 能够判断指定的会议是否存在
	 * @param meeting_id
	 * @throws SQLException
	 */
	public void deleteConference(String meeting_id) throws SQLException{
		//验证指定会议是否存在
		boolean conferenceflag = false;
		conferenceflag = this.conferenceExistById(meeting_id);
		if (conferenceflag){
			boolean deleteflag = false;
			deleteflag = this.deleteConferenceById(meeting_id);
			if (deleteflag){
				//会议删除成功的提示信息
				System.out.println("Delete conference success");
			} else {
				//会议删除失败的提示信息
				System.out.println("Delete conference failure");
			}
		} else {
			//会议不存在，输出提示信息
			System.out.println("Conference does not exist");
		}
	}
/////////////////////////////////////////////清除用户的全部会议////////////////////////////////////////////////
	/**
	 * 通过用户名删除这个人创建的所有会议
	 * 这个函数会判断是否存在这个人
	 * @param user_name
	 * @throws SQLException
	 */
	public void clearConference(String user_name) throws SQLException{
		//获得用户的id
		String user_id = this.getUserIdByName(user_name);
		//确认这个人是否存在
		boolean userexist = this.userExist(user_name);
		if(userexist){
			//用户存在，进行删除会议的操作
			boolean clearflag = this.deleteConferenceByUserId(user_id);
			if(clearflag){
				//会议删除成功的提示信息
				System.out.println("All of conference deleted success");
			} else {
				//会议删除失败的提示信息
				System.out.println("All of conference deleted failure");
			}
		} else {
			//用户不存在，输出提示信息
			System.out.println("User does not exist");
		}
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
		ResultSet userconferencerecord = this.searchRecordByUserId(user_id);
		while(userconferencerecord.next()){
			String meeting_id = userconferencerecord.getString(3);
			ResultSet conference = this.searchMeetingById(meeting_id);
			String starttimestr = null,endtimestr = null;
			while (conference.next()){
				starttimestr = conference.getString(4);
				endtimestr = conference.getString(5);
			}
			cStart = sdf.parse(starttimestr);
			cEnd = sdf.parse(endtimestr);
			if ((Start.getTime() < cEnd.getTime() && Start.getTime() > cStart.getTime())
					|| (End.getTime() > cStart.getTime() && End.getTime() < cEnd.getTime())){
				return true;
			}
		}
		return false;
	}
	/**
	 * 关闭与数据库的连接
	 */
	public void close(){
		this.connection.closeConnecetion();
	}

}
