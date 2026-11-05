package dao;

import entities.Machine;
import entities.Room;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

/**
 * Hibernate implementation of the CRUD contract for {@link Machine}.
 */
public class MachineDao implements IDao<Machine> {

    @Override
    public boolean create(Machine machine) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(machine);
            transaction.commit();
            return true;
        } catch (Exception ex) {
            rollback(transaction);
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Machine machine) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(machine);
            transaction.commit();
            return true;
        } catch (Exception ex) {
            rollback(transaction);
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Machine machine) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(session.contains(machine) ? machine : session.merge(machine));
            transaction.commit();
            return true;
        } catch (Exception ex) {
            rollback(transaction);
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public Machine getById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Machine.class, id);
        }
    }

    @Override
    public List<Machine> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Machine", Machine.class).list();
        }
    }

    /**
     * Returns all machines of a given brand (custom HQL query with a named parameter).
     */
    public List<Machine> findByBrand(String brand) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("from Machine m where m.brand = :brand order by m.reference", Machine.class)
                    .setParameter("brand", brand)
                    .list();
        }
    }

    /**
     * Returns the machines hosted in a given room.
     */
    public List<Machine> findByRoom(Room room) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("from Machine m where m.room.id = :roomId order by m.reference", Machine.class)
                    .setParameter("roomId", room.getId())
                    .list();
        }
    }

    /**
     * Returns the machines with a reference matching the given pattern.
     */
    public List<Machine> searchByReference(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("from Machine m where upper(m.reference) like :keyword order by m.reference", Machine.class)
                    .setParameter("keyword", "%" + keyword.toUpperCase() + "%")
                    .list();
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
