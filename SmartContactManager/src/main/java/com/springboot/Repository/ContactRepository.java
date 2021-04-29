package com.springboot.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.Entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {
	@Query("from Contact as c where c.user.id=:userId")//current page and contact per page 5
public Page<Contact> findContactsByUser(@Param("userId")Long userId,Pageable pageable);
	
	
}
