
package studybridge.studybridge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "syllabus")
public class Syllabus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "unit_name", length = 150)
    private String unitName;

    @Column(name = "syllabus_content", columnDefinition = "TEXT")
    private String syllabusContent;

    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    @Column(name = "extracted_text", columnDefinition = "LONGTEXT")
    private String extractedText;

    public Syllabus() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getSyllabusContent() {
        return syllabusContent;
    }

    public void setSyllabusContent(String syllabusContent) {
        this.syllabusContent = syllabusContent;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }
}
