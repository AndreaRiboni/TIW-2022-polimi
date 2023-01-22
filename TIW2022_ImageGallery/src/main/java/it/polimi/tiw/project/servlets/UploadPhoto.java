package it.polimi.tiw.project.servlets;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.PhotoDAO;
import it.polimi.tiw.project.utility.DBUtils;
import it.polimi.tiw.project.utility.StringUtility;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@WebServlet("/UploadPhoto")
@MultipartConfig(
        fileSizeThreshold = 83_886_080 , // 6 MB
        maxFileSize = 10_485_760, // 10 MB
        maxRequestSize = 10_485_760 // 20 MB
)
public class UploadPhoto extends HttpServlet {
    private Connection connection = null;

    private TemplateEngine templateEngine;

    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
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
        title = request.getParameter("title").trim();
        alt = request.getParameter("alt");
        /* Initial parameters validation */
        if(StringUtility.isNullOrEmpty(title)) {
            forwardToErrorPage(request, response, "Wrong parameters");
            return;
        }
        if (alt == null) alt = "";

        //Insert image on database
        //File exists
        String file_name = part.getSubmittedFileName();
        String extension = part.getContentType();

        // allows only JPEG and PNG files to be uploaded
        if (!extension.equalsIgnoreCase("image/jpeg") && !extension.equalsIgnoreCase("image/png")) {
            //response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong filetype");
            forwardToErrorPage(request, response, "Wrong filetype");
            return;
        }

        String path = null;
        try {
            path = getUnusedPath(uploadFilePath, file_name);
            part.write(path);
            photo_dao.createPhoto(title, alt, path.substring(path.indexOf("photos")), username);
        } catch (IOException | SQLException e){
            e.printStackTrace();
            //response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while uploading the photo");
            forwardToErrorPage(request, response, "An error occurred while uploading the photo");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect("./ShowHomepage");
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