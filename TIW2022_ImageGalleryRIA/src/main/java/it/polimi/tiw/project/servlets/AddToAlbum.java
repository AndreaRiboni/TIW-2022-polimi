package it.polimi.tiw.project.servlets;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AlbumDAO;
import it.polimi.tiw.project.utility.DBUtils;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/AddToAlbum")
@MultipartConfig
public class AddToAlbum extends HttpServlet {
    private Connection connection;

    public void init() throws UnavailableException {
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
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Wrong parameters");
            return;
        }
        try {
            if(!aDAO.checkCreator(username, album_id)){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Unable to add photos to this album: wrong privileges");
                return;
            }
            aDAO.addPhotoIntoAlbum(album_id, photo_id);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Unable to add to album");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}