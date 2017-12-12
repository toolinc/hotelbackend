package com.toolsoft.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import javax.inject.Inject;

/**
 * Data Access Object to provide auth capabilities using a RDBMS.
 */
public final class LoginDaoSql implements LoginDao {

  /**
   * Used to determine if necessary tables are provided.
   */
  private static final String TABLES_SQL =
      "SHOW TABLES LIKE 'users';";
  /**
   * Used to create necessary tables for this example.
   */
  private static final String CREATE_SQL =
      "CREATE TABLE users (" +
          "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
          "username VARCHAR(32) NOT NULL UNIQUE, " +
          "password CHAR(64) NOT NULL, " +
          "usersalt CHAR(32) NOT NULL);";
  /**
   * Used to insert a new user into the database.
   */
  private static final String REGISTER_SQL =
      "INSERT INTO users (username, password, usersalt) " +
          "VALUES (?, ?, ?);";
  /**
   * Used to determine if a username already exists.
   */
  private static final String USER_SQL =
      "SELECT username FROM users WHERE username = ?";
  /**
   * Used to retrieve the salt associated with a specific user.
   */
  private static final String SALT_SQL =
      "SELECT usersalt FROM users WHERE username = ?";
  /**
   * Used to authenticate a user.
   */
  private static final String AUTH_SQL =
      "SELECT username FROM users " +
          "WHERE username = ? AND password = ?";
  /**
   * Used to remove a user from the database.
   */
  private static final String DELETE_SQL =
      "DELETE FROM users WHERE username = ?";
  private final Random random = new Random(System.currentTimeMillis());
  private final Connection connection;

  @Inject
  LoginDaoSql(Connection connection) {
    this.connection = checkNotNull(connection);
  }

  /**
   * Registers a new user, placing the username, password hash, and salt into the database if the
   * username does not already exist.
   *
   * @param newuser - username of new user
   * @param newpass - password of new user
   */
  @Override
  public void registerUser(String newuser, String newpass) {
    // make sure we have non-null and non-emtpy values for login
    if (isBlank(newuser) || isBlank(newpass)) {
      throw new IllegalStateException("");
    }
    try {
      // if okay so far, try to insert new user
      if (duplicateUser(newuser)) {
        throw new IllegalStateException("");
      }
      if (registerNewUser(newuser, newpass) != 1) {
        throw new IllegalStateException("");
      }
    } catch (SQLException ex) {
      throw new IllegalStateException("Unable to register the user.", ex);
    }
  }

  /**
   * Checks to see if a String is null or empty.
   *
   * @param text - String to check
   * @return true if non-null and non-empty
   */
  private static boolean isBlank(String text) {
    return (text == null) || text.trim().isEmpty();
  }

  /**
   * Tests if a user already exists in the database. Requires an active database connection.
   *
   * @param user - username to check
   */
  private boolean duplicateUser(String user) throws SQLException {
    PreparedStatement statement = connection.prepareStatement(USER_SQL);
    statement.setString(1, user);
    ResultSet results = statement.executeQuery();
    return results.next() ? true : false;
  }

  /**
   * Registers a new user, placing the username, password hash, and salt into the database if the
   * username does not already exist.
   *
   * @param newuser - username of new user
   * @param newpass - password of new user
   */
  private int registerNewUser(String newuser, String newpass) throws SQLException {
    byte[] saltBytes = new byte[16];
    random.nextBytes(saltBytes);
    String usersalt = encodeHex(saltBytes, 32);
    String passhash = getHash(newpass, usersalt);
    PreparedStatement statement = connection.prepareStatement(REGISTER_SQL);
    statement.setString(1, newuser);
    statement.setString(2, passhash);
    statement.setString(3, usersalt);
    return statement.executeUpdate();
  }

  /**
   * Checks if the provided username and password match what is stored in the database. Must
   * retrieve the salt and hash the password to do the comparison.
   *
   * @param username - username to authenticate
   * @param password - password to authenticate
   */
  @Override
  public void authenticateUser(String username, String password) {
    try (
        PreparedStatement statement = connection.prepareStatement(AUTH_SQL);
    ) {
      String usersalt = getSalt(username);
      String passhash = getHash(password, usersalt);
      statement.setString(1, username);
      statement.setString(2, passhash);
      ResultSet results = statement.executeQuery();
      if (!results.next()) {
        throw new IllegalStateException("Unable to find the user.");
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Unable to authenticate the user.", e);
    }
  }

  /**
   * Gets the salt for a specific user.
   *
   * @param user - which user to retrieve salt for
   * @return salt for the specified user or null if user does not exist
   * @throws SQLException if any issues with database connection
   */
  private String getSalt(String user) throws SQLException {
    String salt = null;
    try (
        PreparedStatement statement = connection.prepareStatement(SALT_SQL);
    ) {
      statement.setString(1, user);
      ResultSet results = statement.executeQuery();
      if (results.next()) {
        salt = results.getString("usersalt");
      }
    }
    return salt;
  }

  /**
   * Calculates the hash of a password and salt using SHA-256.
   *
   * @param password - password to hash
   * @param salt - salt associated with user
   * @return hashed password
   */
  private static String getHash(String password, String salt) {
    String salted = salt + password;
    String hashed = salted;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(salted.getBytes());
      hashed = encodeHex(md.digest(), 64);
    } catch (Exception ex) {
      throw new IllegalStateException("Hashing failed.", ex);
    }
    return hashed;
  }

  /**
   * Returns the hex encoding of a byte array.
   *
   * @param bytes - byte array to encode
   * @param length - desired length of encoding
   * @return hex encoded byte array
   */
  private static String encodeHex(byte[] bytes, int length) {
    BigInteger bigint = new BigInteger(1, bytes);
    String hex = String.format("%0" + length + "X", bigint);
    assert hex.length() == length;
    return hex;
  }
}
