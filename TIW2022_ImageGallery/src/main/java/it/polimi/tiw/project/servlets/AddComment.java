package it.polimi.tiw.project.servlets;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.CommentDAO;
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

@WebServlet(name = "AddComment", value = "/AddComment")
public class AddComment extends HttpServlet {
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CommentDAO c_dao = new CommentDAO(connection);
        /* Extracts parameters from POST and session*/
        String username = ((User)request.getSession().getAttribute("user")).getUsername();
        String comment = null;
        String req_photo_id = null;
        String req_album_id = null;
        comment = request.getParameter("comment").trim();
        req_photo_id = request.getParameter("photo-id");
        req_album_id = request.getParameter("album-id");
        int photo_id = -1, album_id = -1;
        /* Initial parameters validation */
        try {
            photo_id = Integer.parseInt(req_photo_id);
            album_id = Integer.parseInt(req_album_id);
        } catch (NumberFormatException nf){
            forwardToErrorPage(request, response, "Photo's ID must be an integer value");
            return;
        }
        if(StringUtility.isNullOrEmpty(comment)) {
            forwardToErrorPage(request, response, "Comment can not be empty");
            return;
        }

        try {
            c_dao.createComment(comment, username, photo_id);
        } catch (SQLException e) {
            e.printStackTrace();
            forwardToErrorPage(request, response, "Unable to add comment");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect(
                String.format(
                        "./ShowAlbum?album_id=%d&photo_id=%d",
                        album_id,
                        photo_id
                )
        );
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