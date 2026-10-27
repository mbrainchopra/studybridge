package studybridge.studybridge.model;


import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "notice_date")
    private LocalDate noticeDate;

    private String category;

    public Notice() {}

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getNoticeDate() { return noticeDate; }
    public void setNoticeDate(LocalDate noticeDate) {
        this.noticeDate = noticeDate;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        this.category = category;
    }
}
