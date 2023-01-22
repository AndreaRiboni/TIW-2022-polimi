package it.polimi.tiw.project.servlets;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.PhotoDAO;
import it.polimi.tiw.project.utility.DBUtils;
import it.polimi.tiw.project.utility.StringUtility;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/UploadPhoto")
@MultipartConfig(
        fileSizeThreshold = 83_886_080 , // 6 MB
        maxFileSize = 10_485_760, // 10 MB
        maxRequestSize = 10_485_760 // 20 MB
)
public class UploadPhoto extends HttpServlet {
    private Connection connection = null;

    public void init() throws UnavailableException {
        connection = DBUtils.createConnection(getServletContext());
    }

    private String getUnusedPath(String folder, String file_name) {
        File f = null;
        int count = 0;
        String[] file_attributes = file_name.split("\\.");
        do {
            f = new File(folder + "upload" + count + "." + file_attributes[1]);
            count++;
        } while (f.exists());
        String path = f.getPath();
        return path;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PhotoDAO photo_dao = new PhotoDAO(connection);
        // gets absolute path of the web application
        // constructs path of the directory to save uploaded file
        //String uploadFilePath = "C:\\Users\\Andrea\\Pictures\\UploadImageGallery\\photos\\";
        String uploadFilePath = "/Users/ema/Documents/photos/";
        // creates upload folder if it does not exists
        File uploadFolder = new File(uploadFilePath);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
        Part part = request.getPart("file");
        String username = ((User)request.getSession().getAttribute("user")).getUsername();
        String title = null, alt = null;
        title = request.getParameter("title");
        alt = request.getParameter("alt");
        String file_name = part.getSubmittedFileName();
        String extension = part.getContentType();

        /* Initial parameters validation */
        if(StringUtility.isNullOrEmpty(title)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Title can not be empty");
            return;
        }
        if(StringUtility.isNullOrEmpty(extension)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("An error occurred while checking for the extension");
            return;
        }
        if (alt == null) alt = "";

        // allows only JPEG and PNG files to be uploaded
        if (!extension.equalsIgnoreCase("image/jpeg") && !extension.equalsIgnoreCase("image/png")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Wrong filetype");
            return;
        }

        String path = null;
        try {
            path = getUnusedPath(uploadFilePath, file_name);
            part.write(path);
            photo_dao.createPhoto(title, alt, path.substring(path.indexOf("photos")), username);
        } catch (IOException | SQLException e){
            e.printStackTrace();
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("An error occurred while uploading the photo");
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException sqle) {
        }
    }

}