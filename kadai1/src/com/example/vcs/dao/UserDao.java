package com.example.vcs.dao;

import com.example.vcs.model.User;
import java.sql.SQLException;
import java.util.Optional;

public interface UserDao {
    User insert(User user) throws SQLException;
    Optional<User> findById(int id) throws SQLException;
    Optional<User> findByUsername(String username) throws SQLException;
}