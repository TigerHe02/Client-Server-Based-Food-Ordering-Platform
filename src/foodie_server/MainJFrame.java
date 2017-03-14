package foodie_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import javax.swing.JFrame;

import MyTool.Customer;
import MyTool.Seller;

public class MainJFrame extends JFrame{
	private ServerSocket server;
	public LinkedList<Customer> customerList;
	public LinkedList<Seller> sellerList;
	LinkedList<Socket> socketList;
	public static void main(String args[])
	{
		new MainJFrame("服务端");
	}
	public MainJFrame(String s)
	{
		super(s);
		customerList = new LinkedList<Customer>();
		sellerList = new LinkedList<Seller>();
		socketList = new LinkedList<Socket>();
		startServer();
		//LinkMySql link = new LinkMySql();
	}
	void startServer() {  
        int i = 0;  
        try { 
        	//设置监听端口号和最大接入数
            server = new ServerSocket(8889, 100);  
            System.out.println("==========start===========快点来啊"); 
            while (true) {  
                Socket socket = server.accept();  
                i++;  
                System.out.println("第" + i + "个用户连接成功！"); 
                System.out.println("该用户端的地址信息为:"+socket.getInetAddress());
                //每次建立一个用户连接就开始一个新的serverThread处理该连接
                new Thread(new ServerThread(socket,this)).start();  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}
