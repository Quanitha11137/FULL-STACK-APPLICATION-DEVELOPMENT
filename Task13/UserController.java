package edu.Task_13.controller;
import edu.Task_13.entity.User;
import edu.Task_13.repository.UserRepository;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return repo.save(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id,
                                 @Valid @RequestBody User user) {

        User existing = repo.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setName(user.getName());
        existing.setMail(user.getMail());
        existing.setPhoneNumber(user.getPhoneNumber());
        existing.setAddress(user.getAddress());

        return repo.save(existing);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        repo.deleteById(id);
        return "User deleted with id = " + id;
    }
}