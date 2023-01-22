package it.polimi.tiw.project.packets;

import it.polimi.tiw.project.beans.Comment;
import it.polimi.tiw.project.beans.Photo;

import java.util.List;

public class PhotoPacket {

    private Photo photo;
    private List<Comment> comments;


    public PhotoPacket(Photo photo, List<Comment> comments) {
        super();
        this.photo = photo;
        this.comments = comments;
    }

    public Photo getPhoto() {
        return photo;
    }

    public List<Comment> getComments() {
        return comments;
    }

}