package it.polimi.tiw.project.servlets;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.UserDAO;
import it.polimi.tiw.project.utility.DBUtils;
import it.polimi.tiw.project.utility.StringUtility;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/LoginRegister")
public class LoginRegister extends HttpServlet {
    private Connection connection;
    private TemplateEngine templateEngine;

    public void init() throws UnavailableException {
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
        connection = DBUtils.createConnection(getServletContext());
    }



    private void executeLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (StringUtility.isNullOrEmpty(username, password)) {
            forwardToErrorPage(request, response, "Missing parameters");
            return;
        }

        UserDAO user_data = new UserDAO(connection);
        User user = null;
        try {
            user = user_data.checkCredentials(username, StringUtility.hash(password));
        } catch (SQLException e) {
            forwardToErrorPage(request, response, "Failure in database credential checking");
            return;
        }
        if (user == null) {
            forwardToErrorPage(request, response, "Incorrect credentials");
        } else {
            request.getSession().setAttribute("user", user);
            String path = "./ShowHomepage";
            response.sendRedirect(path);
        }
    }


    private void executeRegister(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        /* Extracts parameters from POST */
        String username = null;
        String email = null;
        String passConfirm = null;
        String password = null;
        username = request.getParameter("username").trim();
        email = request.getParameter("email");
        passConfirm = request.getParameter("conf-password");
        password = request.getParameter("password");
        /* Initial parameters validation */
        if(StringUtility.isNullOrEmpty(username, email, passConfirm, password)) {
            forwardToErrorPage(request, response, "Missing parameters");
            return;
        }
        if(!isMailValid(email)){
            forwardToErrorPage(request, response, "Wrong email format");
            return;
        }
        if(!passConfirm.equals(password)) {
            forwardToErrorPage(request, response, "The two passwords must be identical");
            return;
        }

        UserDAO uDAO = new UserDAO(connection);
        User user = null;
        try {
            if(uDAO.checkByUsername(username)) {
                forwardToErrorPage(request, response, "Username already taken");
                return;
            }
            if(uDAO.checkByEmail(email)) {
                forwardToErrorPage(request, response, "Email already used");
                return;
            }
            uDAO.createUser(username, email, StringUtility.hash(password));
            user = uDAO.getUser(username);
        } catch (SQLException e) {
            e.printStackTrace();
            forwardToErrorPage(request, response, "Unable to register user");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        request.getSession().setAttribute("user", user);
        response.sendRedirect("./ShowHomepage");
    }

    private boolean isMailValid(String email){
        /* OWASP email regex */
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("./");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String req_login = request.getParameter("login");
        String req_register = request.getParameter("register");

        if((req_login == null || req_login.isEmpty()) && (req_register == null || req_register.isEmpty())){
            forwardToErrorPage(request, response, "Missing parameters");
            return;
        }

        if(req_login!=null && !req_login.isEmpty()){
            executeLogin(request, response);
        } else {
            executeRegister(request, response);
        }
    }

    private void forwardToErrorPage(HttpServletRequest request, HttpServletResponse response, String error) throws ServletException, IOException{
        request.setAttribute("error", error);
        forward(request, response, "/errorpage.html");
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException{
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        templateEngine.process(path, ctx, response.getWriter());

    }
}