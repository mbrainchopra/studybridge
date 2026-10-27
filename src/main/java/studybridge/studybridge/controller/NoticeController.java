
package studybridge.studybridge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import studybridge.studybridge.model.Notice;
import studybridge.studybridge.repository.NoticeRepository;

@Controller
public class NoticeController {

    private final NoticeRepository noticeRepository;

    public NoticeController(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @GetMapping("/notices")
    public String listNotices(Model model) {
        model.addAttribute("notices", noticeRepository.findAll());
        return "notices";
    }

    @GetMapping("/notices/add")
    public String addNoticeForm(Model model) {
        model.addAttribute("notice", new Notice());
        return "add-notice";
    }

    @PostMapping("/notices/save")
    public String saveNotice(
            @ModelAttribute Notice notice,
            RedirectAttributes redirectAttributes) {

        if (notice.getTitle() == null || notice.getTitle().isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Notice title is required.");
            return "redirect:/notices/add";
        }

        noticeRepository.save(notice);

        redirectAttributes.addFlashAttribute(
                "message", "Notice added successfully!");

        return "redirect:/notices";
    }

    @GetMapping("/notices/edit/{id}")
    public String editNoticeForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Notice notice = noticeRepository.findById(id).orElse(null);

        if (notice == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Notice not found.");
            return "redirect:/notices";
        }

        model.addAttribute("notice", notice);
        return "edit-notice";
    }

    @PostMapping("/notices/update/{id}")
    public String updateNotice(
            @PathVariable Long id,
            @ModelAttribute Notice updatedNotice,
            RedirectAttributes redirectAttributes) {

        Notice existingNotice =
                noticeRepository.findById(id).orElse(null);

        if (existingNotice == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Notice not found.");
            return "redirect:/notices";
        }

        if (updatedNotice.getTitle() == null
                || updatedNotice.getTitle().isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Notice title is required.");
            return "redirect:/notices/edit/" + id;
        }

        existingNotice.setTitle(updatedNotice.getTitle());
        existingNotice.setDescription(updatedNotice.getDescription());
        existingNotice.setNoticeDate(updatedNotice.getNoticeDate());
        existingNotice.setCategory(updatedNotice.getCategory());

        noticeRepository.save(existingNotice);

        redirectAttributes.addFlashAttribute(
                "message", "Notice updated successfully!");

        return "redirect:/notices";
    }

    @PostMapping("/notices/delete/{id}")
    public String deleteNotice(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (noticeRepository.existsById(id)) {
            noticeRepository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "message", "Notice deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute(
                    "error", "Notice not found.");
        }

        return "redirect:/notices";
    }
}
