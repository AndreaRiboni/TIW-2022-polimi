package it.polimi.tiw.project.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/HomeChecker")
public class HomeChecker implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession s = req.getSession(false);

        if(s != null) {
            Object user = s.getAttribute("user");
            if(user != null) {
                chain.doFilter(request, response);
                return;
            }
        }

        res.sendRedirect(req.getServletContext().getContextPath() + "/LoginRegister");
    }
}