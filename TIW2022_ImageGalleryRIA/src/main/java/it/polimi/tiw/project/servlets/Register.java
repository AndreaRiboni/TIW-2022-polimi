package it.polimi.tiw.project.servlets;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.UserDAO;
import it.polimi.tiw.project.utility.DBUtils;
import it.polimi.tiw.project.utility.StringUtility;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "Register", value = "/Register")
@MultipartConfig
public class Register extends HttpServlet {
    private Connection connection;

    public void init() throws UnavailableException {
        connection = DBUtils.createConnection(getServletContext());
    }

    private boolean isMailValid(String email){
        /* OWASP email regex */
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* Extracts parameters from POST */
        String username = null;
        String email = null;
        String passConfirm = null;
        String password = null;
        username = request.getParameter("username").trim();
        email = request.getParameter("email").trim();
        passConfirm = request.getParameter("conf-pass").trim();
        password = request.getParameter("password").trim();
        /* Initial parameters validation */
        if(StringUtility.isNullOrEmpty(username, email, passConfirm, password)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing parameters");
            return;
        }
        if(!isMailValid(email)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Wrong email format");
            return;
        }
        if(!passConfirm.equals(password)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("The two passwords must be identical");
            return;
        }

        UserDAO uDAO = new UserDAO(connection);
        User user = null;
        try {
            if(uDAO.checkByUsername(username)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Username already taken");
                return;
            }
            if(uDAO.checkByEmail(email)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Email already used");
                return;
            }
            uDAO.createUser(username, email, StringUtility.hash(password));
            user = uDAO.getUser(username);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Unable to register user");
            return;
        }

        request.getSession().setAttribute("user", user);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(user.getUsername());
    }
}
