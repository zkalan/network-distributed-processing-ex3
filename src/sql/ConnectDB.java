package sql;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Connection;



public class ConnectDB {
	
	/**
	 * 连接mysql服务器的方法类
	 */
	/**
	 * 数据库的信息
	 */
	//URl
	public static final String URL = "";
	//driver name
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	//user name
	public static final String USERNAME = "root";
	//user password
	public static final String PASSWD = "";
	
	public Connection connection = null;
	
	public PreparedStatement preparedStatement = null;
	
	/**
	 * 连接数据库
	 */
	public ConnectDB(){
		try {
			//指定驱动器
			Class.forName(DRIVER);
			//获得连接实例
			connection = DriverManager.getConnection(URL, USERNAME, PASSWD);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行并且获得单个结果集合
	 * @param sql
	 * @return
	 */
	public ResultSet executeSQL(String sql){
		try {
			preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			return resultSet;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 
	 */
	public void closeConnecetion(){
		try {
			this.connection.close();
			this.preparedStatement.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	

}
