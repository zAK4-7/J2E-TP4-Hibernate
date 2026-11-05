package dao;

import java.util.List;

/**
 * Generic persistence contract shared by every entity of the application.
 * Keeping it generic avoids duplicating the CRUD signatures in each DAO.
 *
 * @param <T> persisted entity type
 */
public interface IDao<T> {

    boolean create(T entity);

    boolean update(T entity);

    boolean delete(T entity);

    T getById(int id);

    List<T> getAll();
}
