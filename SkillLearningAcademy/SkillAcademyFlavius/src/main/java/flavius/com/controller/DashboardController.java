package flavius.com.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import flavius.com.entity.AppUser;
import flavius.com.entity.Enrollment;
import flavius.com.repository.AppUserRepository;
import flavius.com.repository.CourseRepository;
import flavius.com.repository.EnrollmentRepository;

@Controller
public class DashboardController {

    private final CourseRepository courseRepository;
    private final AppUserRepository appUserRepository;
    private final EnrollmentRepository enrollmentRepository;

    public DashboardController(CourseRepository courseRepository,
                               AppUserRepository appUserRepository,
                               EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.appUserRepository = appUserRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Enrollment> enrollments = enrollmentRepository.findByUser(user);
        Set<Long> enrolledCourseIds = enrollments.stream()
                .map(e -> e.getCourse().getId())
                .collect(Collectors.toSet());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("enrolledCourseIds", enrolledCourseIds);
        return "dashboard";
    }
}