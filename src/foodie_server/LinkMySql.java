package foodie_server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import MyTool.Customer;
import MyTool.Menu;
import MyTool.Seller;

public class LinkMySql {
	Connection conn = null;
	Statement st = null;
	DataInputStream in = null;
    DataOutputStream out = null;
    String sql;
    ResultSet rs ;
    Menu menu[];	
    private Socket socket;
    ServerThread server;
	public LinkMySql(ServerThread server, Socket socket)
	{
		this.server = server;
		this.socket = socket;
	}
	public LinkMySql()
	{
		
	}
	//�˿͵�½
	public  String query(Customer customer)
	{
		String user = "defeat";
		conn = getConnection();	//ͬ����Ҫ��ȡ���ӣ������ӵ����ݿ�
		try {
			sql = "select * from customer where Customer_ID='"+customer.Customer_ID+"' and Customer_password='"+customer.Customer_password+"' ";		// ��ѯ���ݵ�sql���
			System.out.println(sql);
			st = (Statement) conn.createStatement();	//��������ִ�о�̬sql����Statement����st���ֲ�����
			rs = st.executeQuery(sql);	//ִ��sql��ѯ��䣬���ز�ѯ���ݵĽ����  
			System.out.println("���Ĳ�ѯ���Ϊ��");
			if(rs.next()==false)
			{
				conn.close();	//�ر����ݿ�����
				System.out.println("��ѯ����������");
				return user;
			}
			else
			{
				user = "customer";
				//�޸ĸ�cunstomer�����֣�������Ϣ
				customer.Customer_name = rs.getString("Customer_name");
				customer.Customer_integral = rs.getInt("Customer_integral");
				customer.Customer_balance = rs.getDouble("Customer_balance");
				conn.close();
			}
		}
		 catch (SQLException e) {
			System.out.println("��ѯ����ʧ��");
		}
		return user;
	}
	
	//���ҵ�½
	public  String query(Seller seller)
	{
		String user = "defeat";
		conn = getConnection();	//ͬ����Ҫ��ȡ���ӣ������ӵ����ݿ�
		try {
			sql = "select * from seller where Seller_ID='"+seller.Seller_ID+"' and Seller_password='"+seller.Seller_password+"' ";		// ��ѯ���ݵ�sql���
			System.out.println(sql);
			st = (Statement) conn.createStatement();	//��������ִ�о�̬sql����Statement����st���ֲ�����
			rs = st.executeQuery(sql);	//ִ��sql��ѯ��䣬���ز�ѯ���ݵĽ����
			System.out.println("���Ĳ�ѯ���Ϊ��");
			if(rs.next()==false)
			{
				conn.close();	//�ر����ݿ�����
				System.out.println("��ѯ����������");
				return user;
			}
			else
			{
				user = "seller";
				seller.Seller_shopname = rs.getString("Seller_shopname");
				seller.Seller_address = rs.getString("Seller_address");
				seller.Seller_kind = rs.getString("Seller_kind");
				seller.Seller_info = rs.getString("Seller_info");
				seller.Seller_tel = rs.getString("Seller_tel");
				seller.Seller_income = rs.getDouble("Seller_income");
				seller.Seller_appraise = rs.getInt("Seller_appraise");
				seller.Seller_imagePath = rs.getString("Seller_imagePath");
				conn.close();
			}
		}
		 catch (SQLException e) {
			System.out.println("��ѯ����ʧ��");
		}
		System.out.println(user);
		return user;
	}
	
	//�˿�ע��
	public void customerRegister(String Customer_name,String Customer_password,String Customer_tel)
	{
		conn = getConnection();
		try{
			st = (Statement) conn.createStatement();
			//�ҵ�customer������ID����1�õ���ǰcustomer��ID
			//Ҳ�����Զ���pkΪ����1?
			sql = "select max(Customer_ID) Customer_ID_max from customer";
			rs = st.executeQuery(sql);
			rs.next();
			String Customer_ID = Integer.parseInt(rs.getString("Customer_ID_max"))+1+"";
			out.writeUTF(Customer_ID);
			sql = "insert into customer(Customer_ID,Customer_name,Customer_password,Customer_tel,Customer_balance,Customer_integral) "
					+"values('"+Customer_ID+"','"+Customer_name+"','"+Customer_password+"',"+Customer_tel+",100,100)";
			System.out.println(sql);
			st = (Statement) conn.createStatement();
			int count = st.executeUpdate(sql);
			System.out.println("��customer���в��� " + count + " ������");
			conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("�˿�ע��ʧ�ܣ�����");
		}
		catch(IOException e)
		{
			System.out.println("���ع˿���Ϣʧ�ܣ�����");
		}
	}
	
