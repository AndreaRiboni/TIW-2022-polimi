package it.polimi.tiw.project.beans;
import java.util.Date;

public class Album {
    private int id;
    private String title;
    private Date creation_date;
    private int sort_number;
    private String username;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getCreationDate() { return creation_date; }
    public void setCreationDate(Date creation_date) { this.creation_date = creation_date; }

    public int getSortNumber() { return sort_number; }
    public void setSortNumber(int sort_number) { this.sort_number = sort_number; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

}