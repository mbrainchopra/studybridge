
        package studybridge.studybridge.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import studybridge.studybridge.model.User;
import studybridge.studybridge.repository.UserRepository;

@Controller
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Open login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Open registration page
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // Register new student
    @PostMapping("/register")
    public String register(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {

        fullName = fullName.trim();
        email = email.trim().toLowerCase();

        if (fullName.isBlank()
                || email.isBlank()
                || password.isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "error", "Please fill in all fields.");

            return "redirect:/register";
        }

        if (userRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute(
                    "error", "This email is already registered.");

            return "redirect:/register";
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);

        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
                "message",
                "Registration successful! Please login.");

        return "redirect:/login";
    }

    // Login student
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = userRepository
                .findByEmail(email.trim().toLowerCase())
                .orElse(null);

        if (user != null
                && user.getPassword().equals(password)) {

            session.setAttribute("loggedInUser", user.getId());
            session.setAttribute("loggedInName", user.getFullName());

            // FIX: Redirect to the dashboard
            return "redirect:/dashboard";
        }

        redirectAttributes.addFlashAttribute(
                "error", "Invalid email or password.");

        return "redirect:/login";
    }

    // Logout student
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
