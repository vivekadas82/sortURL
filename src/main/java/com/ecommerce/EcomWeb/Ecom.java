package com.ecommerce.EcomWeb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Ecom {
	@Autowired
	JdbcTemplate jdbc;
	public static String generatstring() {
	    int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 6;
	    Random random = new Random();
	    StringBuilder buffer = new StringBuilder(targetStringLength);
	    for (int i = 0; i < targetStringLength; i++) {
	        int randomLimitedInt = leftLimit + (int) 
	          (random.nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    String generatedString = buffer.toString();

	    System.out.println(generatedString);
	    return generatedString;
	}
	
	public static int generatint() {
		Random r=new Random();
		int i=r.nextInt(10000);
		return i;
	}
	
	@PostMapping("/api/login")
	public String login(String email, String password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection
			("jdbc:mysql://localhost:3306/project", "root", "Vivek@7374");
			PreparedStatement pst=con.prepareStatement
					("select * from user where email='"+email+"'");
			ResultSet rs=pst.executeQuery();
			if(rs.next()) {
				String ps=rs.getString("password");
				if(ps.equals(password))
					return "login successful";
				else
					return "wrong password";
			}
				
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return "Email Id not register, Please Signup";
	}
	
	@PostMapping("/api/signup")
	public String signup(String email) {
		int otp;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection
			("jdbc:mysql://localhost:3306/project", "root", "Vivek@7374");
			PreparedStatement pst=con.prepareStatement
			("select * from user where email='"+email+"'");
			ResultSet rs=pst.executeQuery();
			if(rs.next())
				return "allready registered";
			else {
				otp=generatint();
				PreparedStatement ps=con.prepareStatement
						("insert into user(otp) values('"+otp+"')");
				int i=ps.executeUpdate();
				System.out.println(otp);
				if(i==1)
					return "Otp Generated Succefully "+otp;
			}	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return "error";
	}
	
	@PostMapping("/api/signup/setpassword")
	public String setpass(String email, String password, int otp)
	{
		try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con=DriverManager.getConnection
		("jdbc:mysql://localhost:3306/project", "root", "Vivek@7374");
		PreparedStatement pst=con.prepareStatement
				("update user set email='"+email+"',  password='"+password+"' where otp='"+otp+"'");
		int i=pst.executeUpdate();
		if(i==1)
			return "signup successful";
		else
			return "Invalid Otp";
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return "error occur";
	}
	
	@PostMapping("api/forgetpassword")
	public String forgetpas(String email)
	{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection
			("jdbc:mysql://localhost:3306/project", "root", "Vivek@7374");
			PreparedStatement pst=con.prepareStatement
			("select * from user where email='"+email+"'");
			ResultSet rs=pst.executeQuery();
			if(rs.next())
			{
				int otp=generatint();
				PreparedStatement ps=con.prepareStatement
				("update user set otp='"+otp+"' where email='"+email+"'");
				int i=ps.executeUpdate();
				if(i==1)
					return "otp generated succefully"+otp;
				else
					return "please check you email";
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return "some error occur";
	}
	@PostMapping("/api/forgetpassword/verify&setpass")
	public String verifypas(String password, int otp) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection
			("jdbc:mysql://localhost:3306/project", "root", "Vivek@7374");
			PreparedStatement pst=con.prepareStatement
			("update user set password='"+password+"' where otp='"+otp+"'");
			int i=pst.executeUpdate();
			if(i==1)
				return "password reset succefully";
			else
				return "invalid otp";
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return "something went wrong";
	}
	@GetMapping("/api/saleproduct")
	public String insertpro(Product pd, int product_id, String
			model_name, int price, String Specification, String Category) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection
			("jdbc:mysql://localhost:3306/project", "root", "Vivek@7374");
			PreparedStatement pst=con.prepareStatement
			("insert into product values(?)");
			pst.setString(1, pd.getBrand());
			int i=pst.executeUpdate();
			if(i==1)
				return "inserted succefull";
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return "something went wrong";
	}
	@GetMapping("/api/product/search/category")
	public List<Map<String, Object>> searchbycategory(String input)
	{
		List<Map<String,Object>> list=jdbc.queryForList
				("select * from product where Category like '%"+input+"%'");
		return list;
	}
	@GetMapping("/api/addtocart")
	public String addToCart(Product pd, String prod)
	{
		List<Map<String,Object>> list=jdbc.queryForList
				("select * from product where Model_name='"+prod+"'");
		if(list==null)
			return "Product Error";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection
			("jdbc:mysql://localhost:3306/project", "root", "Vivek@7374");
			PreparedStatement pst=con.prepareStatement
			("select * from user where email=?");
			pst.setString(1, pd.getEmail());
			ResultSet rs=pst.executeQuery();
			if(rs.next()) {
				PreparedStatement ps=con.prepareStatement
				("insert into Order_user(name, email, orderInCart) values(?,?,'"+list+"') ");
				ps.setString(1, pd.getName());
				ps.setString(2, pd.getEmail());
				int i=ps.executeUpdate();
				if(i==1)
					return "added to cart";
				else
					return "Model not found";
			}
			else {
					return "user not registered";
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return "Something went wrong";
	}
	@GetMapping("/api/viewCart")
	public Map<String, List> viewCart(String email) {
		Map<String, List> map=new HashMap<String, List>();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection
			("jdbc:mysql://localhost:3306/project", "root", "Vivek@7374");
			PreparedStatement pst=con.prepareStatement
			("select * from Order_user where email='"+email+"'");
			ResultSet rs=pst.executeQuery();
			List list=new ArrayList();
				while(rs.next()==true) {
					Map hm=new HashMap();
					hm.put("products",rs.getString("orderInCart"));
					list.add(hm);
				}
				map.put("products", list);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	public List ProductDetails(String Model_name) {
		List<Map<String, Object>> pr=jdbc.queryForList("select * from product where Model_name='"+Model_name+"'");
		return pr;
		
	}
	@GetMapping("/api/buy")
	public String BuyOrder(Product pro) {   // name, email, model_name:
		List OrderDetails=ProductDetails(pro.getModel_name());
		try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con=DriverManager.getConnection
		("jdbc:mysql://localhost:3306/project", "root", "Vivek@7374");
		PreparedStatement pst=con.prepareStatement
		("select * from user where email=?");
		pst.setString(1, pro.getEmail());
		ResultSet rs=pst.executeQuery();
		if(rs.next()) {
			PreparedStatement pst1=con.prepareStatement
			("select * from Order_user where email=?");
			pst1.setString(1, pro.getEmail());
			ResultSet rs1=pst1.executeQuery();
			if(rs1.next()) {
			String order=rs1.getString("OrderInCart");
			if(order!=null && order.equals(OrderDetails)) {
			PreparedStatement ps=con.prepareStatement
			("update Order_user set Order_details=?, OrderInCart=' ', Order_date=Current_timestamp where OrderInCart='"+order+"'");
			ps.setString(1, order);
			int i=ps.executeUpdate();
			if(i==1)
				return "Thank You for Order";
			else
				return "try again";
			}
			else {
				PreparedStatement pst2=con.prepareStatement
				("insert into Order_user(name, email,Order_details, Order_date) values(?,?,'"+OrderDetails+"',current_timestamp)");
				pst2.setString(1, pro.getName());
				pst2.setString(2, pro.getEmail());
				int insert=pst2.executeUpdate();
				if(insert==1) {
					return "Thank You For Order";
				}
				else
					return "error";
			}
			}
			else {
			PreparedStatement ps1=con.prepareStatement
			("insert into Order_user(name, email,Order_details, Order_date) values(?,?,'"+OrderDetails+"',current_timestamp)");
			ps1.setString(1, pro.getName());
			ps1.setString(2, pro.getEmail());
			int i=ps1.executeUpdate();
			if(i==1)
				return "Thank You for Order";
			else
				return "Try Again";
			}
		}
		else {
			return "Wrong User Id";
		}
		
		}
		catch(Exception e) {e.printStackTrace();}
		return "Something Went Wrong";
	}
	@GetMapping("/api/vieworder")
	public Map vieworders(String email)
	{
		Map map=new HashMap();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection
			("jdbc:mysql://localhost:3306/project", "root", "Vivek@7374");
			PreparedStatement pst=con.prepareStatement
			("select * from order_user where email=?");
			pst.setString(1, email);
			ResultSet rs=pst.executeQuery();
			List list=new ArrayList();
			if(rs.next()) {
				PreparedStatement pst1=con.prepareStatement
				("select orderInCart from Order_user where email='"+email+"'");
				ResultSet rs1=pst1.executeQuery();
				Map m=new HashMap();
				m.put("Product", rs1.getString("orderInCart"));
				list.add(m);
			}
			map.put("Orders", list);
			return map;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	@GetMapping("/api/Trackorder")
//	public ViewAndModel trackorder(Product pro) {
//		
//	}
		
}
