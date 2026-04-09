package flavius.com.config;

import flavius.com.entity.*;
import flavius.com.repository.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(AppUserRepository appUserRepository,
                      RoleRepository roleRepository,
                      CourseRepository courseRepository,
                      EnrollmentRepository enrollmentRepository,
                      PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

       
        if (roleRepository.count() > 0) return;
        Role roleUser = new Role();
        roleUser.setName("ROLE_USER");

        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");
        roleRepository.saveAll(List.of(roleUser, roleAdmin));
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(roleUser);
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(roleUser);
        adminRoles.add(roleAdmin);
        AppUser user = new AppUser();
        user.setUsername("user1");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setRoles(userRoles);
        AppUser admin = new AppUser();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRoles(adminRoles);
        appUserRepository.saveAll(List.of(user, admin));

        Course course1 = new Course();
        course1.setName("Java Basics");
        course1.setDescription("Learn Java from scratch");
        course1.setPrice(100.0);
        Course course2 = new Course();
        course2.setName("Spring Boot");
        course2.setDescription("Build REST APIs");
        course2.setPrice(150.0);
        courseRepository.saveAll(List.of(course1, course2));

        Enrollment e1 = new Enrollment();
        e1.setUser(user);
        e1.setCourse(course1);
        Enrollment e2 = new Enrollment();
        e2.setUser(admin);
        e2.setCourse(course2);

        enrollmentRepository.saveAll(List.of(e1, e2));

        System.out.println("✅ Database seeded successfully!");
    }
}