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
		new MainJFrame("�����");
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
        	//���ü����˿ںź���������
            server = new ServerSocket(8889, 100);  
            System.out.println("==========start===========�������"); 
            while (true) {  
                Socket socket = server.accept();  
                i++;  
                System.out.println("��" + i + "���û����ӳɹ���"); 
                System.out.println("���û��˵ĵ�ַ��ϢΪ:"+socket.getInetAddress());
                //ÿ�ν���һ���û����ӾͿ�ʼһ���µ�serverThread���������
                new Thread(new ServerThread(socket,this)).start();  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}
