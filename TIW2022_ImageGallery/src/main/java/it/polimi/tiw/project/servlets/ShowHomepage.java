package it.polimi.tiw.project.servlets;

import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.Photo;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AlbumDAO;
import it.polimi.tiw.project.dao.PhotoDAO;
import it.polimi.tiw.project.utility.DBUtils;
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
import java.util.List;

@WebServlet(name = "ShowHomepage", value = "/ShowHomepage")
public class ShowHomepage extends HttpServlet {
    private Connection connection;
    private TemplateEngine templateEngine;

    public void init() throws ServletException {
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        AlbumDAO album_data = new AlbumDAO(connection);
        List<Album> my_albums = null, other_albums = null;
        User logged_in = (User) request.getSession().getAttribute("user");
        try {
            my_albums = album_data.getAlbumByUser(logged_in.getUsername(), false);
            other_albums = album_data.getAlbumByUser(logged_in.getUsername(), true);
            String path = "/homepage.html";
            final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
            ctx.setVariable("my_albums", my_albums);
            ctx.setVariable("other_albums", other_albums);
            templateEngine.process(path, ctx, response.getWriter());
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            forwardToErrorPage(request, response, "Unable to generate the homepage");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
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