	//����ע��
	public void sellerRegister(String Seller_shopname,String Seller_password,String Seller_kind,String Seller_address,
			String Seller_tel,String Seller_info)
	{
		conn = getConnection();
		try{
			st = (Statement) conn.createStatement();
			sql = "select max(Seller_ID) Seller_ID_max from seller";
			rs = st.executeQuery(sql);
			rs.next();
			String Seller_ID = Integer.parseInt(rs.getString("Seller_ID_max"))+1+"";
			out.writeUTF(Seller_ID);
			if(Seller_info.equals("false"))
			sql = "insert into seller(Seller_ID,Seller_shopname,Seller_password,Seller_address,Seller_kind,Seller_info,Seller_tel,"
					+ "Seller_income,Seller_appraise,Seller_imagePath) values('"+Seller_ID+"','"+Seller_shopname+"','"+Seller_password
					+"','"+Seller_address+"','"+Seller_kind+"','',"+Seller_tel+",0,300,'E:/image/foodie.jpg')";
			else
				sql = "insert into seller(Seller_ID,Seller_shopname,Seller_password,Seller_address,Seller_kind,Seller_info,Seller_tel,"
						+ "Seller_income,Seller_appraise,Seller_imagePath) values('"+Seller_ID+"','"+Seller_shopname+"','"+Seller_password
						+"','"+Seller_address+"','"+Seller_kind+"','"+Seller_info+"',"+Seller_tel+",0,300,'E:/image/foodie.jpg')";		
				
			System.out.println(sql);
			st = (Statement) conn.createStatement();
			int count = st.executeUpdate(sql);
			System.out.println("��seller���в��� " + count + " ������");
			
			conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("����ע��ʧ�ܣ�����");
		}
		catch(IOException e)
		{
			System.out.println("���ص�����Ϣʧ�ܣ�����");
		}
	}
	//��ȡ���̵Ĳ˵�
	public void getMenu(Seller seller)
	{
		int menu_num,i=0;
		conn = getConnection();
		try{
			st = (Statement) conn.createStatement();
			sql = "select count(*) as rowCount from menu where Seller_ID= '" +seller.Seller_ID+"'";
			rs = st.executeQuery(sql);	
			rs.next();
			menu_num = rs.getInt("rowCount");
			menu = new Menu[menu_num];
			sql = "select * from menu where Seller_ID= '" +seller.Seller_ID+"'";
			rs = st.executeQuery(sql);
			out.writeInt(menu_num);
			System.out.println("��ѯ���Ĳ˵�����������"+menu_num);
			while (rs.next()) {	// �ж��Ƿ�����һ������
				menu[i] = new Menu();
				menu[i].Seller_kind = seller.Seller_kind;
				menu[i].Menu_num = rs.getString("Menu_num");
				menu[i].Menu_name = rs.getString("Menu_name");
				menu[i].Seller_ID = rs.getString("Seller_ID");
				menu[i].Menu_price = rs.getDouble("Menu_price");
				menu[i].Menu_appraise = rs.getInt("Menu_appraise");
				menu[i].Menu_imagePath_s = rs.getString("Menu_imagePath_s");
				menu[i].Menu_imagePath_b = rs.getString("Menu_imagePath_b");
				System.out.println(menu[i].Menu_num+menu[i].Menu_name+menu[i].Seller_ID+menu[i].Menu_price
						+menu[i].Menu_appraise);
				i++;
				}
			//��ѯ�˵���ɣ������ǰѲ˵���Ϣ���͵�client,���Էŵ�serverThread��
			for(int j=0;j<menu.length;j++)
			{
				out.writeUTF(menu[j].Menu_num);
				out.writeUTF(menu[j].Menu_name);
				out.writeUTF(menu[j].Seller_ID);
				out.writeUTF(menu[j].Seller_kind);
				out.writeDouble(menu[j].Menu_price);
				out.writeInt(menu[j].Menu_appraise);
				String[] parms = menu[j].Menu_imagePath_s.split("/");
				out.writeUTF(parms[2]);
				parms =  menu[j].Menu_imagePath_b.split("/");
				out.writeUTF(parms[2]);
			}
			for(int j=0;j<menu.length;j++)
			{
				send_picture(menu[j].Menu_imagePath_s);
				send_picture(menu[j].Menu_imagePath_b);
			}
			out.write("end;end".getBytes());
			out.write("\r".getBytes());
			conn.close();	//�ر����ݿ�����
		}
		catch(SQLException e)
		{
			System.out.println("��ѯ����ʧ��");
		}
		catch(IOException e)
		{
			System.out.println("��������ʧ��");
		}
	}
	
