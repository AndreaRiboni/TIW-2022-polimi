package it.polimi.tiw.project.servlets;

import it.polimi.tiw.project.beans.Photo;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AlbumDAO;
import it.polimi.tiw.project.dao.PhotoDAO;
import it.polimi.tiw.project.utility.DBUtils;
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

@WebServlet("/AddToAlbum")
public class AddToAlbum extends HttpServlet {
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* Extracts parameters from POST */
        String req_photo_id = null, req_album_id = null;
        String title = null;
        String username =  ((User) request.getSession().getAttribute("user")).getUsername();
        req_photo_id = request.getParameter("photo-id");
        req_album_id = request.getParameter("album-id");
        int photo_id = 0, album_id = 0;
        AlbumDAO aDAO = new AlbumDAO(connection);
        try{
            photo_id = Integer.parseInt(req_photo_id);
            album_id = Integer.parseInt(req_album_id);
        } catch(NumberFormatException | NullPointerException nfe){
            nfe.printStackTrace();
            forwardToErrorPage(request, response, "Wrong parameters");
            return;
        }
        try {
            if(!aDAO.checkCreator(username, album_id)){
                forwardToErrorPage(request, response, "Unable to add photos to this album: wrong privileges");
                return;
            }
            aDAO.addPhotoIntoAlbum(album_id, photo_id);
        } catch (SQLException e) {
            e.printStackTrace();
            forwardToErrorPage(request, response, "Unable to add to album");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect(
                String.format(
                        "./ShowAlbum?album_id=%d",
                        album_id
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