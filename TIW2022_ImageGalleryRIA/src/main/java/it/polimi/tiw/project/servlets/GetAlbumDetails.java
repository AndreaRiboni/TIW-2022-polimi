package it.polimi.tiw.project.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.Photo;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AlbumDAO;
import it.polimi.tiw.project.dao.PhotoDAO;
import it.polimi.tiw.project.packets.AlbumPacket;
import it.polimi.tiw.project.utility.DBUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "GetAlbumDetails", value = "/GetAlbumDetails")
@MultipartConfig
public class GetAlbumDetails extends HttpServlet {
    private Connection connection;

    public void init() throws ServletException {
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
        AlbumDAO album_data = new AlbumDAO(connection);
        PhotoDAO photo_data = new PhotoDAO(connection);
        List<Photo> photos = null, available_photos = null;
        Album album = null;
        String
                param_start_index = request.getParameter("start_index"),
                param_album_id = request.getParameter("album_id");
        int album_id = 0, start_index = 0;
        try {
            album_id = Integer.parseInt(param_album_id);
            start_index = param_start_index == null ? 0 : Integer.parseInt(param_start_index);
            if(album_id < 0 || start_index < 0 || start_index % 5 != 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException | NullPointerException ex){
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Wrong request parameters");
        }
        try {
            album = album_data.getAlbumByID(album_id);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Unable to get album");
        }
        try {
            photos = photo_data.getPhotosByAlbum(album_id);
            User logged_in = (User) request.getSession().getAttribute("user");
            //if album_id è mio:
            if(album_data.checkCreator(logged_in.getUsername(), album_id)) {
                available_photos = photo_data.getPhotoByUserNotInAlbum(logged_in.getUsername(), album_id);
            } else {
                available_photos = new ArrayList<>();
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Unable to get photos");
        }
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy MMM dd").create();
        String json = gson.toJson(new AlbumPacket(album, photos, available_photos));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}