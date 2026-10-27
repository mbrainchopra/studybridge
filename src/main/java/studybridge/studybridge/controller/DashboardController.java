
package studybridge.studybridge.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import studybridge.studybridge.model.Deadline;
import studybridge.studybridge.model.Notice;
import studybridge.studybridge.repository.DeadlineRepository;
import studybridge.studybridge.repository.NoticeRepository;
import studybridge.studybridge.repository.SubjectRepository;

@Controller
public class DashboardController {

    private final SubjectRepository subjectRepository;
    private final NoticeRepository noticeRepository;
    private final DeadlineRepository deadlineRepository;

    public DashboardController(
            SubjectRepository subjectRepository,
            NoticeRepository noticeRepository,
            DeadlineRepository deadlineRepository) {

        this.subjectRepository = subjectRepository;
        this.noticeRepository = noticeRepository;
        this.deadlineRepository = deadlineRepository;
    }

    // Default landing page
    @GetMapping("/")
    public String landingPage() {
        return "index";
    }

    // Dashboard after login
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        LocalDate today = LocalDate.now();

        List<Deadline> allDeadlines =
                deadlineRepository.findAll();

        List<Notice> allNotices =
                noticeRepository.findAll();

        List<Deadline> upcomingDeadlines =
                allDeadlines.stream()
                        .filter(deadline ->
                                deadline.getDueDate() != null)
                        .filter(deadline ->
                                !deadline.getDueDate().isBefore(today))
                        .sorted(
                                Comparator.comparing(
                                        Deadline::getDueDate))
                        .limit(5)
                        .toList();

        List<Notice> recentNotices =
                allNotices.stream()
                        .filter(notice ->
                                notice.getNoticeDate() != null)
                        .sorted(
                                Comparator.comparing(
                                                Notice::getNoticeDate)
                                        .reversed())
                        .limit(5)
                        .toList();

        long overdueCount =
                allDeadlines.stream()
                        .filter(deadline ->
                                deadline.getDueDate() != null)
                        .filter(deadline ->
                                deadline.getDueDate()
                                        .isBefore(today))
                        .count();

        long todayCount =
                allDeadlines.stream()
                        .filter(deadline ->
                                deadline.getDueDate() != null)
                        .filter(deadline ->
                                deadline.getDueDate()
                                        .isEqual(today))
                        .count();

        model.addAttribute(
                "subjectCount", subjectRepository.count());

        model.addAttribute(
                "noticeCount", allNotices.size());

        model.addAttribute(
                "deadlineCount", allDeadlines.size());

        model.addAttribute("overdueCount", overdueCount);
        model.addAttribute("todayCount", todayCount);

        model.addAttribute(
                "upcomingDeadlines", upcomingDeadlines);

        model.addAttribute(
                "recentNotices", recentNotices);

        model.addAttribute("today", today);

        return "dashboard";
    }
}
