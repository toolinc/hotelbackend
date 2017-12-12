package com.toolsoft.dao;

/**
 * Data Access Object to auth users.
 */
public interface LoginDao {

  /**
   * Checks if the provided username and password match what is stored in the database. Must
   * retrieve the salt and hash the password to do the comparison.
   *
   * @param username - username to authenticate
   * @param password - password to authenticate
   */
  void authenticateUser(String username, String password);

  /**
   * Registers a new user, placing the username, password hash, and salt into the database if the
   * username does not already exist.
   *
   * @param newuser - username of new user
   * @param newpass - password of new user
   */
  void registerUser(String newuser, String newpass);
}
