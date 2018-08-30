package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.mapper.CafeMapper;
import com.example.demo.model.Coffees;
import com.example.demo.model.LineItems;
import com.example.demo.model.LoginForm;
import com.example.demo.model.Orders;
import com.example.demo.model.Users;

@Controller
public class CafeController {

	@Autowired
	private CafeMapper cafeMapper;
	private HashMap<String, List<Coffees>> allCoffeesList = new HashMap<String, List<Coffees>>();
	private HashMap<String, Orders> ordersList = new HashMap<String, Orders>();

	/* GET */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model, HttpServletRequest request) {
		loginStatus(model, request);
		
		return "index";
	}

	@RequestMapping(value = "/menu", method = RequestMethod.GET)
	public String menu(Model model, HttpServletRequest request, HttpServletResponse response) {
		String scId = visit(request, response);
		loginStatus(model, request);
		Orders order = ordersList.get(scId);
		int totalPrice = order.getTotalPrice();
		model.addAttribute("totalPrice", totalPrice);
		
		return "menu";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model, HttpServletRequest request) {
		if (loginStatus(model, request)) {
			return "redirect:/";
		} else {
			return "login";
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(Model model, HttpServletRequest request, HttpServletResponse response) {
		if (loginStatus(model, request)) {
			Cookie[] cookies = request.getCookies();
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("uId")) {
					cookie.setValue(null);
					cookie.setMaxAge(0);
					response.addCookie(cookie);
					break;
				}
			}
			return "redirect:/";
		} else {
			return "redirect:/";
		}
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signUp(Model model, HttpServletRequest request) {
		if (loginStatus(model, request)) {
			return "redirect:/";
		} else {
			return "signup";
		}
	}

	@RequestMapping(value = "/shoppingcart", method = RequestMethod.GET)
	public String shoppingCart(Model model, HttpServletRequest request, HttpServletResponse response) {
		String scId = visit(request, response);
		loginStatus(model, request);
		List<Coffees> coffeesList = allCoffeesList.get(scId);
		Orders order = ordersList.get(scId);
		int totalPrice = order.getTotalPrice();

		if (totalPrice == 0) {
			model.addAttribute("shoppingCart", "no items");
			model.addAttribute("items", false);
		} else {
			model.addAttribute("totalPrice", totalPrice);
			model.addAttribute("items", true);
		}
		
		model.addAttribute("coffeesList", coffeesList);
		
		return "shoppingcart";
	}

	@RequestMapping(value = "/billinginformation", method = RequestMethod.GET)
	public String billinginformation(Model model, HttpServletRequest request, HttpServletResponse response) {
		if (loginStatus(model, request)) {
			int uId = getuId(request);
			Orders order = cafeMapper.findOrder(uId);
			if (order != null) {
				List<LineItems> lineItems = new ArrayList<LineItems>();
				lineItems = cafeMapper.getLineItems(order.getoId());
				Iterator<LineItems> it = lineItems.iterator();
				
				while (it.hasNext()) {
					LineItems li = it.next();
					Coffees coffee = cafeMapper.findCoffee(li.getcId());
					li.setProductName(coffee.getProductName());
					li.setPrice(coffee.getPrice());
				}
				
				order.setLineItems(lineItems);
				model.addAttribute("order", order);
				
				return "billinginformation";
			}
			
			System.out.println("dont have any bills");
			
			return "redirect:/menu";
		} else {
			
			System.out.println("billinginformation: not log in... redirect:/login");
			
			return "redirect:/login";
		}
	}

	@RequestMapping(value = "/orders", method = RequestMethod.GET)
	public String orders(Model model, HttpServletRequest request, HttpServletResponse response) {

		if (loginStatus(model, request)) {
			int uId = getuId(request);
			List<Orders> orders = cafeMapper.getOrders(uId);
			List<LineItems> lineItems = new ArrayList<LineItems>();
			int count = 0;
			Iterator<Orders> it = orders.iterator();
			
			while (it.hasNext()) {
				Orders o = it.next();
				System.out.println("date: " + o.getDate());
				System.out.println("oId: " + o.getoId() + "    totalPrice: " + o.getTotalPrice());
				lineItems = cafeMapper.getLineItems(o.getoId());
				Iterator<LineItems> itItems = lineItems.iterator();
				
				while (itItems.hasNext()) {
					LineItems li = itItems.next();
					Coffees coffee = cafeMapper.findCoffee(li.getcId());
					li.setProductName(coffee.getProductName());
					li.setPrice(coffee.getPrice());
					System.out.println("cId: " + li.getcId() + "    quantity: " + li.getQuantity());
				}
				
				o.setLineItems(lineItems);
				count++;
			}

			model.addAttribute("orders", orders);
			
			System.out.println("#orders: " + count);
			
			return "order";
		} else {
			return "redirect:/login";
		}

	}

	/* POST */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(LoginForm loginForm, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		String name = loginForm.getName();
		String password = loginForm.getPassword();
		Users users = cafeMapper.findUser(name);

		if (users != null) {
			if (password.equals(users.getPassword())) {
				int uId = users.getuId();
				loginAction(response, uId);
				
				return "redirect:/";
			} else {
				model.addAttribute("invalidCredentials", true);
				model.addAttribute("wrong", "password is wrong");
				
				return "login";
			}
		} else {
			model.addAttribute("invalidCredentials", true);
			model.addAttribute("wrong", "name is wrong");
			
			return "login";
		}
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signUp(Users user, Model model) {

		String name = user.getName();
		String email = user.getEmail();
		String password = user.getPassword();
		String confirmPassword = user.getConfirmPassword();
		String phoneNumber = user.getPhoneNumber();

		Users confirmInfo = cafeMapper.findUser(name);

		if (confirmInfo != null) {
			model.addAttribute("confirmName", true);
			model.addAttribute("nameErrMsg", "	" + name + " already exists");
			
			return "signup";
		} else {
			if (password.equals(confirmPassword)) {
				user.setName(name);
				user.setEmail(email);
				user.setPassword(password);
				user.setPhoneNumber(phoneNumber);
				cafeMapper.insertUser(user);
				
				System.out.println("name: " + name);
				System.out.println("email: " + email);
				System.out.println("password: " + password);
				System.out.println("confirmPassword: " + confirmPassword);
				System.out.println("phoneNumber: " + phoneNumber);
				
				return "redirect:/login";
			} else {
				model.addAttribute("confirmPassword", true);
				model.addAttribute("confirmPwdErrMsg", "	inconsistent password");
				
				return "signup";
			}
		}
	}

	@RequestMapping(value = "/order", method = RequestMethod.POST)
	public void orderCoffees(@RequestParam("coffees") String[] coffees, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		String scId = visit(request, response);
		boolean flag = false;
		int cId = Integer.parseInt(coffees[0]);
		String productName = coffees[1];
		int price = Integer.parseInt(coffees[2]);
		int quantity = Integer.parseInt(coffees[3]);
		int totalPrice = 0;
		Coffees coffeesOrder = new Coffees(cId, productName, price, quantity);
		List<Coffees> coffeesList = allCoffeesList.get(scId);
		Orders order = ordersList.get(scId);
		Iterator<Coffees> it = coffeesList.iterator();
		
		while (it.hasNext()) {
			Coffees coffeesCart = it.next();
			if (coffeesCart.getcId() == cId) {
				coffeesCart.setQuantity(coffeesCart.getQuantity() + quantity);
				flag = true;
				break;
			}
		}

		if (flag == false) {
			coffeesList.add(coffeesOrder);
		}

		order.setTotalPrice(order.getTotalPrice() + price * quantity);
		totalPrice = order.getTotalPrice();
		
		System.out.println("cId: " + cId);
		System.out.println("productName: " + productName);
		System.out.println("price: " + price);
		System.out.println("quantity: " + quantity);
		System.out.println("subTotal: " + price * quantity);
		System.out.println("totalPrice: " + totalPrice);
	}

	@RequestMapping(value = "/clear", method = RequestMethod.POST)
	public void clear(Model model, HttpServletRequest request, HttpServletResponse response) {
		String scId = visit(request, response);
		allCoffeesList.remove(scId);
		ordersList.remove(scId);
		
		System.out.println("clear scId: " + scId);
	}

	@RequestMapping(value = "/deletecoffees", method = RequestMethod.POST)
	public void deleteCoffees(@RequestParam("cId") int cId, HttpServletRequest request,
			HttpServletResponse response) {
		String scId = visit(request, response);
		List<Coffees> coffeesList = allCoffeesList.get(scId);
		Orders order = ordersList.get(scId);
		Iterator<Coffees> it = coffeesList.iterator();
		int price;
		int quantity;
		int subTotal;

		while (it.hasNext()) {
			Coffees coffeesCart = it.next();
			if (coffeesCart.getcId() == cId) {
				price = coffeesCart.getPrice();
				quantity = coffeesCart.getQuantity();
				subTotal = price * quantity;
				order.setTotalPrice(order.getTotalPrice() - subTotal);
				coffeesList.remove(coffeesCart);
				break;
			}
		}
	}
	
	@RequestMapping(value = "/check", method = RequestMethod.POST)
	public void check(@RequestParam("arrivalTime") String arrivalTime, Model model, HttpServletRequest request, HttpServletResponse response) {
		String scId = visit(request, response);

		if (loginStatus(model, request)) {
			List<Coffees> coffeesList = allCoffeesList.get(scId);
			Orders order = ordersList.get(scId);
			LineItems lineItem = new LineItems();
			
			LocalDateTime currentTime = LocalDateTime.now();
			String oId = currentTime.toString();
			order.setoId(oId);
			order.setuId(getuId(request));
			order.setArrivalTime(arrivalTime);
			cafeMapper.insertOrder(order);

			lineItem.setoId(oId);
			Iterator<Coffees> it = coffeesList.iterator();
			
			while (it.hasNext()) {
				Coffees coffeesItem = it.next();
				lineItem.setcId(coffeesItem.getcId());
				lineItem.setQuantity(coffeesItem.getQuantity());
				cafeMapper.insertLineItem(lineItem);
			}
			
			allCoffeesList.remove(scId);
			ordersList.remove(scId);
			
			System.out.println("check: log in...");
			
		} else {			
			System.out.println("check: not log in...");
		}
	}

	@RequestMapping(value = "/deleteorder", method = RequestMethod.POST)
	public String deleteOrder(@RequestParam("oId") String oId) {
		cafeMapper.deleteOrder(oId);
		cafeMapper.deleteLineItems(oId);
		
		return "order";
	}

	public String visit(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		boolean flag = false;

		if (cookies == null) {
			LocalDateTime currentTime = LocalDateTime.now();
			String scId = currentTime.toString();
			List<Coffees> coffeesList = new ArrayList<Coffees>();
			Orders order = new Orders(0);
			Cookie cookie = new Cookie("scId", scId);
			cookie.setMaxAge(5184000);
			cookie.setHttpOnly(true);
			response.addCookie(cookie);
			allCoffeesList.put(scId, coffeesList);
			ordersList.put(scId, order);

			System.out.println("no cookies");
			System.out.println("scId: " + scId);
			System.out.println("allCoffeesList.containsKey(): " + allCoffeesList.containsKey(scId));
			System.out.println("ordersList.containsKey(): " + ordersList.containsKey(scId));
			
			return scId;
		} else {
			String scId = "";
			String newscId = "";
			
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("scId")) {
					flag = true;
					scId = cookie.getValue();
					
					System.out.println("allCoffeesList.containsKey(): " + allCoffeesList.containsKey(scId));
					System.out.println("ordersList.containsKey(): " + ordersList.containsKey(scId));
					if (allCoffeesList.containsKey(scId) == false || ordersList.containsKey(scId) == false) {
						LocalDateTime currentTime = LocalDateTime.now();
						newscId = currentTime.toString();
						List<Coffees> coffeesList = new ArrayList<Coffees>();
						Orders order = new Orders(0);
						allCoffeesList.put(newscId, coffeesList);
						ordersList.put(newscId, order);
						cookie.setValue(newscId);
						cookie.setHttpOnly(true);
						response.addCookie(cookie);
						
						System.out.println("cannot find a shoppingcart");
						System.out.println("newscId: " + newscId);
						
						return newscId;
					}
				}
			}

			if (flag == false) {
				LocalDateTime currentTime = LocalDateTime.now();
				scId = currentTime.toString();
				List<Coffees> coffeesList = new ArrayList<Coffees>();
				Orders order = new Orders(0);
				allCoffeesList.put(scId, coffeesList);
				ordersList.put(scId, order);
				Cookie cookie = new Cookie("scId", scId);
				cookie.setHttpOnly(true);
				response.addCookie(cookie);
				
				System.out.println("dont have cookie scId");
				System.out.println("scId: " + scId);
			}
			
			return scId;
		}
	}

	public void loginAction(HttpServletResponse response, int uId) {
		Cookie cookie = new Cookie("uId", Integer.toString(uId));
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
		
		System.out.println("log in successfully");
	}

	public boolean loginStatus(Model model, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("uId")) {
					model.addAttribute("loginFlag", 1);
					
					System.out.println("uId: " + cookie.getValue());
					
					return true;
				}
			}
		}
		
		return false;
	}

	public int getuId(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		int uId = 0;
		
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("uId")) {
				uId = Integer.parseInt(cookie.getValue());
				break;
			}
		}
		
		return uId;
	}
}
