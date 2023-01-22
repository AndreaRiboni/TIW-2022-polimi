package it.polimi.tiw.project.servlets;

import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.Comment;
import it.polimi.tiw.project.beans.Photo;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AlbumDAO;
import it.polimi.tiw.project.dao.CommentDAO;
import it.polimi.tiw.project.dao.PhotoDAO;
import it.polimi.tiw.project.utility.Counter;
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

@WebServlet(name = "ShowAlbum", value = "/ShowAlbum")
public class ShowAlbum extends HttpServlet {
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

    private void showDetailedPhoto(HttpServletRequest request, HttpServletResponse response, WebContext ctx, AlbumDAO a_dao, int album_index, int photo_index) throws SQLException, IOException, ServletException {
        //check that the selected photo is contained in the selected album
        Photo selected = a_dao.getPhotoIfContained(album_index, photo_index);
        if(selected == null){
            forwardToErrorPage(request, response, "The selected photo isn't contained in this album");
            return;
        }
        //comments
        CommentDAO c_dao = new CommentDAO(connection);
        List<Comment> comments = c_dao.getCommentsByPhoto(photo_index);
        ctx.setVariable("selected_photo", selected);
        ctx.setVariable("comments", comments);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        AlbumDAO album_data = new AlbumDAO(connection);
        PhotoDAO photo_data = new PhotoDAO(connection);
        String
                param_start_index = request.getParameter("start_index"),
                param_album_id = request.getParameter("album_id"),
                param_selected_photo = request.getParameter("photo_id");
        int album_id = 0, start_index = 0, photo_id = -1;
        try {
            album_id = Integer.parseInt(param_album_id);
            photo_id = param_selected_photo == null ? -1 : Integer.parseInt(param_selected_photo);
            start_index = param_start_index == null ? 0 : Integer.parseInt(param_start_index);
            if(album_id < 0 || start_index < 0 || start_index % 5 != 0) throw new NumberFormatException();
        } catch (NumberFormatException | NullPointerException ex){
            ex.printStackTrace();
            forwardToErrorPage(request, response, "Wrong request parameters");
        }
        Album selected_album = null;
        List<Photo> album_photos = null;

        try {
            selected_album = album_data.getAlbumByID(album_id);
            album_photos = photo_data.getPhotosByAlbum(album_id, start_index);
            String path = "/albumpage.html";
            final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
            //if a photo is selected
            if(photo_id != -1){
                showDetailedPhoto(request, response, ctx, album_data, album_id, photo_id);
            }
            //"add photo form" can be visibile only if the logged in user has created the album
            String logged_in = ((User) request.getSession().getAttribute("user")).getUsername();
            boolean can_add_photo = album_data.checkCreator(logged_in, album_id);
            List<Photo> available_photos = null;
            if(can_add_photo) {
                available_photos = photo_data.getPhotoByUserNotInAlbum(logged_in, album_id);
            }
            ctx.setVariable("available_photos", available_photos);
            ctx.setVariable("selected_album", selected_album);
            ctx.setVariable("album_photos", album_photos);
            ctx.setVariable("start_index", start_index);
            ctx.setVariable("counter", new Counter(0));
            templateEngine.process(path, ctx, response.getWriter());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to generate the album page");
            forwardToErrorPage(request, response, "Unable to add album");
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
