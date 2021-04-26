package com.springboot.Controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.Entity.User;
import com.springboot.Helper.Message;
import com.springboot.Repository.UserRepository;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userrepo;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Signup - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	@PostMapping("/do_register")
	public String doregister(@Valid @ModelAttribute("user") User user, BindingResult bresult,


			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		try {

			if (!agreement) {
				System.out.println("You have not agreed the term and condition.");
				throw new Exception("You have not agreed the term and condition.");
			}

			if (bresult.hasErrors()) {
				System.out.println(bresult.toString());
				model.addAttribute("user", user);
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("degau.jpg");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			User result = this.userrepo.save(user);

			System.out.println("User details :- " + user);

			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something Went Wrong !!" + e.getMessage(), "alert-danger"));
			return "signup";
		}

		return "signup";
	}
	
	@GetMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title", "Login - Smart Contact Manager");
		return "login";
	}

}
