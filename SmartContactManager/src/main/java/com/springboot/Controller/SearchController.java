package com.springboot.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.Entity.Contact;
import com.springboot.Entity.User;
import com.springboot.Repository.ContactRepository;
import com.springboot.Repository.UserRepository;

@RestController
public class SearchController 
{
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ContactRepository contactRepo;
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query")String query, Principal principal)
	{
		System.out.println(query);
		User user=userRepo.getUserByUserName(principal.getName());
		List<Contact> findByNameContainingAndUser = contactRepo.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(findByNameContainingAndUser);
		
	}
}
