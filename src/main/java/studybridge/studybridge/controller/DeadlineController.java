
package studybridge.studybridge.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import studybridge.studybridge.model.Deadline;
import studybridge.studybridge.repository.DeadlineRepository;
import studybridge.studybridge.repository.SubjectRepository;

@Controller
public class DeadlineController {

    private final DeadlineRepository deadlineRepository;
    private final SubjectRepository subjectRepository;

    public DeadlineController(
            DeadlineRepository deadlineRepository,
            SubjectRepository subjectRepository) {
        this.deadlineRepository = deadlineRepository;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping("/deadlines")
    public String listDeadlines(Model model) {

        LocalDate today = LocalDate.now();
        List<Deadline> allDeadlines = deadlineRepository.findAll();

        Comparator<Deadline> dateOrder =
                Comparator.comparing(Deadline::getDueDate);

        List<Deadline> overdueDeadlines = allDeadlines.stream()
                .filter(d -> d.getDueDate() != null
                        && d.getDueDate().isBefore(today))
                .sorted(dateOrder)
                .toList();

        List<Deadline> todayDeadlines = allDeadlines.stream()
                .filter(d -> d.getDueDate() != null
                        && d.getDueDate().isEqual(today))
                .sorted(dateOrder)
                .toList();

        List<Deadline> upcomingDeadlines = allDeadlines.stream()
                .filter(d -> d.getDueDate() != null
                        && d.getDueDate().isAfter(today))
                .sorted(dateOrder)
                .toList();

        model.addAttribute("overdueDeadlines", overdueDeadlines);
        model.addAttribute("todayDeadlines", todayDeadlines);
        model.addAttribute("upcomingDeadlines", upcomingDeadlines);

        model.addAttribute("overdueCount", overdueDeadlines.size());
        model.addAttribute("todayCount", todayDeadlines.size());
        model.addAttribute("upcomingCount", upcomingDeadlines.size());
        model.addAttribute("totalCount", allDeadlines.size());
        model.addAttribute("today", today);

        return "deadlines";
    }

    @GetMapping("/deadlines/add")
    public String addDeadlineForm(Model model) {
        model.addAttribute("deadline", new Deadline());
        model.addAttribute("subjects", subjectRepository.findAll());
        return "add-deadline";
    }

    @PostMapping("/deadlines/save")
    public String saveDeadline(
            @ModelAttribute Deadline deadline,
            RedirectAttributes redirectAttributes) {

        if (deadline.getTitle() == null
                || deadline.getTitle().isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Deadline title is required.");
            return "redirect:/deadlines/add";
        }

        if (deadline.getDueDate() == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Please select a due date.");
            return "redirect:/deadlines/add";
        }

        if (deadline.getSubjectId() != null
                && !subjectRepository.existsById(deadline.getSubjectId())) {
            redirectAttributes.addFlashAttribute(
                    "error", "Please select a valid subject.");
            return "redirect:/deadlines/add";
        }

        deadlineRepository.save(deadline);

        redirectAttributes.addFlashAttribute(
                "message", "Deadline added successfully!");

        return "redirect:/deadlines";
    }

    @GetMapping("/deadlines/edit/{id}")
    public String editDeadlineForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Deadline deadline = deadlineRepository.findById(id).orElse(null);

        if (deadline == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Deadline not found.");
            return "redirect:/deadlines";
        }

        model.addAttribute("deadline", deadline);
        model.addAttribute("subjects", subjectRepository.findAll());

        return "edit-deadline";
    }

    @PostMapping("/deadlines/update/{id}")
    public String updateDeadline(
            @PathVariable Long id,
            @ModelAttribute Deadline updatedDeadline,
            RedirectAttributes redirectAttributes) {

        Deadline existingDeadline =
                deadlineRepository.findById(id).orElse(null);

        if (existingDeadline == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Deadline not found.");
            return "redirect:/deadlines";
        }

        if (updatedDeadline.getTitle() == null
                || updatedDeadline.getTitle().isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Deadline title is required.");
            return "redirect:/deadlines/edit/" + id;
        }

        if (updatedDeadline.getDueDate() == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Please select a due date.");
            return "redirect:/deadlines/edit/" + id;
        }

        if (updatedDeadline.getSubjectId() != null
                && !subjectRepository.existsById(
                updatedDeadline.getSubjectId())) {
            redirectAttributes.addFlashAttribute(
                    "error", "Please select a valid subject.");
            return "redirect:/deadlines/edit/" + id;
        }

        existingDeadline.setTitle(updatedDeadline.getTitle());
        existingDeadline.setDescription(updatedDeadline.getDescription());
        existingDeadline.setDueDate(updatedDeadline.getDueDate());
        existingDeadline.setDeadlineType(updatedDeadline.getDeadlineType());
        existingDeadline.setSubjectId(updatedDeadline.getSubjectId());

        deadlineRepository.save(existingDeadline);

        redirectAttributes.addFlashAttribute(
                "message", "Deadline updated successfully!");

        return "redirect:/deadlines";
    }

    @PostMapping("/deadlines/delete/{id}")
    public String deleteDeadline(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (deadlineRepository.existsById(id)) {
            deadlineRepository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "message", "Deadline deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute(
                    "error", "Deadline not found.");
        }

        return "redirect:/deadlines";
    }
}
