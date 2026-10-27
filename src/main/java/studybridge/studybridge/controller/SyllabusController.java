
package studybridge.studybridge.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import studybridge.studybridge.model.Syllabus;
import studybridge.studybridge.repository.SyllabusRepository;
import studybridge.studybridge.repository.SubjectRepository;

@Controller
public class SyllabusController {

    private final SyllabusRepository syllabusRepository;
    private final SubjectRepository subjectRepository;

    private final Path uploadDirectory =
            Paths.get("uploads", "syllabus")
                    .toAbsolutePath()
                    .normalize();

    public SyllabusController(
            SyllabusRepository syllabusRepository,
            SubjectRepository subjectRepository) {

        this.syllabusRepository = syllabusRepository;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping("/syllabus")
    public String listSyllabus(Model model) {
        model.addAttribute("syllabusList", syllabusRepository.findAll());
        return "syllabus";
    }

    @GetMapping("/syllabus/add")
    public String addForm(Model model) {
        model.addAttribute("syllabus", new Syllabus());
        model.addAttribute("subjects", subjectRepository.findAll());
        return "add-syllabus";
    }

    @PostMapping("/syllabus/save")
    public String saveSyllabus(
            @ModelAttribute Syllabus syllabus,
            @RequestParam("pdfFile") MultipartFile pdfFile,
            RedirectAttributes redirectAttributes) {

        Path destination = null;

        try {
            if (syllabus.getSubjectId() == null
                    || !subjectRepository.existsById(syllabus.getSubjectId())) {
                redirectAttributes.addFlashAttribute(
                        "error", "Please select a valid subject.");
                return "redirect:/syllabus/add";
            }

            if (syllabus.getUnitName() == null
                    || syllabus.getUnitName().isBlank()) {
                redirectAttributes.addFlashAttribute(
                        "error", "Unit name is required.");
                return "redirect:/syllabus/add";
            }

            if (pdfFile == null || pdfFile.isEmpty()) {
                redirectAttributes.addFlashAttribute(
                        "error", "Please select a PDF file.");
                return "redirect:/syllabus/add";
            }

            if (pdfFile.getSize() > 10L * 1024 * 1024) {
                redirectAttributes.addFlashAttribute(
                        "error", "PDF size must not exceed 10 MB.");
                return "redirect:/syllabus/add";
            }

            String originalName = pdfFile.getOriginalFilename();

            if (originalName == null
                    || !originalName.toLowerCase().endsWith(".pdf")) {
                redirectAttributes.addFlashAttribute(
                        "error", "Please upload a PDF file.");
                return "redirect:/syllabus/add";
            }

            Files.createDirectories(uploadDirectory);

            String fileName = UUID.randomUUID() + ".pdf";
            destination = uploadDirectory.resolve(fileName).normalize();

            if (!destination.startsWith(uploadDirectory)) {
                redirectAttributes.addFlashAttribute(
                        "error", "Invalid file path.");
                return "redirect:/syllabus/add";
            }

            pdfFile.transferTo(destination);

            String extractedText;

            try (PDDocument document = Loader.loadPDF(destination.toFile())) {
                if (document.isEncrypted()) {
                    throw new IOException("Encrypted PDF is not supported.");
                }

                PDFTextStripper stripper = new PDFTextStripper();
                extractedText = stripper.getText(document);
            }

            syllabus.setPdfPath(fileName);
            syllabus.setExtractedText(extractedText);
            syllabusRepository.save(syllabus);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Syllabus uploaded and text extracted successfully!");

        } catch (IOException | IllegalStateException e) {

            if (destination != null) {
                try {
                    Files.deleteIfExists(destination);
                } catch (IOException ignored) {
                    // Keep the original upload error.
                }
            }

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Could not read the PDF. Please upload a valid, unencrypted PDF.");
        }

        return "redirect:/syllabus";
    }

    @PostMapping("/syllabus/reprocess")
    public String reprocessAllPdfs(
            RedirectAttributes redirectAttributes) {

        List<Syllabus> syllabusList = syllabusRepository.findAll();

        int successCount = 0;
        int failedCount = 0;
        int missingFileCount = 0;

        for (Syllabus syllabus : syllabusList) {

            try {
                if (syllabus.getPdfPath() == null
                        || syllabus.getPdfPath().isBlank()) {
                    failedCount++;
                    continue;
                }

                Path pdfPath =
                        uploadDirectory.resolve(syllabus.getPdfPath()).normalize();

                if (!pdfPath.startsWith(uploadDirectory)
                        || !Files.isRegularFile(pdfPath)) {
                    missingFileCount++;
                    continue;
                }

                String extractedText;

                try (PDDocument document = Loader.loadPDF(pdfPath.toFile())) {

                    if (document.isEncrypted()) {
                        failedCount++;
                        continue;
                    }

                    PDFTextStripper stripper = new PDFTextStripper();
                    extractedText = stripper.getText(document);
                }

                syllabus.setExtractedText(extractedText);
                syllabusRepository.save(syllabus);

                successCount++;

            } catch (IOException | IllegalStateException e) {
                failedCount++;
            }
        }

        String message = "PDF reprocessing finished. Successful: "
                + successCount
                + ", failed: "
                + failedCount
                + ", missing files: "
                + missingFileCount
                + ".";

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/syllabus";
    }

    @GetMapping("/syllabus/view/{id}")
    public ResponseEntity<Resource> viewPdf(@PathVariable Long id) {

        try {
            Syllabus syllabus =
                    syllabusRepository.findById(id).orElse(null);

            if (syllabus == null
                    || syllabus.getPdfPath() == null
                    || syllabus.getPdfPath().isBlank()) {
                return ResponseEntity.notFound().build();
            }

            Path pdfPath =
                    uploadDirectory.resolve(syllabus.getPdfPath()).normalize();

            if (!pdfPath.startsWith(uploadDirectory)
                    || !Files.isRegularFile(pdfPath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(pdfPath.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"syllabus.pdf\"")
                    .body(resource);

        } catch (IOException | IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/syllabus/delete/{id}")
    public String deleteSyllabus(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Syllabus syllabus =
                syllabusRepository.findById(id).orElse(null);

        if (syllabus == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Syllabus not found.");
            return "redirect:/syllabus";
        }

        try {
            if (syllabus.getPdfPath() != null
                    && !syllabus.getPdfPath().isBlank()) {

                Path pdfPath =
                        uploadDirectory.resolve(syllabus.getPdfPath()).normalize();

                if (pdfPath.startsWith(uploadDirectory)) {
                    Files.deleteIfExists(pdfPath);
                }
            }

            syllabusRepository.delete(syllabus);

            redirectAttributes.addFlashAttribute(
                    "message", "Syllabus deleted successfully.");

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Could not delete the PDF file.");
        }

        return "redirect:/syllabus";
    }
}
