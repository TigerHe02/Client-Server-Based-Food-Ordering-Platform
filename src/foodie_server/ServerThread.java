package foodie_server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import MyTool.Customer;
import MyTool.Menu;
import MyTool.Seller;

public class ServerThread implements Runnable {  
  
	private MainJFrame mainJFrame;
    private Socket socket;
    private String accept;
    public Customer customer;
    public Seller seller;
    DataInputStream in = null;
    DataOutputStream out = null;
    LinkMySql linkMySql;	
	// ������̬ȫ�ֱ���
    public ServerThread(Socket socket, MainJFrame mainJFrame)
    {  
    	this.mainJFrame = mainJFrame;
        this.socket = socket;  
        // connect this socket to the sql
        linkMySql = new LinkMySql(this,socket);
    }  
  
    // ������Ϊһ���û��ṩ����  
    @Override  
    public void run() 
    {  
        try
        {  
            // ��ȡ�ͻ��˴�������Ϣ��DataInputStream  
        	in = new DataInputStream(socket.getInputStream());  
            // ��ͻ��˷�����Ϣ��DataOutputStream  
            out = new DataOutputStream(socket.getOutputStream());   
            System.out.println("��������ɣ�������");
            //������Ϣ��ʼ���ͻ��˽��棬�Ƽ��˵�
            linkMySql.init(in,out);
      while(true)
      {
            // ��ȡ���Կͻ��˵���Ϣ  
    	  //accept��Ҫִ�еĲ���
            accept = in.readUTF();  
            System.out.println(accept);
     
        if(accept.equals("CustomerLogin"))
        {
        	
            customer = new Customer();
                 customer.Customer_ID = in.readUTF();
                 customer.Customer_password = in.readUTF();
                 System.out.println("�û�����"+customer.Customer_ID+"\n���룺"+customer.Customer_password);
                 //��ѯ���ݿ⿴�û��Ƿ����
                 String user = linkMySql.query(customer);
                 out.writeUTF(user);
                 if(user.equals("customer"))
                 {
                	 //���û��ӵ�list��������û���Ϣ���û�����ͨ��out = new DataOutputStream(socket.getOutputStream());  
                	 customer.Customer_ip = socket.getInetAddress().toString();
                	 mainJFrame.customerList.add(customer);
                	 out.writeUTF(customer.Customer_name);
                	 out.writeDouble(customer.Customer_balance);
                	 out.writeInt(customer.Customer_integral);
                 }
        }
        else if(accept.equals("SellerLogin"))
        {
        	seller = new Seller();
        	seller.Seller_ID = in.readUTF();
        	seller.Seller_password = in.readUTF();
        	//��ѯseller��Ϣ
        	String user = linkMySql.query(seller);
        	out.writeUTF(user);
        	if(user.equals("seller"))
        	{
        		seller.Seller_ip = socket.getInetAddress().toString();
        		mainJFrame.sellerList.add(seller);
                out.writeUTF(seller.Seller_shopname);
                out.writeUTF(seller.Seller_address);
                out.writeUTF(seller.Seller_kind);
                out.writeUTF(seller.Seller_info);
                out.writeUTF(seller.Seller_tel);
                String parms[] = seller.Seller_imagePath.split("/");
                out.writeUTF(parms[2]);
                out.writeInt(seller.Seller_appraise);
                out.writeDouble(seller.Seller_income);
                //�������
                linkMySql.send_picture(seller.Seller_imagePath);
                out.write("end;end".getBytes());
    			out.write("\r".getBytes());
    			in.readInt();
                linkMySql.getMenu(seller);
        	}
        }
        else if(accept.equals("customerRegister"))
        {
        	//���ע��
        	linkMySql.customerRegister(in.readUTF(),in.readUTF(),in.readUTF());
        }
        else if(accept.equals("sellerRegister"))
        {
        	//����ע��
        	linkMySql.sellerRegister(in.readUTF(),in.readUTF(),in.readUTF(),in.readUTF(),in.readUTF(),in.readUTF());
        }
        else if(accept.equals("deleteMenu"))
        {
        	String menu_num = in.readUTF();
        	linkMySql.deleteMenu(menu_num);
        }
        else if(accept.equals("modifyImage"))
        {
        	String path = in.readUTF();
        	System.out.println("ͼƬ������Ϊ��"+path);
        	linkMySql.modifyName(in.readUTF(),in.readUTF(),in.readDouble());
        	linkMySql.modifyImage();
        }
        else if(accept.equals("modifyName"))
        {
        	linkMySql.modifyName(in.readUTF(),in.readUTF(),in.readDouble());
        }
        else if(accept.equals("addMenu"))
        {
        	Menu menu = new Menu();
        	menu.Seller_ID = in.readUTF();
        	menu.Menu_name = in.readUTF();
        	menu.Menu_price = in.readDouble();
        	menu.Menu_imagePath_s = in.readUTF();
        	linkMySql.addMenu(menu);
        	out.writeUTF(menu.Menu_num);
        }
        else if(accept.equals("sellerQueryOrder"))
        {
        	String Seller_ID = in.readUTF();
        	linkMySql.sellerQueryOrder(Seller_ID);
        	
        }
        else if(accept.equals("completeOrder"))
        {
        	linkMySql.completeOrder(in.readUTF());
        }
        else if(accept.equals("queryShop"))
        {
        	linkMySql.queryShop(in.readUTF(),in.readUTF(),in.readUTF());
        }
        else if(accept.equals("getShop"))
        {
        	linkMySql.getShop(in.readUTF());
        }
        else if(accept.equals("getMenu"))
        {
        	Seller seller = new Seller();
        	seller.Seller_ID = in.readUTF();
        	seller.Seller_kind = in.readUTF();
        	linkMySql.getMenu(seller);
        }
        else if(accept.equals("getShopping"))
        {
        	linkMySql.getShopping(in.readUTF());
        }
        else if(accept.equals("submitOrder"))
        {
        	linkMySql.submitOrder(in.readUTF());
        }
        else if(accept.equals("submitShopping"))
        {
        	//�ύ���ﳵ��Ϣ
        	linkMySql.submitShopping(in.readUTF());
        }
        else if(accept.equals("customerQueryOrder"))
        {
        	//��ѯ��ҵĶ���
        	linkMySql.customerQueryOrder(in.readUTF());
        }
        else if(accept.equals("payOrder"))
        {
        	//����������
        	linkMySql.payOrder(in.readUTF());
        }
        else if(accept.equals("end"))
        {
        	System.out.println("�ͻ����ѶϿ����ӣ�����");
        	break;
        }
        
      	}
        }    
        catch (IOException e)
        {  
        	System.out.println("�Ҹ��ܵ�������������Ķ���");
            e.printStackTrace();  
        }
    }
}  