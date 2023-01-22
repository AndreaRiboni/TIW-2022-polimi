package it.polimi.tiw.project.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AlbumDAO;
import it.polimi.tiw.project.utility.DBUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "GetAlbums", value = "/GetAlbums")
@MultipartConfig
public class GetAlbums extends HttpServlet {
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
    /*
     * To get our albums: /GetAlbums
     * To get other users' albums: /GetAlbums?other_albums
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AlbumDAO album_data = new AlbumDAO(connection);
        boolean other_albums = request.getParameter("other_albums") != null;
        List<Album> albums = null;
        User logged_in = (User) request.getSession().getAttribute("user");
        try {
            albums = album_data.getAlbumByUser(logged_in.getUsername(), other_albums);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Unable to generate the homepage");
        }
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy MMM dd").create();
        String json = gson.toJson(albums);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}