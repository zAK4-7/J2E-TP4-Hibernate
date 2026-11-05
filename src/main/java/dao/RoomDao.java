package dao;

import entities.Room;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

/**
 * Hibernate implementation of the CRUD contract for {@link Room}.
 */
public class RoomDao implements IDao<Room> {

    @Override
    public boolean create(Room room) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(room);
            transaction.commit();
            return true;
        } catch (Exception ex) {
            rollback(transaction);
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Room room) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(room);
            transaction.commit();
            return true;
        } catch (Exception ex) {
            rollback(transaction);
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Room room) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(session.contains(room) ? room : session.merge(room));
            transaction.commit();
            return true;
        } catch (Exception ex) {
            rollback(transaction);
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public Room getById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Room.class, id);
        }
    }

    @Override
    public List<Room> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Room", Room.class).list();
        }
    }

    /**
     * Returns the rooms whose code starts with the given prefix.
     * Demonstrates a typed HQL query with a named parameter.
     */
    public List<Room> findByCodePrefix(String prefix) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("from Room r where r.code like :prefix order by r.code", Room.class)
                    .setParameter("prefix", prefix + "%")
                    .list();
        }
    }

    /**
     * Returns the rooms that host at least one machine, using a join in HQL.
     */
    public List<Room> findRoomsWithMachines() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("select distinct m.room from Machine m", Room.class)
                    .list();
        }
    }

    /**
     * Counts the machines hosted in a room with an aggregate HQL query.
     */
    public long countMachines(int roomId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("select count(m) from Machine m where m.room.id = :roomId", Long.class)
                    .setParameter("roomId", roomId)
                    .getSingleResult();
        }
    }

    private void rollback(Transaction transaction) {
        try {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } catch (Exception ignored) {
            // The transaction may already be rolled back by Hibernate after a failed flush
        }
    }
}