	//��ʼ���ͻ�����
	public void init(DataInputStream in,DataOutputStream out)
	{
		//��serverThread��in��out����
		this.in = in;
		this.out = out;
		int i = 0,menu_num;
		conn = getConnection();	
		try {
			//��ѯ���в˵�
			st = (Statement) conn.createStatement();
			sql = "select count(*) as rowCount from menu where Menu_appraise >300";
			rs = st.executeQuery(sql);	
			rs.next();
			menu_num = rs.getInt("rowCount");
			menu = new Menu[menu_num];
			sql = "select * from menu where Menu_appraise >300";
			rs = st.executeQuery(sql);
			out.writeInt(menu_num);
			while (rs.next()) {	// �ж��Ƿ�����һ������
			menu[i] = new Menu();
			menu[i].Menu_num = rs.getString("Menu_num");
			menu[i].Menu_name = rs.getString("Menu_name");
			menu[i].Seller_ID = rs.getString("Seller_ID");
			menu[i].Menu_price = rs.getDouble("Menu_price");
			menu[i].Menu_appraise = rs.getInt("Menu_appraise");
			menu[i].Menu_imagePath_s = rs.getString("Menu_imagePath_s");
			menu[i].Menu_imagePath_b = rs.getString("Menu_imagePath_b");
			System.out.println(menu[i].Menu_num+menu[i].Menu_name+menu[i].Seller_ID+menu[i].Menu_price
					+menu[i].Menu_appraise);
			i++;
			}
			for(int j=0;j<menu.length;j++)
			{
				sql = "select * from seller where Seller_ID ='"+menu[j].Seller_ID+"'";
				rs = st.executeQuery(sql);
				rs.next();
				menu[j].Seller_kind = rs.getString("Seller_kind");	
				System.out.println(menu[j].Seller_ID+" : "+menu[j].Seller_kind);
			}
			for(int j=0;j<menu.length;j++)
			{
				out.writeUTF(menu[j].Menu_num);
				out.writeUTF(menu[j].Menu_name);
				out.writeUTF(menu[j].Seller_ID);
				out.writeUTF(menu[j].Seller_kind);
				out.writeDouble(menu[j].Menu_price);
				out.writeInt(menu[j].Menu_appraise);
				String[] parms = menu[j].Menu_imagePath_s.split("/");
				out.writeUTF(parms[2]);
				parms =  menu[j].Menu_imagePath_b.split("/");
				out.writeUTF(parms[2]);
			}
			for(int j=0;j<menu.length;j++)
			{
				//send_picture��in��out���������in��out,�����ǰ�picture���͵��ͻ���
				send_picture(menu[j].Menu_imagePath_s);
				send_picture(menu[j].Menu_imagePath_b);
			}
			out.write("end;end".getBytes());
			out.write("\r".getBytes());
			conn.close();	//�ر����ݿ�����
		} catch (SQLException e) {
			System.out.println("��ѯ����ʧ��");
		}
		catch(IOException e)
		{
			System.out.println("��������ʧ��");
		}
	}	
	
	//���Ӳ˵�
	public void addMenu(Menu menu)
	{
		String menu_amount;
		conn = getConnection();	
		try {
			st = (Statement) conn.createStatement();
			sql = "select max(Menu_num) as rowCount from menu";
			rs = st.executeQuery(sql);	
			rs.next();
			menu_amount = Integer.parseInt(rs.getString("rowCount"))+1+"";
			menu.Menu_num = menu_amount;
			menu.Menu_appraise = 100;
			String name = menu.Menu_imagePath_s;
			//�ڷ��������ͼƬ��Ŀ¼
			menu.Menu_imagePath_s = "E:/image/"+name;
			String parms[] = name.split("_");
			menu.Menu_imagePath_b = "E:/image/"+parms[0]+"_b.jpg";
			System.out.println("�����˵��ı��Ϊ: "+menu.Menu_num);
			receive_picture();
			sql = "INSERT INTO menu(Menu_num, Menu_name, Seller_ID, Menu_price, Menu_appraise, Menu_imagePath_s, Menu_imagePath_b)"
					+ "VALUES ('"+menu.Menu_num+"','"+menu.Menu_name+"','"+menu.Seller_ID+"','"+menu.Menu_price+"','"+menu.Menu_appraise+
					"','"+menu.Menu_imagePath_s+"','"+menu.Menu_imagePath_b+"')";
			System.out.println(sql);
			st = (Statement) conn.createStatement();
			int count = st.executeUpdate(sql);
			System.out.println("��staff���в��� " + count + " ������");
			conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("��Ӳ˵�ʧ�ܣ�������Ұɣ�����");
		}
	}
	
