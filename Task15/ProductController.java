package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
	@GetMapping("product")
	public String ProductService() {
		return "Product Service Page can be loaded here";
	}

}
