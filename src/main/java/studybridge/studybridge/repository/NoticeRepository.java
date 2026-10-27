
package studybridge.studybridge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import studybridge.studybridge.model.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String description);
}
