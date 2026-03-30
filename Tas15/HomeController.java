package com.example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HomeController {
	@GetMapping("home")
	public String msg() {
		return "I'am the Home Service.You can create a Home Page";
	}

}
