package com.example.userservice.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.String;
@RestController
@RequestMapping("/users")
public class UserController {
	@GetMapping("/{id}")
	public String getUser(@PathVariable int id) {
		return "User ID:"+id+"Name:Sai";
	}

}
