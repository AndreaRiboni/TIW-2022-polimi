package it.polimi.tiw.project.beans;

import java.util.Date;

public class Photo {
    private int id;
    private String title;
    private Date upload_date;
    private String alt_text;
    private String path;
    private String id_user;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getUploadDate() { return upload_date; }
    public void setUploadDate(Date upload_date) { this.upload_date = upload_date; }

    public String getAltText() { return alt_text; }
    public void setAltText(String alt_text) { this.alt_text = alt_text; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getIdUser() { return id_user; }
    public void setIdUser(String idUser) { this.id_user = idUser; }
}