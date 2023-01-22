package it.polimi.tiw.project.servlets;

import it.polimi.tiw.project.beans.Photo;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AlbumDAO;
import it.polimi.tiw.project.dao.PhotoDAO;
import it.polimi.tiw.project.utility.DBUtils;
import it.polimi.tiw.project.utility.StringUtility;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/CreateAlbum")
public class CreateAlbum extends HttpServlet {
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

    public void destroy(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* Extracts parameters from POST */
        String username = null;
        String title = null;
        username =  ((User) request.getSession().getAttribute("user")).getUsername();
        title = request.getParameter("title").trim();

        AlbumDAO aDAO = new AlbumDAO(connection);
        if(StringUtility.isNullOrEmpty(title)) {
            forwardToErrorPage(request, response, "Title must not be empty");
            return;
        }
        try {
            aDAO.createAlbum(title, username);
        } catch (SQLException e) {
            e.printStackTrace();
            forwardToErrorPage(request, response, "Unable to add album");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect("./ShowHomepage");
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