	//���Ҳ�ѯ����
	public void sellerQueryOrder(String Seller_ID)
	{
		int order_amount;
		conn = getConnection();	
		try {
			st = (Statement) conn.createStatement();
			sql = "select count(*) as rowCount from myordertable where Seller_ID='"+Seller_ID+"' and Order_status ='pay'";
			System.out.println(sql);
			rs = st.executeQuery(sql);	
			rs.next();
			order_amount = rs.getInt("rowCount");
			System.out.println("��ѯ�����ݵ�����:"+order_amount);
			out.writeInt(order_amount);
			sql = "select * from myordertable where Seller_ID='"+Seller_ID+"' and Order_status = 'pay'";
			System.out.println(sql);
			rs = st.executeQuery(sql);
			while(rs.next())
			{
				out.writeUTF(rs.getString("Order_ID"));
				out.writeUTF(rs.getString("Order_num"));
				out.writeUTF(rs.getString("Menu_num"));
				out.writeInt(rs.getInt("Order_amount"));
				out.writeDouble(rs.getDouble("Order_price"));
				out.writeUTF(rs.getString("Customer_ID"));
				out.writeUTF(rs.getString("Order_handleStatus"));
				out.writeUTF(rs.getString("Order_time"));	
			}
		}
		catch(SQLException e)
		{
			System.out.println("���Ҷ�����ѯʧ�ܣ���Ҫ�����ң�����");
		}
		catch(IOException e)
		{
			System.out.println("�������ݴ���");
		}
	}
	
	//��Ҷ�����ѯ
	public void customerQueryOrder(String Customer_ID)
	{
		int order_amount;
		conn = getConnection();	
		try {
			st = (Statement) conn.createStatement();
			sql = "select count(*) as rowCount from myordertable,menu ,seller where myordertable.Seller_ID=seller.Seller_ID and "
					+ "myordertable.Menu_num=menu.Menu_num and Customer_ID='"+Customer_ID+"' and Order_status <>'non_submit'";
			System.out.println(sql);
			rs = st.executeQuery(sql);	
			rs.next();
			order_amount = rs.getInt("rowCount");
			System.out.println("��ѯ�����ݵ�����:"+order_amount);
			out.writeInt(order_amount);
			sql = "select * from myordertable,menu,seller where myordertable.Seller_ID=seller.Seller_ID and myordertable.Menu_num=menu.Menu_num"
					+ " and Customer_ID='"+Customer_ID+"' and Order_status <> 'non_submit'";
			System.out.println(sql);
			rs = st.executeQuery(sql);
			while(rs.next())
			{
				out.writeUTF(rs.getString("Order_ID"));
				out.writeUTF(rs.getString("Order_num"));
				out.writeUTF(rs.getString("Menu_num"));
				out.writeUTF(rs.getString("Menu_name"));
				out.writeInt(rs.getInt("Order_amount"));
				out.writeDouble(rs.getDouble("Order_price"));
				out.writeUTF(rs.getString("Seller_shopname"));
				out.writeUTF(rs.getString("Order_status"));
				out.writeUTF(rs.getString("Order_handleStatus"));
				out.writeUTF(rs.getString("Order_time"));	
			}
		}
		catch(SQLException e)
		{
			System.out.println("��Ҷ�����ѯʧ�ܣ���Ҫ�����ң�����");
		}
		catch(IOException e)
		{
			System.out.println("�������ݴ���");
		}
	}
	//��Ҹ���������
	public void payOrder(String order_num)
	{
		conn = getConnection();	
		try {
			st = (Statement) conn.createStatement();
			sql = "update myordertable set Order_status= 'pay' where Order_num='"+order_num+"'";
			System.out.println(sql);
			int count = st.executeUpdate(sql);
			System.out.println("��staff���и��� " + count + " ������");
			conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("��ɶ���ʧ�ܣ�������");
		}
	}
	//������ɶ���
	public void completeOrder(String order_num)
	{
		conn = getConnection();	
		try {
			st = (Statement) conn.createStatement();
			sql = "update myordertable set Order_handleStatus= 'complete' where Order_num='"+order_num+"'";
			System.out.println(sql);
			int count = st.executeUpdate(sql);
			System.out.println("��staff���и��� " + count + " ������");
			conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("��ɶ���ʧ�ܣ�������");
		}
	}
	
