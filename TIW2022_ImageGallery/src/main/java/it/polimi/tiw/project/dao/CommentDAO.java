package it.polimi.tiw.project.dao;

import it.polimi.tiw.project.beans.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    private Connection connection;

    public CommentDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Gets the comments posted on a photo
     * @param photo_id photo used to retrieve comments
     * @return list of related comments
     * @throws SQLException an error occurred
     */
    public List<Comment> getCommentsByPhoto(int photo_id) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT text, tstamp, IDUser FROM Comment WHERE IDPhoto = ? ORDER BY tstamp DESC";
        ResultSet result = null;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setInt(1, photo_id);
            result = pstatement.executeQuery();
            while (result.next()) {
                Comment comment = new Comment();
                comment.setText(result.getString("text"));
                comment.setTimestamp(result.getTimestamp("tstamp"));
                comment.setUsername(result.getString("IDUser"));
                comments.add(comment);
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
        return comments;
    }

    /**
     * Adds a comment to a photo
     * @param text comment's text
     * @param username comment's author
     * @param photo_id photo to be commented
     * @return code
     * @throws SQLException an error occurred
     */
    public int createComment(String text, String username, int photo_id) throws SQLException {
        //INSERT INTO album (title, IDUser) VALUES ('Le città italiane', 'andreariboni');
        String query = "INSERT into comment (text, IDUser, IDPhoto) VALUES(?, ?, ?)";
        int code = 0;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setString(1, text);
            pstatement.setString(2, username);
            pstatement.setInt(3, photo_id);
            code = pstatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
            } catch (Exception e1) {
                System.out.println("create comment error");
            }
        }
        return code;
    }
}
