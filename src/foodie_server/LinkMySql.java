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
	//顾客登陆
	public  String query(Customer customer)
	{
		String user = "defeat";
		conn = getConnection();	//同样先要获取连接，即连接到数据库
		try {
			sql = "select * from customer where Customer_ID='"+customer.Customer_ID+"' and Customer_password='"+customer.Customer_password+"' ";		// 查询数据的sql语句
			System.out.println(sql);
			st = (Statement) conn.createStatement();	//创建用于执行静态sql语句的Statement对象，st属局部变量
			rs = st.executeQuery(sql);	//执行sql查询语句，返回查询数据的结果集  
			System.out.println("最后的查询结果为：");
			if(rs.next()==false)
			{
				conn.close();	//关闭数据库连接
				System.out.println("查询不到该数据");
				return user;
			}
			else
			{
				user = "customer";
				//修改该cunstomer的名字，余额等信息
				customer.Customer_name = rs.getString("Customer_name");
				customer.Customer_integral = rs.getInt("Customer_integral");
				customer.Customer_balance = rs.getDouble("Customer_balance");
				conn.close();
			}
		}
		 catch (SQLException e) {
			System.out.println("查询数据失败");
		}
		return user;
	}
	
	//卖家登陆
	public  String query(Seller seller)
	{
		String user = "defeat";
		conn = getConnection();	//同样先要获取连接，即连接到数据库
		try {
			sql = "select * from seller where Seller_ID='"+seller.Seller_ID+"' and Seller_password='"+seller.Seller_password+"' ";		// 查询数据的sql语句
			System.out.println(sql);
			st = (Statement) conn.createStatement();	//创建用于执行静态sql语句的Statement对象，st属局部变量
			rs = st.executeQuery(sql);	//执行sql查询语句，返回查询数据的结果集
			System.out.println("最后的查询结果为：");
			if(rs.next()==false)
			{
				conn.close();	//关闭数据库连接
				System.out.println("查询不到该数据");
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
			System.out.println("查询数据失败");
		}
		System.out.println(user);
		return user;
	}
	
	//顾客注册
	public void customerRegister(String Customer_name,String Customer_password,String Customer_tel)
	{
		conn = getConnection();
		try{
			st = (Statement) conn.createStatement();
			//找到customer表的最大ID，加1得到当前customer的ID
			//也可以自定义pk为自增1?
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
			System.out.println("向customer表中插入 " + count + " 条数据");
			conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("顾客注册失败！！！");
		}
		catch(IOException e)
		{
			System.out.println("返回顾客信息失败！！！");
		}
	}
	
	//店铺注册
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
			System.out.println("向seller表中插入 " + count + " 条数据");
			
			conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("店铺注册失败！！！");
		}
		catch(IOException e)
		{
			System.out.println("返回店铺信息失败！！！");
		}
	}
	//获取店铺的菜单
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
			System.out.println("查询到的菜单，快快出来？"+menu_num);
			while (rs.next()) {	// 判断是否还有下一个数据
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
			conn.close();	//关闭数据库连接
		}
		catch(SQLException e)
		{
			System.out.println("查询数据失败");
		}
		catch(IOException e)
		{
			System.out.println("传送数据失败");
		}
	}
	
	//初始化客户界面
	public void init(DataInputStream in,DataOutputStream out)
	{
		//与serverThread的in和out连接
		this.in = in;
		this.out = out;
		int i = 0,menu_num;
		conn = getConnection();	
		try {
			//查询所有菜单
			st = (Statement) conn.createStatement();
			sql = "select count(*) as rowCount from menu where Menu_appraise >300";
			rs = st.executeQuery(sql);	
			rs.next();
			menu_num = rs.getInt("rowCount");
			menu = new Menu[menu_num];
			sql = "select * from menu where Menu_appraise >300";
			rs = st.executeQuery(sql);
			out.writeInt(menu_num);
			while (rs.next()) {	// 判断是否还有下一个数据
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
				//send_picture的in和out都是这里的in和out,所以是把picture发送到客户端
				send_picture(menu[j].Menu_imagePath_s);
				send_picture(menu[j].Menu_imagePath_b);
			}
			out.write("end;end".getBytes());
			out.write("\r".getBytes());
			conn.close();	//关闭数据库连接
		} catch (SQLException e) {
			System.out.println("查询数据失败");
		}
		catch(IOException e)
		{
			System.out.println("传送数据失败");
		}
	}	
	
	//增加菜单
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
			//在服务器存放图片的目录
			menu.Menu_imagePath_s = "E:/image/"+name;
			String parms[] = name.split("_");
			menu.Menu_imagePath_b = "E:/image/"+parms[0]+"_b.jpg";
			System.out.println("新增菜单的编号为: "+menu.Menu_num);
			receive_picture();
			sql = "INSERT INTO menu(Menu_num, Menu_name, Seller_ID, Menu_price, Menu_appraise, Menu_imagePath_s, Menu_imagePath_b)"
					+ "VALUES ('"+menu.Menu_num+"','"+menu.Menu_name+"','"+menu.Seller_ID+"','"+menu.Menu_price+"','"+menu.Menu_appraise+
					"','"+menu.Menu_imagePath_s+"','"+menu.Menu_imagePath_b+"')";
			System.out.println(sql);
			st = (Statement) conn.createStatement();
			int count = st.executeUpdate(sql);
			System.out.println("向staff表中插入 " + count + " 条数据");
			conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("添加菜单失败，请打死我吧！！！");
		}
	}
	
	//卖家查询订单
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
			System.out.println("查询到数据的数量:"+order_amount);
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
			System.out.println("卖家订单查询失败，不要打死我！！！");
		}
		catch(IOException e)
		{
			System.out.println("传输数据错误！");
		}
	}
	
	//买家订单查询
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
			System.out.println("查询到数据的数量:"+order_amount);
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
			System.out.println("买家订单查询失败，不要打死我！！！");
		}
		catch(IOException e)
		{
			System.out.println("传输数据错误！");
		}
	}
	//买家给订单付款
	public void payOrder(String order_num)
	{
		conn = getConnection();	
		try {
			st = (Statement) conn.createStatement();
			sql = "update myordertable set Order_status= 'pay' where Order_num='"+order_num+"'";
			System.out.println(sql);
			int count = st.executeUpdate(sql);
			System.out.println("向staff表中更新 " + count + " 条数据");
			conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("完成订单失败！！！！");
		}
	}
	//卖家完成订单
	public void completeOrder(String order_num)
	{
		conn = getConnection();	
		try {
			st = (Statement) conn.createStatement();
			sql = "update myordertable set Order_handleStatus= 'complete' where Order_num='"+order_num+"'";
			System.out.println(sql);
			int count = st.executeUpdate(sql);
			System.out.println("向staff表中更新 " + count + " 条数据");
			conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("完成订单失败！！！！");
		}
	}
	
	//查询店铺
	public void queryShop(String shopname,String address,String kind)
	{
		String sql2;
		int shop_amount;
		conn = getConnection();	
		try{
		st = (Statement) conn.createStatement();
		System.out.println(shopname + address +kind);
		if(shopname.equals("其它"))
		{
			if(address.equals("其它"))
			{
				sql = "select count(*) as rowCount from seller where Seller_kind='"+kind+"'";
				sql2 = "select * from seller where Seller_kind='"+kind+"'";
				
			}
			else if(kind.equals("其它"))
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
		else if(address.equals("其它"))
		{
			if(kind.equals("其它"))
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
		else if(kind.equals("其它"))
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
		System.out.println("查询到数据的数量:"+shop_amount);
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
		conn.close();	//关闭数据库连接
		}
		catch(SQLException e)
		{
			System.out.println("查询店铺信息失败！！");
		}		
		catch(IOException e)
		{
			System.out.println("发送数据失败！！");
		}
	}
	//获取店铺信息，根据店铺ID
	public void getShop(String Seller_ID)
	{
		System.out.println("跑到这里了？"
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
			conn.close();	//关闭数据库连接
		}
		catch(SQLException e)
		{
			System.out.println("查询店铺信息失败！！！");
		}
		catch(IOException e)
		{
			System.out.println("发送店铺信息失败！！！");
		}
	}
	//获取购物车信息
	public void getShopping(String Customer_ID)
	{
		int shopping_amount;
		//System.out.println("查询不到购物车信息？？");
		conn = getConnection();	
		try {
			st = (Statement) conn.createStatement();
			sql = "select count(*) as rowCount from myordertable,menu where myordertable.Menu_num= Menu.Menu_num and Customer_ID='"+Customer_ID+"' "
					+ "and Order_status ='non_submit'";
			System.out.println(sql);
			rs = st.executeQuery(sql);	
			rs.next();
			shopping_amount = rs.getInt("rowCount");
			System.out.println("查询到数据的数量:"+shopping_amount);
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
			System.out.println("买家购物车查询失败，不要打死我！！！");
		}
		catch(IOException e)
		{
			System.out.println("传输数据错误！");
		}
	}
	
	//发送图片初始化客户端界面
	public void send_picture(String path)
	{
		//buffer用来存放图片
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
			//把图片输出到客户端。这里的out就是serveThread里的out
			out.write(buffer, 0, readBytes);        
		}       
		out.flush();   
		in.close();
		//System.out.println("发送成功？");
		}
		catch(FileNotFoundException e)
		{}
		catch(IOException e)
		{}
	}
	
	//接收图片,把图片放到服务器的相应位置
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
                        System.out.println("我的天： "+"E:/image/"+parms[0]);
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
                throw new RuntimeException("获取客户端输入流失败", e); 
            } 
    }
	//修改菜单图片
	public void modifyImage()
	{
		receive_picture();
		System.out.println("修改完成？");
	}
	//修改菜单名称和价格
	public void modifyName(String menu_num,String name,double price)
	{
		try{
		conn = getConnection();	
		sql = "update menu set Menu_name='"+name+"',Menu_price="+price+" where Menu_num='"+menu_num+"'";
		System.out.println(sql);
		st = (Statement) conn.createStatement();	
		int count = st.executeUpdate(sql);// 执行sql删除语句，返回删除数据的数量
		System.out.println("表中更新 " + count + " 条数据\n");	//输出删除操作的处理结果
		conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("更新数据失败");
		}
	}
	//删除菜单
	public void deleteMenu(String menu_num)
	{
		try{
		conn = getConnection();
		sql = "delete from menu where Menu_num='"+menu_num+"'";
		st = (Statement) conn.createStatement();
		int count = st.executeUpdate(sql);// 执行sql删除语句，返回删除数据的数量
		System.out.println("表中删除 " + count + " 条数据\n");	//输出删除操作的处理结果
		conn.close();	//关闭数据库连接	
		}
		catch(SQLException e)
		{
			System.out.println("删除数据失败");
		}
	}
	
	//提交订单
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
		//设置时间格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//获取系统时间并转化成字符串
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
			System.out.println("向myordertable表中更新 " + count + " 条数据，把non_submit换成non_pay");
		}
		System.out.println("要提交订单的数量： "+(order_size-non_submit));
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
			System.out.println("向myordertable表中插入 " + count + " 条数据");
		}
		conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("客户添加订单失败!!!");
		}
		catch(IOException e)
		{
			System.out.println("传输数据失败!!!");
		}
	
	}
	
	//提交购物车信息
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
			//设置时间格式
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//获取系统时间并转化成字符串
			String sdate=df.format(new Date());
			System.out.println(sdate);
			String order_time = sdate;
			int non_submit = in.readInt();
			int order_size = in.readInt();
			System.out.println("要提交订单的数量： "+(order_size-non_submit));
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
				System.out.println("向myordertable表中插入 " + count + " 条数据");
			}
			conn.close();
			}
			catch(SQLException e)
			{
				System.out.println("保存购物车信息失败!!!");
			}
			catch(IOException e)
			{
				System.out.println("传输数据失败!!!");
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
//			System.out.println(passwd+consume+"贱人分割线"+money);
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
			System.out.println("传输数据失败！");
		}catch(SQLException e)
		{
			System.out.println("更新数据失败！");
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
			while (rs.next()) {	// 判断是否还有下一个数据
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
			while (rs.next()) {	// 判断是否还有下一个数据
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
				System.out.println("传输数据失败!");
			}
		catch(SQLException e)
		{
			System.out.println("查询数据失败！");
		}
	}
	*/
	public  Connection getConnection()
	{
		Connection con = null;	//创建用于连接数据库的Connection对象
		try {
			Class.forName("com.mysql.jdbc.Driver");// 加载Mysql数据驱动
			
			con = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/foodie"+"?useUnicode=true&characterEncoding=utf8", "root", "root");// 创建数据连接
			} 
		catch (Exception e) {
			System.out.println("数据库连接失败" + e.getMessage());
		}
		return con;	//返回所建立的数据库连接
	}
}