package com.springboot.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.springboot.Entity.User;
import com.springboot.Repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserRepository userrepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User userByUserName = userrepo.getUserByUserName(username);
		if (userByUserName==null) {
			throw new UsernameNotFoundException("Could not found user.");
		}
		
		CustomUserDetails customUserDetails=new CustomUserDetails(userByUserName);
		return customUserDetails;
	}

}
