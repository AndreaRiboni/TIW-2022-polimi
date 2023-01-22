package it.polimi.tiw.project.servlets;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.CommentDAO;
import it.polimi.tiw.project.utility.DBUtils;
import it.polimi.tiw.project.utility.StringUtility;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "AddComment", value = "/AddComment")
@MultipartConfig
public class AddComment extends HttpServlet {
    private Connection connection;

    public void init() throws UnavailableException {
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
        comment = request.getParameter("comment").trim();
        req_photo_id = request.getParameter("photo-id");
        int photo_id = -1;
        /* Initial parameters validation */
        try {
            photo_id = Integer.parseInt(req_photo_id);
        } catch (NumberFormatException nf){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Photo's ID must be an integer value");
            return;
        }
        if(StringUtility.isNullOrEmpty(comment)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Comment can not be empty");
            return;
        }

        try {
            c_dao.createComment(comment, username, photo_id);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Unable to add comment");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}