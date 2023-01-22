package it.polimi.tiw.project.servlets;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AlbumDAO;
import it.polimi.tiw.project.utility.DBUtils;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/Logout")
public class Logout extends HttpServlet {
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.setStatus(HttpServletResponse.SC_OK);
        request.getSession().setAttribute("user", null);
        response.sendRedirect("./index.html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}