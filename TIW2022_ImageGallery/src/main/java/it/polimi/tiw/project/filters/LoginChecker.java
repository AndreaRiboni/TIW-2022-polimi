package it.polimi.tiw.project.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/LoginChecker")
public class LoginChecker implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession s = req.getSession(false);

        if(s != null) {
            Object user = s.getAttribute("user");
            if(user != null) {
                res.sendRedirect(req.getServletContext().getContextPath() + "/ShowHomepage");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}