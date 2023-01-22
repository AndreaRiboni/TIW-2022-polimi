package it.polimi.tiw.project.servlets;

import it.polimi.tiw.project.utility.StringUtility;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

@WebServlet(name = "DownloadPhoto", value = "/DownloadPhoto")
public class DownloadPhoto extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String imageId = request.getParameter("imageId");

            if (StringUtility.isNullOrEmpty(imageId)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

            byte[] imageData = Files.readAllBytes(
                    new File(
                            //"C:\\Users\\Andrea\\Pictures\\UploadImageGallery\\" + imageId
                            "/Users/ema/Documents/" + imageId
                    ).toPath()
            );// data from db for specified imageId
            OutputStream out = response.getOutputStream();
            out.write(imageData);
            out.flush();
            out.close();
        } catch (IOException e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
