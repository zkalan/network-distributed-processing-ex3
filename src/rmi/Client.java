package rmi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;

public class Client {

	//用户名
	String USER = "user4";
	//用户密码
	String PASSWD = "user4";
	//服务器端口
	static int PORT = 8080;
	
	static String registryURL = "rmi://localhost:"+PORT+"/conference";
	
	public static void main(String[] args) {
		Client client = new Client();
		ServiceInterface service = null;
		try{
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			service = (ServiceInterface)Naming.lookup(registryURL);
			
			String command = null;
			String[] commandFormat = null;
			while(true){
				
				boolean loginflag = false;
				//输出提示用户输入的符号
				client.printBBB();
				
				command = br.readLine();
				//对登陆前的命令进行格式化
				commandFormat = client.commandFormatBeforeLogin(command);
				
				if(commandFormat[0].equals("Register")){
					//进行注册
				} else if(commandFormat[0].equals("Login")){
					//进行登陆验证
					loginflag = service.verifyLogin(commandFormat[1],commandFormat[2]);
					if (loginflag){
						System.out.println("Login Success");
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
				while(loginflag){
					System.out.println("Login Success---");
					break;
				}
				if (service != null){
					service.close();
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
		if(formatcommand[0].equals("Login") || formatcommand[0].equals("login")){
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
		} else if(formatcommand[0].equals("Register") || formatcommand[0].equals("register")){
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
	 * 登陆前
	 * 打印帮助信息
	 */
	public void meunBeforeLogin(){
		System.out.println("RMI Menu");
		System.out.println("    1. Register");
		System.out.println("    2. Login");
		System.out.println("    3. Help");
		System.out.println("    4. Quit");
	}
	/**
	 * 登陆后
	 * 打印帮助信息
	 */
	public void menu(){
		System.out.println("RMI Menu");
		System.out.println("    1. Register");
		System.out.println("    2. Add conference");
		System.out.println("    3. Search conference");
		System.out.println("    4. Delete conference");
		System.out.println("    5. Clear conference");
		System.out.println("    6. Logout");
	}
	/**
	 * 输出一个好看的命令提示符
	 */
	public void printBBB(){
		System.out.print(USER + " $ ");
	}

}
