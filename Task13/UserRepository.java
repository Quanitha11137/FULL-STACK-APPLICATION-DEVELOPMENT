
package edu.Task_13.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.Task_13.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}