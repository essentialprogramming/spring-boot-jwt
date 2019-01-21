package code.project.springbootjwt.repository;

import code.project.springbootjwt.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
