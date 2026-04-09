package flavius.com.controller;

import flavius.com.entity.AppUser;
import flavius.com.entity.Enrollment;
import flavius.com.repository.AppUserRepository;
import flavius.com.repository.EnrollmentRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class PaymentController {

    private final EnrollmentRepository enrollmentRepository;
    private final AppUserRepository appUserRepository;

    public PaymentController(EnrollmentRepository enrollmentRepository,
                             AppUserRepository appUserRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.appUserRepository = appUserRepository;
    }
    @GetMapping("/pay/{enrollmentId}")
    public String payPage(@PathVariable Long enrollmentId,
                          Authentication authentication,
                          Model model) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (!enrollment.getUser().getUsername().equals(authentication.getName())) {
            throw new RuntimeException("Unauthorized");
        }

        model.addAttribute("enrollment", enrollment);
        return "pay";
    }
    @PostMapping("/pay-success")
    public String paySuccess(@RequestParam Long enrollmentId,
                             Authentication authentication) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (!enrollment.getUser().getUsername().equals(authentication.getName())) {
            throw new RuntimeException("Unauthorized");
        }

        enrollment.setPaid(true);
        enrollment.setPaymentDate(LocalDate.now());
        enrollmentRepository.save(enrollment);

        return "redirect:/my-courses";
    }
}