package rmi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;

public class Client {

	//用户名
	static String USER = "user4";
	//用户密码
	static String PASSWD = "user4";
	//服务器端口
	static int PORT = 8080;
	
	
	
	static String registryURL = "rmi://localhost:"+PORT+"/conference";
	
	public static void main(String[] args) {
		Client client = new Client();
		ServiceInterface service = null;
		try{
			
			//接受用户输入的信息
			BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
			
			service = (ServiceInterface)Naming.lookup(registryURL);
			
			service.register("user8", "user8");
			
			if (cin.readLine().equals("help")){
				client.menu();
			}
			
			service.close();
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 打印帮助信息
	 */
	public void menu(){
		System.out.println("RMI Menu");
		System.out.println("    1. Register");
		System.out.println("    2. Add conference");
		System.out.println("    3. Search conference");
		System.out.println("    4. Delete conference");
		System.out.println("    5. Clear conference");
		System.out.println("    6. Quit");
	}

}
