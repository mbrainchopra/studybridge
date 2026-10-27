package studybridge.studybridge.model;


import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_code", unique = true)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    private Integer semester;

    private String department;

    public Subject() {}

    public Long getId() { return id; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) {
        this.department = department;
    }
}
