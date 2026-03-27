package edu.Task_12.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.Task_12.Entity.Product;
import edu.Task_12.Service.ProductService;

@RestController
public class ProductController {

    @Autowired
    ProductService service;

    @GetMapping("/getProducts")
    public List<Product> getProducts() {
        return service.getProducts();
    }

    @GetMapping("/getProductByCost/{cost}")
    public Product getProductByCost(@PathVariable Double cost) {
        return service.getProductByCost(cost);
    }

    @GetMapping("/getProduct/{id}")
    public Product getProduct(@PathVariable int id) {
        return service.getProduct(id);
    }

    @GetMapping("/getProductByName/{name}")
    public Product getProductByName(@PathVariable String name) {
        return service.getProductByName(name);
    }

    @PostMapping("/addProduct")
    public Product addData(@RequestBody Product product) {
        service.addData(product);
        return product;
    }

    @PostMapping("/saveProduct")
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        service.addData(product);
        return ResponseEntity.ok(product);
    }
}