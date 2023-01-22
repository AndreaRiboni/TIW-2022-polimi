package it.polimi.tiw.project.packets;

import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.Photo;

import java.util.List;

public class AlbumPacket {

    private final Album album;
    private final List<Photo> photos;
    private final List<Photo> available_photos;


    public AlbumPacket(Album album, List<Photo> photos, List<Photo> available_photos) {
        this.album = album;
        this.photos = photos;
        this.available_photos = available_photos;
    }

    public Album getAlbum() {
        return album;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public List<Photo> getAvailablePhotos(){
        return available_photos;
    }

}