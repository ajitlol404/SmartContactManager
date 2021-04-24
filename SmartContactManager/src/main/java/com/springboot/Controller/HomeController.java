package com.springboot.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.Entity.User;
import com.springboot.Repository.UserRepository;

@Controller
public class HomeController 
{
	@Autowired
	private UserRepository userrepo;
	
	@GetMapping("/")
	public String home(Model model)
	{
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model)
	{
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model)
	{
		model.addAttribute("title", "Signup - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	@PostMapping("/do_register")
	public String doregister(@ModelAttribute("user") User user,@RequestParam(value = "agreement",defaultValue = "false")boolean agreement,Model model)
	{
		if(!agreement)
		{
			System.out.println("You have not agreed the term and condition.");
		}
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		
		User result = this.userrepo.save(user);
	
		System.out.println("User details :- "+user);
		
		model.addAttribute("user", user);
		return "signup";
	}
}
