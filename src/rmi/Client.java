package rmi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;

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
					
					
					
					
					
					break;
				}
			}
			
			//接受用户输入的信息
//			BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
//			
//			service = (ServiceInterface)Naming.lookup(registryURL);
//			
//			if (cin.readLine().equals("help")){
//				client.menu();
//			}
			
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
			//对用户信息进行注册
			String cd = "Help";
			response = cd.split(" ");
		} else if(formatcommand[0].equals("Quit") || formatcommand[0].equals("quit")){
			//对用户信息进行注册
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
	
	public String[] commandFormatAfterLogin(String command){
		
		String[] response = null;
		String[] formatcommand = command.split(" ");
		
		if (formatcommand[0].equals("add") && 7 == formatcommand.length){
			//添加会议
			
		} else if (formatcommand[0].equals("search") && 5 == formatcommand.length){
			//搜索
		} else if (formatcommand[0].equals("delete") && 2 == formatcommand.length){
			
		} else if (formatcommand[0].equals("clear")){
			
		} else if (formatcommand[0].equals("logout")){
			
		} else{
			//非法命令
			String cd = "IIIegal ";
			response = cd.split(" ");
		}
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
		System.out.println("    1. Add conference\n        CMD: add <title> <user1;user2> <start time> <start time> <end time> <end time>");
		System.out.println("    2. Search conference\n        CMD: search <start time> <start time> <end time> <end time>");
		System.out.println("    3. Delete conference\n         CMD: delete <conference ID>");
		System.out.println("    4. Clear conference\n         CMD: clear");
		System.out.println("    5. Logout\n         CMD: logout");
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

}
