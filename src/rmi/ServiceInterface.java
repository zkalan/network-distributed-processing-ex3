package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.ParseException;

public interface ServiceInterface extends Remote{
	
	/**
	 * 
	 */
	static final long serialVersionUID = -3654859414036481202L;

	//注册函数接口
	public void register(String username,String passwd) throws SQLException,RemoteException;
	//添加会议的接口
	public void addConference(String username,String passwd,String otheruser,String starttime,String endtime,String title)
			throws SQLException, ParseException,RemoteException;
	//根据时间段查询会议的接口
	public void conferenceSearch(String user_name,String passwd,String starttime,String endtime) 
			throws SQLException, ParseException,RemoteException;
	//删除某个会议的接口
	public void deleteConference(String user_name,String passwd,String meeting_id) throws SQLException,RemoteException;
	//清除全部会议的接口
	public void clearConference(String user_name,String passwd) throws SQLException,RemoteException;
	//关闭数据库连接
	public void close()throws RemoteException;
}