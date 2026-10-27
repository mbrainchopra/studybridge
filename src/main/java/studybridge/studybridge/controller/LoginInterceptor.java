
package studybridge.studybridge.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();

        // Allow public pages without login
        if (uri.equals(contextPath + "/")
                || uri.equals(contextPath + "/login")
                || uri.equals(contextPath + "/register")
                || uri.equals(contextPath + "/logout")
                || uri.equals(contextPath + "/error")
                || uri.startsWith(contextPath + "/css/")
                || uri.startsWith(contextPath + "/js/")
                || uri.startsWith(contextPath + "/images/")
                || uri.equals(contextPath + "/favicon.ico")) {

            return true;
        }

        // Allow access to protected pages only after login
        if (request.getSession(false) != null
                && request.getSession(false)
                .getAttribute("loggedInUser") != null) {

            return true;
        }

        // Redirect unauthenticated users to login
        response.sendRedirect(contextPath + "/login");
        return false;
    }
}
