package com.springboot.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.springboot.Helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.Entity.Contact;
import com.springboot.Entity.User;
import com.springboot.Repository.ContactRepository;
import com.springboot.Repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	public UserRepository userRepo;

	@Autowired
	public ContactRepository contactRepo;

	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String userName = principal.getName();
		System.out.println("Username: " + userName);

		User userByUserName = userRepo.getUserByUserName(userName);

		System.out.println("User: - " + userByUserName);
		model.addAttribute("user", userByUserName);

	}

	// dashboard home
	@GetMapping("/index")
	public String userdashboard(Model model, Principal principal) {
		return "normal/user_dashboard";
	}

	@GetMapping("/addContact")
	public String openAddContact(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/addContactForm";
	}

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {
			String name = principal.getName();
			User user = this.userRepo.getUserByUserName(name);

			// processing and uploading file
			if (file.isEmpty()) {
				// if file is empty then try message
				contact.setImage("contact.png");
			} else {
				// file the file to the folder and update the name to contact
				contact.setImage(file.getOriginalFilename());

				File file2 = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			user.getContact().add(contact);
			contact.setUser(user);

			this.userRepo.save(user);

			System.out.println("Added to database");

			// message success
			session.setAttribute("message", new Message("Your Contact is added succesfully", "success"));
		} catch (Exception exception) {
			exception.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong", "danger"));
		}

		return "normal/addContactForm";
	}

	@GetMapping("/show-contact/{page}")
	public String showContact(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show Contact");
		String name = principal.getName();
		User user = this.userRepo.getUserByUserName(name);
		// currentpage-page and contact per page-5
		PageRequest pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepo.findContactsByUser(user.getUid(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/showContact";

	}

	// showing specific user details
	@RequestMapping("/{cid}/contact")
	public String showContactDetails(@PathVariable("cid") Long cid, Model model, Principal principal) {

		Optional<Contact> findById = contactRepo.findById(cid);
		Contact contact = findById.get();

		String name = principal.getName();
		User UserName = userRepo.getUserByUserName(name);

		if (UserName.getUid() == contact.getUser().getUid()) {
			model.addAttribute("contact", contact);
		}

		System.out.println(cid);
		return "normal/contact_detail";

	}

	// deleting contact
	@GetMapping("/delete/{cid}")
	@Transactional
	public String delteContact(@PathVariable("cid") Long cid, Principal principal,HttpSession session) {
		Optional<Contact> contactdel = contactRepo.findById(cid);
		Contact contact = contactdel.get();
		
		User user = userRepo.getUserByUserName(principal.getName());
		user.getContact().remove(contact);
		userRepo.save(user);

		session.setAttribute("message", new Message("Contact deleted succesfully", "success"));
		return "redirect:/user/show-contact/0";

	}

	// open update form handler contact
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Long cid, Model model) {
		model.addAttribute("title", "Update Contact");
		Contact contact = contactRepo.findById(cid).get();

		model.addAttribute("contact", contact);

		return "normal/update_form";

	}

	// update contact handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession session,Principal principal) {
		
		
		try {
			
			Contact oldContact = contactRepo.findById(contact.getCid()).get();
			
			// image
			if (!file.isEmpty()) {
				//delete old photo
				File deletefile = new ClassPathResource("static/image").getFile();
				File file1=new File(deletefile, oldContact.getImage());
				file1.delete();
				
				//update new photo
				File file2 = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
			}
			else
			{
				contact.setImage(oldContact.getImage());
			}
			User userName = userRepo.getUserByUserName(principal.getName());
			contactRepo.save(contact);

			session.setAttribute("message", new Message("Contact Updated", "success"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/"+contact.getCid()+"/contact";

	}
	
	//profile
	@GetMapping("/profile")
	public String profile(Model model)
	{
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}

}
