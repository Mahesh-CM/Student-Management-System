package com.sms.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Common CRUD contract implemented by every DAO (abstraction). */
public interface CrudDao<T> {

    /** Inserts the entity and returns its generated id. */
    int add(T entity) throws SQLException;

    boolean update(T entity) throws SQLException;

    boolean delete(int id) throws SQLException;

    Optional<T> findById(int id) throws SQLException;

    List<T> findAll() throws SQLException;
}
