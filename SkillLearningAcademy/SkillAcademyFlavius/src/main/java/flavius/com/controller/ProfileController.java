package flavius.com.controller;

import flavius.com.entity.AppUser;
import flavius.com.entity.Enrollment;
import flavius.com.repository.AppUserRepository;
import flavius.com.repository.EnrollmentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProfileController {

    private final AppUserRepository appUserRepository;
    private final EnrollmentRepository enrollmentRepository;

    public ProfileController(AppUserRepository appUserRepository,
                             EnrollmentRepository enrollmentRepository) {
        this.appUserRepository = appUserRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {

        AppUser user = appUserRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Enrollment> enrollments = enrollmentRepository.findByUser(user);

        long totalEnrolled = enrollments.size();
        long totalCompleted = enrollments.stream()
                .filter(e -> e.getStatus() == Enrollment.Status.COMPLETED)
                .count();

        int progressPercent = 0;
        if (totalEnrolled > 0) {
            progressPercent = (int) ((totalCompleted * 100) / totalEnrolled);
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("totalEnrolled", totalEnrolled);
        model.addAttribute("totalCompleted", totalCompleted);
        model.addAttribute("progressPercent", progressPercent);

        return "profile";
    }
}