package studybridge.studybridge.model;



import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "deadlines")
public class Deadline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "deadline_type")
    private String deadlineType;

    @Column(name = "subject_id")
    private Long subjectId;

    public Deadline() {}

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getDeadlineType() { return deadlineType; }
    public void setDeadlineType(String deadlineType) {
        this.deadlineType = deadlineType;
    }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
}