	//��ѯ����
	public void queryShop(String shopname,String address,String kind)
	{
		String sql2;
		int shop_amount;
		conn = getConnection();	
		try{
		st = (Statement) conn.createStatement();
		System.out.println(shopname + address +kind);
		if(shopname.equals("����"))
		{
			if(address.equals("����"))
			{
				sql = "select count(*) as rowCount from seller where Seller_kind='"+kind+"'";
				sql2 = "select * from seller where Seller_kind='"+kind+"'";
				
			}
			else if(kind.equals("����"))
			{
				sql = "select count(*) as rowCount from seller where Seller_address='"+address+"'";
				sql2 = "select * from seller where Seller_address='"+address+"'";
			}
			else
			{
				sql = "select count(*) as rowCount from seller where Seller_address='"+address+"'and Seller_kind='"+kind+"'";
				sql2 = "select * from seller where Seller_address='"+address+"'and Seller_kind='"+kind+"'";
			}
		}
		else if(address.equals("����"))
		{
			if(kind.equals("����"))
			{
				sql = "select count(*) as rowCount from seller where Seller_shopname='"+shopname+"'";
				sql2 = "select * from seller where Seller_shopname='"+shopname+"'";
			}
			else
			{
				sql = "select count(*) as rowCount from seller where Seller_shopname='"+shopname+"'and Seller_kind='"+kind+"'";
				sql2 = "select * from seller where Seller_shopname='"+shopname+"'and Seller_kind='"+kind+"'";
			}
		}
		else if(kind.equals("����"))
		{
			sql = "select count(*) as rowCount from seller where Seller_shopname='"+shopname+"'and Seller_address='"+address+"'";
			sql2 = "select * from seller where Seller_shopname='"+shopname+"'and Seller_address='"+address+"'";
		}
		else
		{
			sql = "select count(*) as rowCount from seller where Seller_shopname='"+shopname+"'and Seller_address='"+address+"'and "
					+ "Seller_kind= '"+kind+"'";
			sql2 = "select * from seller where Seller_shopname='"+shopname+"'and Seller_address='"+address+"'and "
					+ "Seller_kind= '"+kind+"'";
		}
		System.out.println(sql+"\n"+sql2);
		rs = st.executeQuery(sql);	
		rs.next();
		shop_amount = rs.getInt("rowCount");
		System.out.println("��ѯ�����ݵ�����:"+shop_amount);
		out.writeInt(shop_amount);
		rs = st.executeQuery(sql2);	
		Seller[] seller = new Seller[shop_amount];
		int i=0;
		while(rs.next())
		{
			seller[i] = new Seller();
			out.writeUTF(rs.getString("Seller_ID"));
			out.writeUTF(rs.getString("Seller_shopname"));
			out.writeUTF(rs.getString("Seller_address"));
			out.writeUTF(rs.getString("Seller_kind"));
			out.writeUTF(rs.getString("Seller_info"));
			seller[i].Seller_imagePath = rs.getString("Seller_imagePath");
			String parms[] = seller[i].Seller_imagePath.split("/");
			System.out.println(seller[i].Seller_imagePath);
			out.writeUTF(parms[2]);
			i++;
		}
		for(int j=0;j<shop_amount;j++)
		{
			send_picture(seller[j].Seller_imagePath);
		}
		out.write("end;end".getBytes());
		out.write("\r".getBytes());
		conn.close();	//�ر����ݿ�����
		}
		catch(SQLException e)
		{
			System.out.println("��ѯ������Ϣʧ�ܣ���");
		}		
		catch(IOException e)
		{
			System.out.println("��������ʧ�ܣ���");
		}
	}
	//��ȡ������Ϣ�����ݵ���ID
	public void getShop(String Seller_ID)
	{
		System.out.println("�ܵ������ˣ�"
				+ "");
		try
		{
			conn = getConnection();
			st = (Statement) conn.createStatement();
			sql = "select * from seller where Seller_ID = '"+Seller_ID+"'";
			System.out.println(sql);
			rs = st.executeQuery(sql);
			rs.next();
			out.writeUTF(rs.getString("Seller_ID"));
			out.writeUTF(rs.getString("Seller_shopname"));
			out.writeUTF(rs.getString("Seller_address"));
			out.writeUTF(rs.getString("Seller_kind"));
			out.writeUTF(rs.getString("Seller_info"));
			String Seller_imagePath = rs.getString("Seller_imagePath");
			String parms[] = Seller_imagePath.split("/");
			System.out.println(Seller_imagePath);
			out.writeUTF(parms[2]);
			send_picture(Seller_imagePath);
			out.write("end;end".getBytes());
			out.write("\r".getBytes());
			conn.close();	//�ر����ݿ�����
		}
		catch(SQLException e)
		{
			System.out.println("��ѯ������Ϣʧ�ܣ�����");
		}
		catch(IOException e)
		{
			System.out.println("���͵�����Ϣʧ�ܣ�����");
		}
	}
	//��ȡ���ﳵ��Ϣ
	public void getShopping(String Customer_ID)
	{
		int shopping_amount;
		//System.out.println("��ѯ�������ﳵ��Ϣ����");
		conn = getConnection();	
		try {
			st = (Statement) conn.createStatement();
			sql = "select count(*) as rowCount from myordertable,menu where myordertable.Menu_num= Menu.Menu_num and Customer_ID='"+Customer_ID+"' "
					+ "and Order_status ='non_submit'";
			System.out.println(sql);
			rs = st.executeQuery(sql);	
			rs.next();
			shopping_amount = rs.getInt("rowCount");
			System.out.println("��ѯ�����ݵ�����:"+shopping_amount);
			out.writeInt(shopping_amount);
			sql = "select * from myordertable,menu where myordertable.Menu_num= Menu.Menu_num and Customer_ID='"+Customer_ID+"' and Order_status ='non_submit'";
			//System.out.println(sql);
			rs = st.executeQuery(sql);
			while(rs.next())
			{
				out.writeUTF(rs.getString("Order_ID"));
				out.writeUTF(rs.getString("Order_num"));
				out.writeUTF(rs.getString("Menu_num"));
				out.writeInt(rs.getInt("Order_amount"));
				out.writeDouble(rs.getDouble("Order_price"));
				out.writeUTF(rs.getString("Customer_ID"));
				out.writeUTF(rs.getString("Seller_ID"));
				out.writeUTF(rs.getString("Order_time"));
				out.writeUTF(rs.getString("Menu_name"));
			}
		}
		catch(SQLException e)
		{
			System.out.println("��ҹ��ﳵ��ѯʧ�ܣ���Ҫ�����ң�����");
		}
		catch(IOException e)
		{
			System.out.println("�������ݴ���");
		}
	}
	
