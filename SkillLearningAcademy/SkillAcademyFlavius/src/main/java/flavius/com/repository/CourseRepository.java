package flavius.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import flavius.com.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}