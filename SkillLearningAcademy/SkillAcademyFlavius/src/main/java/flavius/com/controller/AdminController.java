package flavius.com.controller;

import flavius.com.entity.Course;
import flavius.com.entity.Enrollment;
import flavius.com.repository.CourseRepository;
import flavius.com.repository.EnrollmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AdminController(CourseRepository courseRepository,
                           EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @GetMapping
    public String adminHome(Model model) {

        List<Course> courses = courseRepository.findAll();
        List<Enrollment> enrollments = enrollmentRepository.findAllByOrderByIdDesc();

        long totalCourses = courses.size();
        long totalEnrollments = enrollments.size();

        long completed = enrollments.stream()
                .filter(e -> e.getStatus() == Enrollment.Status.COMPLETED)
                .count();

        long inProgress = enrollments.stream()
                .filter(e -> e.getStatus() == Enrollment.Status.IN_PROGRESS)
                .count();

        double completionRate = (totalEnrollments == 0)
                ? 0
                : (completed * 100.0 / totalEnrollments);

        model.addAttribute("course", new Course());
        model.addAttribute("courses", courses);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("totalEnrollments", totalEnrollments);
        model.addAttribute("completedCount", completed);
        model.addAttribute("inProgressCount", inProgress);
        model.addAttribute("completionRate", String.format("%.1f", completionRate));

        return "admin";
    }

    @PostMapping("/add-course")
    public String addCourse(@ModelAttribute Course course) {
        courseRepository.save(course);
        return "redirect:/admin";
    }

    @PostMapping("/delete-course")
    public String deleteCourse(@RequestParam Long courseId) {
        courseRepository.deleteById(courseId);
        return "redirect:/admin";
    }
}