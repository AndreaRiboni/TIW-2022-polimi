package it.polimi.tiw.project.dao;

import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.Photo;
import it.polimi.tiw.project.utility.StringUtility;

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
        String query = "SELECT ID, title, creation_date, IDUser FROM Album WHERE IDUser " + avoid + "= ? ORDER BY sort_id, creation_date DESC";
        ResultSet result = null;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setString(1, username);
            result = pstatement.executeQuery();
            while (result.next()) {
                Album album = new Album();
                album.setId(result.getInt("ID"));
                album.setTitle(StringUtility.getUnicode(result.getString("title")));
                album.setCreationDate(result.getDate("creation_date"));
                album.setUsername(StringUtility.getUnicode(result.getString("IDUser")));
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
                album.setTitle(StringUtility.getUnicode(result.getString("title")));
                album.setCreationDate(result.getDate("creation_date"));
                album.setUsername(StringUtility.getUnicode(result.getString("IDUser")));
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
        //INSERT INTO album (title, IDUser) VALUES ('Le città italiane', 'andreariboni');
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

    /**
     * It sets a specific sort_id value to the specified album
     * @param album_id album's id
     * @param sort_id new sort_id value
     * @throws SQLException an error occurred
     */
    private void updateSingleAlbumOrder(int album_id, int sort_id) throws SQLException{
        String query = "UPDATE Album SET sort_id = ? WHERE Id = ?";
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setInt(1, sort_id);
            pstatement.setInt(2, album_id);
            pstatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        } finally {
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
            } catch (Exception e1) {
                System.out.println("update order error");
            }
        }
    }

    /**
     * Reorder a user's albums. Firstly we check that each album specified by the album_id's array has been created
     * by that user. Then, we begin a transaction in order to set the new sort_id values atomically
     * @param username user's username
     * @param albums_id array of albums' ids. Each album id must be crated by that user. Respective sort_ids are
     *                  generated using the order of this array
     * @throws Exception an error occurred
     * */
    public void updateOrder(String username, int[] albums_id) throws Exception{
        //controlliamo che l'utente stia aggiornando solo album propri (tutti)
        List<Album> other_albums = getAlbumByUser(username, true);
        for(Album a : other_albums){
            for(int i : albums_id){
                if(a.getId() == i) throw new Exception("You can not update other users' albums");
            }
        }
        try {
            //begin transaction
            connection.setAutoCommit(false);
            for (int i = 0; i < albums_id.length; i++) {
                updateSingleAlbumOrder(albums_id[i], i);
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException sqle){
            connection.rollback();
            throw new Exception("Unable to update the albums order");
        }
    }

}
