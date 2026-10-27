
package studybridge.studybridge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import studybridge.studybridge.model.Subject;
import studybridge.studybridge.repository.SubjectRepository;

@Controller
public class SubjectController {

    private final SubjectRepository subjectRepository;

    public SubjectController(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @GetMapping("/subjects")
    public String listSubjects(Model model) {
        model.addAttribute("subjects", subjectRepository.findAll());
        return "subjects";
    }

    @GetMapping("/subjects/add")
    public String addSubjectForm(Model model) {
        model.addAttribute("subject", new Subject());
        return "add-subject";
    }

    @PostMapping("/subjects/save")
    public String saveSubject(
            @ModelAttribute Subject subject,
            RedirectAttributes redirectAttributes) {

        if (subject.getSubjectName() == null
                || subject.getSubjectName().isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Subject name is required.");
            return "redirect:/subjects/add";
        }

        subjectRepository.save(subject);

        redirectAttributes.addFlashAttribute(
                "message", "Subject added successfully!");

        return "redirect:/subjects";
    }

    @GetMapping("/subjects/edit/{id}")
    public String editSubjectForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Subject subject = subjectRepository.findById(id).orElse(null);

        if (subject == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Subject not found.");
            return "redirect:/subjects";
        }

        model.addAttribute("subject", subject);
        return "edit-subject";
    }

    @PostMapping("/subjects/update/{id}")
    public String updateSubject(
            @PathVariable Long id,
            @ModelAttribute Subject updatedSubject,
            RedirectAttributes redirectAttributes) {

        Subject existingSubject =
                subjectRepository.findById(id).orElse(null);

        if (existingSubject == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Subject not found.");
            return "redirect:/subjects";
        }

        if (updatedSubject.getSubjectName() == null
                || updatedSubject.getSubjectName().isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Subject name is required.");
            return "redirect:/subjects/edit/" + id;
        }

        existingSubject.setSubjectCode(updatedSubject.getSubjectCode());
        existingSubject.setSubjectName(updatedSubject.getSubjectName());
        existingSubject.setSemester(updatedSubject.getSemester());
        existingSubject.setDepartment(updatedSubject.getDepartment());

        subjectRepository.save(existingSubject);

        redirectAttributes.addFlashAttribute(
                "message", "Subject updated successfully!");

        return "redirect:/subjects";
    }

    @PostMapping("/subjects/delete/{id}")
    public String deleteSubject(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (subjectRepository.existsById(id)) {
            subjectRepository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "message", "Subject deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute(
                    "error", "Subject not found.");
        }

        return "redirect:/subjects";
    }
}
