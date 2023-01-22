package it.polimi.tiw.project.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.project.beans.Comment;
import it.polimi.tiw.project.beans.Photo;
import it.polimi.tiw.project.dao.CommentDAO;
import it.polimi.tiw.project.dao.PhotoDAO;
import it.polimi.tiw.project.packets.PhotoPacket;
import it.polimi.tiw.project.utility.DBUtils;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet(name = "GetPhoto", value = "/GetPhoto")
@MultipartConfig
public class GetPhoto extends HttpServlet {
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
        PhotoDAO photo_data = new PhotoDAO(connection);
        CommentDAO comment_data = new CommentDAO(connection);
        List<Comment> comments = null;
        Photo photo = null;
        String param_selected_photo = request.getParameter("photo_id");
        int photo_id = param_selected_photo == null ? -1 : Integer.parseInt(param_selected_photo);
        try {
            photo = photo_data.findPhotoById(photo_id);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Unable to find photo");
        }
        try {
            comments = comment_data.getCommentsByPhoto(photo_id);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Unable to collect comments");
        }
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        String uploaddate = new SimpleDateFormat("yyyy-MM-dd").format(photo.getUploadDate());
        String json = gson.toJson(new PhotoPacket(photo, comments)).replace(uploaddate+" 00:00:00", uploaddate); //bad workaround
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}