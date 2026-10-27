
package studybridge.studybridge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import studybridge.studybridge.model.Deadline;

public interface DeadlineRepository extends JpaRepository<Deadline, Long> {

    List<Deadline> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String description);
}
