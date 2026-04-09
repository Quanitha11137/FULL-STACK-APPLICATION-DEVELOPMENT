package flavius.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import flavius.com.entity.Enrollment;
import flavius.com.entity.AppUser;
import flavius.com.entity.Course;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByUser(AppUser user);

    List<Enrollment> findAllByOrderByIdDesc();

    Optional<Enrollment> findByUserAndCourse(AppUser user, Course course);
}