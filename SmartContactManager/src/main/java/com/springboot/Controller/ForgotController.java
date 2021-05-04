package com.springboot.Controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.Entity.User;
import com.springboot.Repository.UserRepository;
import com.springboot.Service.EmailService;

@Controller
public class ForgotController 
{
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	Random random = new Random();
	
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		return "forgot_email_form";
		
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("registeredemail") String email,HttpSession session)
	{
		System.out.println("Registered email : "+ email);
		
		//generate otp of 4 digit
		int otp = random.nextInt(9999);
		System.out.println("Otp: "+otp);
		
		String subject="OTP from Smart Contact Manager.";
		String message=""
				+ "<div style='border:1px solid #e2e2e2;padding:20px' >"
				+ "<h2>"
				+ "Otp is "
				+ "<b>"+otp
				+ "</n>"
				+ "</h2>"
				+ "</div>";
		String to=email;
		//write code for send otp
		boolean result = emailService.sendEmail(subject, message, to);
		
		if(result)
		{
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verifyOtp";
		}
		else
		{
			session.setAttribute("message", "Check your email id.");
			return "forgot_email_form";
		}
		
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp")int otp,HttpSession session)
	{
		int myOtp=(int) session.getAttribute("myotp");
		String email=(String) session.getAttribute("email");
		
		if(myOtp==otp)
		{
			//password change form
			User user = userRepository.getUserByUserName(email);
			if(user==null)
			{
				//send error message
				session.setAttribute("message", "User doesnot exist with this email");
				return "forgot_email_form";
			}
			else {
				//send change password form
			}
			return "password_change_form";
		}
		else
		{
			session.setAttribute("message", "You have entered wrong OTP");
			return "verifyOtp";
		}
		
	}
	
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword")String newpassword,HttpSession session)
	{
		String email=(String) session.getAttribute("email");
		User user = userRepository.getUserByUserName(email);
		user.setPassword(bCryptPasswordEncoder.encode(newpassword));
		
		userRepository.save(user);
		
		return "redirect:/signin?change=password change successfullly.";
	}
}
