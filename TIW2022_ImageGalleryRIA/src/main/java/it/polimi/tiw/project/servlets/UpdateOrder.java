package it.polimi.tiw.project.servlets;

import com.google.gson.*;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AlbumDAO;
import it.polimi.tiw.project.utility.DBUtils;
import it.polimi.tiw.project.utility.StringUtility;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "UpdateOrder", value = "/UpdateOrder")
@MultipartConfig
public class UpdateOrder extends HttpServlet {
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
        AlbumDAO a_dao = new AlbumDAO(connection);
        /* Extracts parameters from POST and session*/
        String username = ((User)request.getSession().getAttribute("user")).getUsername();
        String req_albums_order = request.getParameter("albums_order");
        if(StringUtility.isNullOrEmpty(req_albums_order)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("The order list can not be empty");
            return;
        }
        int[] albums_id = null;
        try{
            albums_id = parse(req_albums_order);
        } catch (NumberFormatException nf){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Album ID must be an integer value");
            return;
        }

        try {
            a_dao.updateOrder(username, albums_id);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(e.getMessage());
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }

    private int[] parse(String jsonLine) throws NumberFormatException {
        Gson gson = new Gson();
        String[] albums_id_str = gson.fromJson(jsonLine, String[].class);
        int[] albums_id = new int[albums_id_str.length];
        for(int i = 0; i < albums_id.length; i++){
            albums_id[i] = Integer.parseInt(albums_id_str[i]);
        }
        return albums_id;
    }
}