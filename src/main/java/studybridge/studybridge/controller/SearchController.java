
package studybridge.studybridge.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import studybridge.studybridge.model.Deadline;
import studybridge.studybridge.model.Notice;
import studybridge.studybridge.model.Subject;
import studybridge.studybridge.model.Syllabus;

import studybridge.studybridge.repository.DeadlineRepository;
import studybridge.studybridge.repository.NoticeRepository;
import studybridge.studybridge.repository.SubjectRepository;
import studybridge.studybridge.repository.SyllabusRepository;

@Controller
public class SearchController {

    private final SubjectRepository subjectRepository;
    private final NoticeRepository noticeRepository;
    private final DeadlineRepository deadlineRepository;
    private final SyllabusRepository syllabusRepository;

    public SearchController(
            SubjectRepository subjectRepository,
            NoticeRepository noticeRepository,
            DeadlineRepository deadlineRepository,
            SyllabusRepository syllabusRepository) {

        this.subjectRepository = subjectRepository;
        this.noticeRepository = noticeRepository;
        this.deadlineRepository = deadlineRepository;
        this.syllabusRepository = syllabusRepository;
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(name = "q", required = false, defaultValue = "")
            String q,
            Model model) {

        String keyword = q.trim();

        List<Subject> subjects = List.of();
        List<Notice> notices = List.of();
        List<Deadline> deadlines = List.of();
        List<Syllabus> syllabusList = List.of();

        if (!keyword.isBlank()) {

            subjects =
                    subjectRepository
                            .findBySubjectNameContainingIgnoreCaseOrSubjectCodeContainingIgnoreCase(
                                    keyword, keyword);

            notices =
                    noticeRepository
                            .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                                    keyword, keyword);

            deadlines =
                    deadlineRepository
                            .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                                    keyword, keyword);

            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);

            syllabusList = syllabusRepository.findAll()
                    .stream()
                    .filter(syllabus ->
                            contains(syllabus.getUnitName(), lowerKeyword)
                                    || contains(syllabus.getSyllabusContent(), lowerKeyword)
                                    || contains(syllabus.getExtractedText(), lowerKeyword))
                    .toList();
        }

        model.addAttribute("q", keyword);

        model.addAttribute("subjects", subjects);
        model.addAttribute("notices", notices);
        model.addAttribute("deadlines", deadlines);
        model.addAttribute("syllabusList", syllabusList);

        model.addAttribute("subjectCount", subjects.size());
        model.addAttribute("noticeCount", notices.size());
        model.addAttribute("deadlineCount", deadlines.size());
        model.addAttribute("syllabusCount", syllabusList.size());

        model.addAttribute(
                "totalResults",
                subjects.size()
                        + notices.size()
                        + deadlines.size()
                        + syllabusList.size());

        return "search";
    }

    private boolean contains(String value, String keyword) {
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(keyword);
    }
}
