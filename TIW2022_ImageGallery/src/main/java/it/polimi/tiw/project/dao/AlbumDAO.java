package it.polimi.tiw.project.dao;

import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.Photo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlbumDAO {
    private Connection connection;

    public AlbumDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * This functions returns every album created by the specified user if avoid_id is false
     * It returns every album not created by the specified user otherwise
     * @param username username to select from / to avoid
     * @param avoid_id true if we want to avoid user's albums, false if we want ONLY that user's albums
     * @return list of the related albums
     * @throws SQLException sql-related error occurred
     */
    public List<Album> getAlbumByUser(String username, boolean avoid_id) throws SQLException {
        List<Album> albums = new ArrayList<>();
        String avoid = avoid_id ? "!" : "";
        String query = "SELECT ID, title, creation_date, IDUser FROM Album WHERE IDUser " + avoid + "= ? ORDER BY creation_date DESC";
        ResultSet result = null;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setString(1, username);
            result = pstatement.executeQuery();
            while (result.next()) {
                Album album = new Album();
                album.setId(result.getInt("ID"));
                album.setTitle(result.getString("title"));
                album.setCreationDate(result.getDate("creation_date"));
                album.setUsername(result.getString("IDUser"));
                albums.add(album);
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
        return albums;
    }

    /**
     * This functions returns every album created by the specified user if avoid_id is false
     * It returns every album not created by the specified user otherwise
     * @param id album's id
     * @return list of the related albums
     * @throws SQLException sql-related error occurred
     */
    public Album getAlbumByID(int id) throws SQLException {
        Album album = new Album();
        String query = "SELECT ID, title, creation_date, IDUser FROM Album WHERE ID = ?";
        ResultSet result = null;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setInt(1, id);
            result = pstatement.executeQuery();
            if (result.next()) {
                album.setId(result.getInt("ID"));
                album.setTitle(result.getString("title"));
                album.setCreationDate(result.getDate("creation_date"));
                album.setUsername(result.getString("IDUser"));
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
        return album;
    }

    /**
     * Creates a new album
     * @param title album's title
     * @param username creator
     * @return code
     * @throws SQLException sql error description
     */
    public int createAlbum(String title, String username) throws SQLException {
        String query = "INSERT into album (title, IDUser) VALUES(?, ?)";
        int code = 0;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setString(1, title);
            pstatement.setString(2, username);
            code = pstatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
            } catch (Exception e1) {
                System.out.println("create album error");
            }
        }
        return code;
    }

    /**
     * Returns the photo identified by photo_id if the album identified by album_id contains it
     * @param album_id album's id
     * @param photo_id photo's id
     * @return photo if it's contained, null otherwise
     * @throws SQLException a sql error occurred
     */
    public Photo getPhotoIfContained(int album_id, int photo_id) throws SQLException {
        String query = "SELECT * FROM photo JOIN photoinalbum ON IDPhoto = Photo.ID WHERE IDPhoto = ? AND IDAlbum = ? LIMIT 1";
        ResultSet result = null;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setInt(1, photo_id);
            pstatement.setInt(2, album_id);
            result = pstatement.executeQuery();
            if (result.next()) {
                Photo photo = new Photo();
                photo.setId(result.getInt("ID"));
                photo.setTitle(result.getString("title"));
                photo.setPath(result.getString("path"));
                photo.setUploadDate(result.getDate("upload_date"));
                photo.setAltText(result.getString("alt_text"));
                return photo;
            } else return null;
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
    }

    /**
     * Determines if the specified user is the creator of the album
     * @param username user to check
     * @param album_id album to verify
     * @return true if username is the creator of album_id's album
     * @throws SQLException an error occurred
     */
    public boolean checkCreator(String username, int album_id) throws SQLException {
        String query = "SELECT * FROM Album JOIN User ON Album.IDuser = User.Username WHERE Username = ? AND ID = ? LIMIT 1";
        ResultSet result = null;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setString(1, username);
            pstatement.setInt(2, album_id);
            result = pstatement.executeQuery();
            if (result.next()) {
                return true;
            } else return false;
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
    }

    /**
     * adds a photo into an album
     * @param album_id destination album
     * @param photo_id photo to be added
     * @return code
     * @throws SQLException an error occurred
     */
    public int addPhotoIntoAlbum(int album_id, int photo_id) throws SQLException {
        String query = "INSERT into PhotoInAlbum (IDPhoto, IDAlbum) VALUES(?, ?)";
        int code = 0;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setInt(1, photo_id);
            pstatement.setInt(2, album_id);
            code = pstatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        } finally {
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
            } catch (Exception e1) {
                System.out.println("add to album error");
            }
        }
        return code;
    }

}