	//����ͼƬ��ʼ���ͻ��˽���
	public void send_picture(String path)
	{
		//buffer�������ͼƬ
		byte[] buffer = new byte[8192];         
		int readBytes = -1;   
		try
		{
		File image = new File(path);
		FileInputStream in = new FileInputStream(path); 
		String parms[] = path.split("/");
		String header = parms[2]+";"+image.length();
		out.write(header.getBytes());      
		out.write("\r".getBytes());           
		out.flush();                  
		while ((readBytes = in.read(buffer)) != -1) 
		{  
			//��ͼƬ������ͻ��ˡ������out����serveThread���out
			out.write(buffer, 0, readBytes);        
		}       
		out.flush();   
		in.close();
		//System.out.println("���ͳɹ���");
		}
		catch(FileNotFoundException e)
		{}
		catch(IOException e)
		{}
	}
	
	//����ͼƬ,��ͼƬ�ŵ�����������Ӧλ��
	public void receive_picture()
    {
        try { 
          boolean isEnd = false; 
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream()); 
            while (!isEnd) { 
                 int d = -1; 
                 StringBuilder header = new StringBuilder(); 
                 while ((d = in.read()) != '\r') { 
                      if (d == -1) { 
                            isEnd = true; 
                            break; 
                        } 
                        header.append((char) d); 
                    } 
                    if (!isEnd) { 
                        String[] parms = header.toString().split(";");
                        if(parms[1].equals("end"))
                        {
                            isEnd = true;
                            break;
                        }
                        FileOutputStream out = new FileOutputStream("E:/image/"+ parms[0]);
                        System.out.println("�ҵ��죺 "+"E:/image/"+parms[0]);
                        long size = Long.parseLong(parms[1]); 
                        while (size > 0 && (d = in.read()) != -1) { 
                            out.write(d); 
                            size--; 
                        } 
                        out.flush(); 
                        out.close(); 
                    } 
                } 
                //in.close();
            } catch (IOException e) { 
                throw new RuntimeException("��ȡ�ͻ���������ʧ��", e); 
            } 
    }
	//�޸Ĳ˵�ͼƬ
	public void modifyImage()
	{
		receive_picture();
		System.out.println("�޸���ɣ�");
	}
	//�޸Ĳ˵����ƺͼ۸�
	public void modifyName(String menu_num,String name,double price)
	{
		try{
		conn = getConnection();	
		sql = "update menu set Menu_name='"+name+"',Menu_price="+price+" where Menu_num='"+menu_num+"'";
		System.out.println(sql);
		st = (Statement) conn.createStatement();	
		int count = st.executeUpdate(sql);// ִ��sqlɾ����䣬����ɾ�����ݵ�����
		System.out.println("���и��� " + count + " ������\n");	//���ɾ�������Ĵ�����
		conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("��������ʧ��");
		}
	}
	//ɾ���˵�
	public void deleteMenu(String menu_num)
	{
		try{
		conn = getConnection();
		sql = "delete from menu where Menu_num='"+menu_num+"'";
		st = (Statement) conn.createStatement();
		int count = st.executeUpdate(sql);// ִ��sqlɾ����䣬����ɾ�����ݵ�����
		System.out.println("����ɾ�� " + count + " ������\n");	//���ɾ�������Ĵ�����
		conn.close();	//�ر����ݿ�����	
		}
		catch(SQLException e)
		{
			System.out.println("ɾ������ʧ��");
		}
	}
	
	//�ύ����
	public void submitOrder(String Order_num)
	{
		try{
		conn = getConnection();
		st = (Statement) conn.createStatement();
		if(Order_num.equals("false"))
		{
			sql = "select max(Order_num) order_num_max from myordertable";
			rs = st.executeQuery(sql);
			rs.next();
			String order_num_max = Integer.parseInt(rs.getString("order_num_max"))+1+"";
			Order_num = order_num_max;
			System.out.println(Order_num);
			
		}
		//����ʱ���ʽ
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//��ȡϵͳʱ�䲢ת�����ַ���
		String sdate=df.format(new Date());
		//System.out.println(sdate);
		String order_time = sdate;
		int non_submit = in.readInt();
		int order_size = in.readInt();
		for(int i=0;i<non_submit;i++)
		{
			String order_ID = in.readUTF();
			sql = "update myordertable set Order_status='non_pay' where Order_ID='"+order_ID+"'";
			int count = st.executeUpdate(sql);
			System.out.println("��myordertable���и��� " + count + " �����ݣ���non_submit����non_pay");
		}
		System.out.println("Ҫ�ύ������������ "+(order_size-non_submit));
		sql = "select max(Order_ID) order_ID_max from myordertable";
		rs = st.executeQuery(sql);
		rs.next();
		int order_ID_max = Integer.parseInt(rs.getString("order_ID_max"));
		for(int i=0;i<order_size-non_submit;i++)
		{
			String order_ID = ++order_ID_max+"";
	        sql = "insert into myordertable(Order_ID,Order_num,Order_price,Menu_num,Customer_ID,Seller_ID,Order_time,Order_status,Order_amount,"
	             + "Order_handleStatus) " + "values('"+order_ID+"','"+Order_num+"','"+in.readDouble()+"','"+in.readUTF()+"','"+in.readUTF()
	             +"','"+in.readUTF()+"','"+order_time+"','non_pay','"+in.readInt()+"','non_complete')";
	        //System.out.println(sql);
	        st = (Statement) conn.createStatement();
			int count = st.executeUpdate(sql);
			System.out.println("��myordertable���в��� " + count + " ������");
		}
		conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("�ͻ���Ӷ���ʧ��!!!");
		}
		catch(IOException e)
		{
			System.out.println("��������ʧ��!!!");
		}
	
	}
	
	//�ύ���ﳵ��Ϣ
	public void submitShopping(String Order_num)
	{
		try{
			conn = getConnection();
			st = (Statement) conn.createStatement();
			if(Order_num.equals("false"))
			{
				sql = "select max(Order_num) order_num_max from myordertable";
				rs = st.executeQuery(sql);
				rs.next();
				String order_num_max = Integer.parseInt(rs.getString("order_num_max"))+1+"";
				Order_num = order_num_max;
				System.out.println(Order_num);
				
			}
			//����ʱ���ʽ
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//��ȡϵͳʱ�䲢ת�����ַ���
			String sdate=df.format(new Date());
			System.out.println(sdate);
			String order_time = sdate;
			int non_submit = in.readInt();
			int order_size = in.readInt();
			System.out.println("Ҫ�ύ������������ "+(order_size-non_submit));
			sql = "select max(Order_ID) order_ID_max from myordertable";
			rs = st.executeQuery(sql);
			rs.next();
			int order_ID_max = Integer.parseInt(rs.getString("order_ID_max"));
			for(int i=0;i<order_size-non_submit;i++)
			{
				// read from the socket to get the menu, customer id
				String order_ID = ++order_ID_max+"";
		        sql = "insert into myordertable(Order_ID,Order_num,Order_price,Menu_num,Customer_ID,Seller_ID,Order_time,Order_status,Order_amount,"
		             + "Order_handleStatus) " + "values('"+order_ID+"','"+Order_num+"','"+in.readDouble()+"','"+in.readUTF()+"','"+in.readUTF()
		             +"','"+in.readUTF()+"','"+order_time+"','non_submit','"+in.readInt()+"','non_complete')";
		        //System.out.println(sql);
		        st = (Statement) conn.createStatement();
				int count = st.executeUpdate(sql);
				System.out.println("��myordertable���в��� " + count + " ������");
			}
			conn.close();
			}
			catch(SQLException e)
			{
				System.out.println("���湺�ﳵ��Ϣʧ��!!!");
			}
			catch(IOException e)
			{
				System.out.println("��������ʧ��!!!");
			}
	}
	/*
	public void handleOrder()
	{
		int order_num,num;
		float price;
		String name,shop,type;
		conn = getConnection();	
		String shop1_ip,shop2_ip,shop3_ip,passwd;
		InetAddress address; 
		
		try
		{
			
			st = (Statement) conn.createStatement();
			passwd = in.readUTF();
			Float consume = in.readFloat();
			sql = "select * from usertable where account ="+server.account+" ";
			rs = st.executeQuery(sql);
			rs.next();
			Float money = rs.getFloat("money");
//			System.out.println(passwd+consume+"���˷ָ���"+money);
			if(server.passwd.equals(passwd) && money>=consume)
			{
				money -= consume;
				sql = "update usertable set money="+money+" where account ="+server.account+" ";
				System.out.println(sql);
				st.executeUpdate(sql);
			
				out.writeUTF("SUCCESS");
			}
			else 
				out.writeUTF("DEFEAT");
			sql = "select * from shoptable";
			rs = st.executeQuery(sql);
			rs.next();
			shop1_ip=rs.getString("ip");
			rs.next();
			shop2_ip=rs.getString("ip");
			rs.next();
			shop3_ip=rs.getString("ip");
			address =  InetAddress.getByName("127.0.0.1");
			order_num = in.readInt();
			for(int i=0;i<order_num;i++)
			{
				num = in.readInt();
				name = in.readUTF();
				shop = in.readUTF();
				type = in.readUTF();
				price = in.readFloat();
				sql = "insert into ordertable(num,name,shop,type,price)" 
						+" values("+num+",'"+name+"','"+shop+"','"+type+"',"+price+")";
				System.out.println(sql);
				st.executeUpdate(sql);
			}
			conn.close();
		}catch(IOException e)
		{
			System.out.println("��������ʧ�ܣ�");
		}catch(SQLException e)
		{
			System.out.println("��������ʧ�ܣ�");
		}
}
	
	public void initseller(DataInputStream in,DataOutputStream out)
	
	{
		this.in = in;
		this.out = out;
		int food_num,num;
		String name,shop,type;
		float price;
		conn = getConnection();	
		try {
			st = (Statement) conn.createStatement();
			sql = "select count(*) as rowCount from dstable";
			System.out.println(sql);
			rs = st.executeQuery(sql);	
			rs.next();
			food_num = rs.getInt("rowCount");
			sql = "select * from dstable";
			rs = st.executeQuery(sql);
			out.writeInt(food_num);
			while (rs.next()) {	// �ж��Ƿ�����һ������
			num = rs.getInt("num");
			name = rs.getString("name");
			type = rs.getString("type");
			price =rs.getFloat("price");
			System.out.println(num + " " + name + " "+type +" "
						+price+" ");
			out.writeInt(num);
			out.writeUTF(name);
			out.writeUTF(type);
			out.writeFloat(price);
			}
			sql = "select count(*) as rowCount from ordertable where num>"+300000;
			System.out.println(sql);
			rs = st.executeQuery(sql);
			rs.next();
			food_num = rs.getInt("rowCount");
			sql = "select * from ordertable where num>"+300000;
			System.out.println(sql);
			rs = st.executeQuery(sql);
			out.writeInt(food_num);
			while (rs.next()) {	// �ж��Ƿ�����һ������
			num = rs.getInt("num");
			name = rs.getString("name");
			type = rs.getString("type");
			price =rs.getFloat("price");
			System.out.println(num + " " + name + " "+type +" "
						+price+" ");
			out.writeInt(num);
			out.writeUTF(name);
			out.writeUTF(type);
			out.writeFloat(price);
			}
		
		}catch(IOException e)
			{
				System.out.println("��������ʧ��!");
			}
		catch(SQLException e)
		{
			System.out.println("��ѯ����ʧ�ܣ�");
		}
	}
	*/
	public  Connection getConnection()
	{
		Connection con = null;	//���������������ݿ��Connection����
		try {
			Class.forName("com.mysql.jdbc.Driver");// ����Mysql��������
			
			con = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/foodie"+"?useUnicode=true&characterEncoding=utf8", "root", "root");// ������������
			} 
		catch (Exception e) {
			System.out.println("���ݿ�����ʧ��" + e.getMessage());
		}
		return con;	//���������������ݿ�����
	}
}