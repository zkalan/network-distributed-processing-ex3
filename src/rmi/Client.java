package rmi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {

	//用户名
	String USER = null;
	//用户密码
	String PASSWD = null;
	//服务器端口
	static int PORT = 8080;
	
	static String registryURL = "rmi://localhost:"+PORT+"/conference";
	
	public static void main(String[] args) {
		Client client = new Client();
		ServiceInterface service = null;
		try{
			
			client.welcome();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			String command = null;
			String[] commandFormat = null;
			//外循环
			//主要完成不需要验证登录的功能
			while(true){
				service = (ServiceInterface)Naming.lookup(registryURL);
				//登记用户是否登陆成功的布尔值
				boolean loginflag = false;
				//输出提示用户输入的符号
				client.printBBB();
				//等待用户输入
				command = br.readLine();
				//对登陆前的命令进行格式化
				commandFormat = client.commandFormatBeforeLogin(command);
				
				if(commandFormat[0].equals("Register")){
					//进行注册
					System.out.println(service.register(commandFormat[1],commandFormat[2]));
					
				} else if(commandFormat[0].equals("Login")){
					//进行登陆验证
					loginflag = service.verifyLogin(commandFormat[1],commandFormat[2]);
					if (loginflag){
						System.out.println("Login Success");
						//将用户登陆成功的信息保存下来
						client.USER = commandFormat[1];
						client.PASSWD = commandFormat[2];
					} else {
						System.out.println("Login failure");
					}
				} else if(commandFormat[0].equals("Help")){
					//输出帮助信息
					client.meunBeforeLogin();
				} else if(commandFormat[0].equals("Quit")){
					//退出客户端
					System.out.println("Bye");
					if (service != null){
						service.close();
					}
					return;
				} else {
					System.out.println("IIIegal command ，please re-enter");
				}
				//仅当通过验证
				//内循环
				//主要实现需要验证用户身份的功能
				while(loginflag){
					service = (ServiceInterface)Naming.lookup(registryURL);
					//输出一个好看的命令提示
					client.printCCC();
					//等待用户输入
					command = br.readLine();
					//格式化用户的输入
					commandFormat = client.commandFormatAfterLogin(command);
					if (commandFormat[0].equals("Add")){
						//添加会议
						try{
							String starttime = client.spiderDateAndTime(commandFormat[3], commandFormat[4]);
							String endtime = client.spiderDateAndTime(commandFormat[5], commandFormat[6]);
							System.out.println(service.addConference(client.USER, client.PASSWD, 
									commandFormat[2], 
									starttime, 
									endtime,
									commandFormat[1]));
						} catch (Exception e){
							System.out.println("please check the time format <S yyyy-MM-dd> <hh:mm:ss> <E yyyy-MM-dd> <hh:mm:ss>");
						}
					} else if (commandFormat[0].equals("Search")){
						//搜索会议
						System.out.println(service.conferenceSearch(client.USER, client.PASSWD,
								client.spiderDateAndTime(commandFormat[1], commandFormat[2]), 
								client.spiderDateAndTime(commandFormat[3], commandFormat[4])));
					} else if (commandFormat[0].equals("Delete")){
						//删除会议
						System.out.println(service.deleteConference(client.USER, client.PASSWD, commandFormat[1]));
					} else if (commandFormat[0].equals("AddAttender")){
						//添加单个参与者
						System.out.println(service.addConferenceAttender(commandFormat[1], commandFormat[2]));
					} else if (commandFormat[0].equals("DeleteAttender")){
						//删除单个参与者
						System.out.println(service.deleteConferenceAttender(commandFormat[1], commandFormat[2]));
					} else if (commandFormat[0].equals("Clear")){
						//清除会议
						System.out.println(service.clearConference(client.USER, client.PASSWD));
					} else if (commandFormat[0].equals("Help")){
						//帮助
						client.menu();
					} else if (commandFormat[0].equals("Logout")){
						//退出登录
						client.USER = null;
						client.PASSWD = null;
						System.out.println("Logout success");
						break;
					} else if (commandFormat[0].equals("IIIegal")){
						//非法命令
						System.out.println("IIIegal command ，please re-enter");
					}
				}
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 将用户在验证登陆之前的命令进行格式化
	 * @param command
	 * @return
	 */
	public String[] commandFormatBeforeLogin(String command){
		String[] response = null;
		String[] formatcommand = command.split(" ");
		if((formatcommand[0].equals("Login") || formatcommand[0].equals("login")) && 3 == formatcommand.length){
			//对用户信息进行验证
			String cd = "Login " + formatcommand[1] + " "+ formatcommand[2];
			response = cd.split(" ");
		} else if(formatcommand[0].equals("Help") || formatcommand[0].equals("help")){
			//输出帮助信息
			String cd = "Help";
			response = cd.split(" ");
		} else if(formatcommand[0].equals("Quit") || formatcommand[0].equals("quit")){
			//退出客户端
			String cd = "Quit";
			response = cd.split(" ");
		} else if((formatcommand[0].equals("Register") || formatcommand[0].equals("register"))&& 3 == formatcommand.length){
			//对用户信息进行注册
			String cd = "Register " + formatcommand[1] + " "+ formatcommand[2];
			response = cd.split(" ");
		} else{
			//非法命令
			String cd = "IIIegal ";
			response = cd.split(" ");
		}
		return response;
	}
	
	/**
	 * 对用户登录之后的命令进行格式化
	 * 需要特别注意对时间的格式化
	 * 第一步先不考虑对时间的格式化
	 * 第二部等到所有其他功能完成后再进行时间的格式化
	 * @param command
	 * @return
	 */
	public String[] commandFormatAfterLogin(String command){
		
		String[] response = null;
		String[] formatcommand = command.split(" ");
		
		if (formatcommand[0].equals("add") && 7 == formatcommand.length){
			//添加会议
			String cd = "Add " + formatcommand[1] + " " + formatcommand[2] 
					+ " " + formatcommand[3] + " " + formatcommand[4]
					+ " " + formatcommand[5] + " " + formatcommand[6];
			response = cd.split(" ");
		} else if (formatcommand[0].equals("help")){
			//输出帮助信息
			String cd = "Help ";
			response = cd.split(" ");
		} else if (formatcommand[0].equals("search") && 5 == formatcommand.length){
			//搜索
			String cd = "Search " + formatcommand[1] + " " + formatcommand[2] 
					+ " " + formatcommand[3] + " " + formatcommand[4];
			response = cd.split(" ");
		} else if (formatcommand[0].equals("deleteattender") && 3 == formatcommand.length){
			//删除某个参与者
			String cd = "DeleteAttender " + formatcommand[1] + " " + formatcommand[2];
			response = cd.split(" ");
		} else if (formatcommand[0].equals("addattender") && 3 == formatcommand.length){
			//添加某个参与者
			String cd = "AddAttender " + formatcommand[1] + " " + formatcommand[2];
			response = cd.split(" ");
		} else if (formatcommand[0].equals("delete") && 2 == formatcommand.length){
			//删除指定会议
			String cd = "Delete " + formatcommand[1];
			response = cd.split(" ");
		} else if (formatcommand[0].equals("clear")&& 1 == formatcommand.length){
			//清除所有会议
			String cd = "Clear ";
			response = cd.split(" ");
		} else if (formatcommand[0].equals("logout")&& 1 == formatcommand.length){
			//退出当前登录
			String cd = "Logout ";
			response = cd.split(" ");
		} else{
			//非法命令
			String cd = "IIIegal ";
			response = cd.split(" ");
		}
		return response;
	}
	/**
	 * 登陆前
	 * 打印信息
	 */
	public void welcome(){
		System.out.println("#########Welcome to my RMI Client#########");
		this.meunBeforeLogin();
		System.out.println("##########################################");
	}
	
	/**
	 * 登陆前
	 * 打印帮助信息
	 */
	public void meunBeforeLogin(){
		System.out.println("RMI Menu");
		System.out.println("    1. Register\n        CMD: register <user name> <pass word>");
		System.out.println("    2. Login\n        CMD: login <user name> <pass word>");
		System.out.println("    3. Help\n        CMD: help");
		System.out.println("    4. Quit\n        CMD: quit");
	}
	/**
	 * 登陆后
	 * 打印帮助信息
	 */
	public void menu(){
		System.out.println("RMI Menu");
		System.out.println("    1. Add conference\n        CMD: add <title> <user1;user2> <S yyyy-MM-dd> <hh:mm:ss> <E yyyy-MM-dd> <hh:mm:ss>");
		System.out.println("    2. Search conference\n        CMD: search <S yyyy-MM-dd> <hh:mm:ss> <E yyyy-MM-dd> <hh:mm:ss>");
		System.out.println("    3. Delete conference\n         CMD: delete <conference ID>");
		System.out.println("    4. Clear conference\n         CMD: clear");
		System.out.println("    5. Logout\n         CMD: logout");
		System.out.println("   *6. Delete attender\n         CMD: deleteattend <conference ID> <user name>");
		System.out.println("   *7. Add attender\n         CMD: addattender <conference ID> <user name>");
	}
	/**
	 * 输出一个好看的命令提示符
	 */
	public void printBBB(){
		System.out.print(" $ ");
	}
	/**
	 * 输出一个好看的命令提示符
	 */
	public void printCCC(){
		System.out.print(USER + " # ");
	}
	
	/**
	 * 对时间和日期进行连接
	 * 并且将其格式化为yyyy-MM-dd hh:mm:ss
	 * @param date
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	public String spiderDateAndTime(String date,String time){
		Date SE = null;
		String formatDateTime = null;
		String datetime = date + " " + time;
		//对日期和时间的格式化格式
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			SE = formatter.parse(datetime);
		} catch (ParseException e) {
			
		}
		formatDateTime = formatter.format(SE);
		return formatDateTime;
	}

}
