package com.springboot.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springboot.Entity.Contact;
import com.springboot.Entity.User;
import com.springboot.Repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController 
{
	@Autowired
	public UserRepository userRepo;
	
	//method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model,Principal principal)
	{

		String userName = principal.getName();
		System.out.println("Username: "+userName);
		
		User userByUserName = userRepo.getUserByUserName(userName);
		
		System.out.println("User: - "+userByUserName);
		model.addAttribute("user",userByUserName);
		
	}
	
	@GetMapping("/index")
	public String userdashboard(Model model,Principal principal)
	{
		return "normal/user_dashboard";
	}
	
	@GetMapping("/addContact")
	public String openAddContact(Model model)
	{
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/addContactForm";
	}
}
