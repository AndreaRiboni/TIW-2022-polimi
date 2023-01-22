package it.polimi.tiw.project.dao;

import it.polimi.tiw.project.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a user
     * @param username user's username
     * @param mail user's email
     * @param password user's password
     * @return code
     * @throws SQLException an error occurred
     */
    public int createUser(String username, String mail, String password) throws SQLException {
        String query = "INSERT into user (username, mail, password) VALUES(?, ?, ?)";
        int code = 0;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setString(1, username);
            pstatement.setString(2, mail);
            pstatement.setString(3, password);
            code = pstatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            try {
                if (pstatement != null) {
                    pstatement.close();
                }
            } catch (Exception e1) {
                System.out.println("create user error");
            }
        }
        return code;
    }

    /**
     * Retrieves an user
     * @param username user's username
     * @return the user
     * @throws SQLException an error occurred
     */
    public User getUser(String username) throws SQLException {
        User user = new User();
        String query = "SELECT username, mail FROM user WHERE username = ?";
        ResultSet result = null;
        PreparedStatement pstatement = null;
        try {
            pstatement = connection.prepareStatement(query);
            pstatement.setString(1, username);
            result = pstatement.executeQuery();
            result.next();
            user.setUsername(result.getString("username"));
            user.setMail(result.getString("mail"));
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
        return user;
    }

    /**
     * Determines if a certain user exists
     * @param username username
     * @param password related password
     * @return the user if it exists, null otherwise
     * @throws SQLException an error occurred
     */
    public User checkCredentials(String username, String password) throws SQLException {
        String query = "SELECT * FROM user WHERE username = ? AND password =?";
        try (PreparedStatement pstatement = connection.prepareStatement(query);) {
            pstatement.setString(1, username);
            pstatement.setString(2, password);
            try (ResultSet result = pstatement.executeQuery();) {
                if (!result.isBeforeFirst()) // no results, credential check failed
                    return null;
                else {
                    result.next();
                    User user = new User();
                    user.setUsername(result.getString("username"));
                    user.setMail(result.getString("mail"));
                    user.setPassword(result.getString("password"));
                    return user;
                }
            }
        }
    }

    /**
     * Check if a user with a determined username exists
     * @param username username to check
     * @return true if it exists, false otherwise
     * @throws SQLException an error occurred
     */
    public boolean checkByUsername(String username) throws SQLException {
        String query = "SELECT * FROM user WHERE username = ? LIMIT 1";
        try(PreparedStatement pstatement = connection.prepareStatement(query)) {
            pstatement.setString(1, username);
            try(ResultSet result = pstatement.executeQuery()) {
                if(result.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if a user with a determined email exists
     * @param email email to check
     * @return true if it exists, false otherwise
     * @throws SQLException an error occurred
     */
    public boolean checkByEmail(String email) throws SQLException {
        String query = "SELECT * FROM user WHERE mail = ? LIMIT 1";
        try(PreparedStatement pstatement = connection.prepareStatement(query)) {
            pstatement.setString(1, email);
            try(ResultSet result = pstatement.executeQuery()) {
                if(result.next()) {
                    return true;
                }
            }
        }
        return false;
    }
}