package it.polimi.tiw.project.dao;

import it.polimi.tiw.project.beans.Comment;
import it.polimi.tiw.project.beans.Photo;
import it.polimi.tiw.project.utility.StringUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhotoDAO {
    private Connection connection;

    public PhotoDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * used to get an album's photos
     * @param album_id album used to retrieve the photos
     * @return list of selected photos
     * @throws SQLException an error occurred
     */
    public List<Photo> getPhotosByAlbum(int album_id) throws SQLException {
        List<Photo> photos = new ArrayList<>();
        String query = "SELECT Photo.ID AS IDPhoto, title, upload_date, path FROM photoinalbum JOIN photo ON photoinalbum.IDPhoto = Photo.ID WHERE IDAlbum = ? ORDER BY upload_date DESC";
        ResultSet result = null;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setInt(1, album_id);
            result = pstatement.executeQuery();
            while (result.next()) {
                Photo photo = new Photo();
                photo.setId(result.getInt("IDPhoto"));
                photo.setTitle(StringUtility.getUnicode(result.getString("title")));
                photo.setUploadDate(result.getDate("upload_date"));
                photo.setPath(result.getString("path"));
                photos.add(photo);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return photos;
    }

    /**
     * used to get the photos related to a user which are not contained in the specified album
     * @param user_id user of reference
     * @param album_id the album whose photos should not be considered
     * @return list of photos
     * @throws SQLException an error occurred
     */
    public List<Photo> getPhotoByUserNotInAlbum(String user_id, int album_id) throws SQLException {
        List<Photo> photos = new ArrayList<>();
        String query = "SELECT Photo.ID, title FROM User JOIN photo ON username = IDUser WHERE Username = ? AND Photo.ID NOT IN (" +
                "SELECT PIA.IDPhoto FROM PhotoInAlbum AS PIA WHERE PIA.IDAlbum = ?" +
                ");";
        ResultSet result = null;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setString(1, user_id);
            pstatement.setInt(2, album_id);
            result = pstatement.executeQuery();
            while (result.next()) {
                Photo photo = new Photo();
                photo.setId(result.getInt("ID"));
                photo.setTitle(StringUtility.getUnicode(result.getString("title")));
                photos.add(photo);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (Exception e1) {
                throw new SQLException(e1);
            }
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
            } catch (Exception e2) {
                throw new SQLException(e2);
            }
        }
        return photos;
    }

    /**
     * Get a photo details searching it through its id
     * @param photoId photo's id
     * @return photo details
     * @throws SQLException an error occurred
     */
    public Photo findPhotoById(int photoId) throws SQLException {
        Photo photo = null;
        String query = "SELECT * FROM photo WHERE id = ?";
        try(PreparedStatement pstatement = connection.prepareStatement(query)) {
            pstatement.setInt(1, photoId);
            try(ResultSet result = pstatement.executeQuery()) {
                if(result.next()) {
                    photo = new Photo();
                    photo.setId(result.getInt("id"));
                    photo.setTitle(StringUtility.getUnicode(result.getString("title")));
                    photo.setUploadDate(result.getDate("upload_date"));
                    photo.setAltText(StringUtility.getUnicode(result.getString("alt_text")));
                    photo.setPath(result.getString("path"));
                }
            }
        }
        return photo;
    }

    /**
     * Adds a photo
     * @param title photo's title
     * @param altText photo's alternative text
     * @param path photo's path
     * @param user photo's uploader
     * @return code
     * @throws SQLException an error occurred
     */
    public int createPhoto(String title, String altText, String path, String user) throws SQLException {
        String query = "INSERT into photo (title, alt_text, path, IDUser) VALUES(?, ?, ?, ?)";
        int code = 0;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setString(1, title);
            pstatement.setString(2, altText);
            pstatement.setString(3, path);
            pstatement.setString(4, user);
            code = pstatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
            } catch (Exception e1) {
                System.out.println("create photo error");
            }
        }
        return code;
    }
}