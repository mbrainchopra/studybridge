
package studybridge.studybridge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import studybridge.studybridge.model.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findBySubjectNameContainingIgnoreCaseOrSubjectCodeContainingIgnoreCase(
            String subjectName, String subjectCode);
}
