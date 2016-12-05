package rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
/**
 * 创建RMI注册表，启动RMI服务，并将远程对象注册到RMI注册表中
 * @author zk
 *
 */
public class Server {

	//服务端口
	private static int PORT = 8080;
	
	public static void main(String[] args) {
		
		//提供方法调用的地址
		String registerURL = "rmi://localhost:" + PORT + "/conference";
		
		try {
			Service service = new Service();
			LocateRegistry.createRegistry(PORT);
			Naming.bind(registerURL, service);
			System.out.println("Service is up...");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
