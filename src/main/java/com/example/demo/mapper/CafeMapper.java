package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import com.example.demo.model.Coffees;
import com.example.demo.model.LineItems;
import com.example.demo.model.Orders;
import com.example.demo.model.Users;

@Mapper
public interface CafeMapper {
	
	@Select("select * from users where name = #{name}")
	public Users findUser(String nane);
	
	@Select("select uId from users where name = #{name}")
	public Integer getuId(String name);
	
	@Select("select * from orders where uId = #{uId} ORDER BY date DESC limit 1")
	public Orders findOrder(int uId);
	
	@Select("select * from orders where uId = #{uId} ORDER BY date DESC")
	public List<Orders> getOrders(int uId);
	
	@Select("select * from lineitems where oId = #{oId}")
	public List<LineItems> getLineItems(String oId);
	
	@Select("select * from coffees where cId = #{cId}")
	public Coffees findCoffee(int cId);
	
	@Insert("insert into users(name, email, password, phoneNumber) value(#{name}, #{email}, #{password}, #{phoneNumber})")
	@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "uId", before = false, resultType = Integer.class)
	public boolean insertUser(Users user);

	@Insert("insert into orders(oId, uId, totalPrice, arrivalTime) value(#{oId}, #{uId}, #{totalPrice}, #{arrivalTime})")
	public boolean insertOrder(Orders order);
	
	@Insert("insert into lineitems(oId, cId, quantity) value(#{oId}, #{cId}, #{quantity})")
	@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "liId", before = false, resultType = Integer.class)
	public boolean insertLineItem(LineItems lineItem);
	
	@Delete("delete from orders where oId = #{oId}")
	public boolean deleteOrder(String oId);
	
	@Delete("delete from lineitems where oId = #{oId}")
	public boolean deleteLineItems(String oId);
	
}
