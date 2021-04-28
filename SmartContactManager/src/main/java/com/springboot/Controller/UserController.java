package com.springboot.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("image") MultipartFile file,Principal principal)
	{
		try {
			String name = principal.getName();
			User user = this.userRepo.getUserByUserName(name);
			
			//processing and uploading file
			if (file.isEmpty()) 
			{	
				//if file is empty then try message
			}
			else {
				//file the file to the folder and update the name to contact
				contact.setImage(file.getOriginalFilename());
				
				File file2 = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			
			contact.setUser(user);
			user.getContact().add(contact);
			
			
			this.userRepo.save(user);
			
			System.out.println("Added to database");
		}catch(Exception exception)
		{
			exception.printStackTrace();
		}
		return "normal/addContactForm";
	}
}
