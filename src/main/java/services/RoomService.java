package services;

import dao.IDao;
import dao.RoomDao;
import entities.Room;
import java.util.List;

/**
 * Business layer for rooms.
 * It delegates persistence to the DAO and stays the only entry point used by the
 * presentation layer.
 */
public class RoomService implements IDao<Room> {

    private final RoomDao dao = new RoomDao();

    @Override
    public boolean create(Room room) {
        return dao.create(room);
    }

    @Override
    public boolean update(Room room) {
        return dao.update(room);
    }

    @Override
    public boolean delete(Room room) {
        return dao.delete(room);
    }

    @Override
    public Room getById(int id) {
        return dao.getById(id);
    }

    @Override
    public List<Room> getAll() {
        return dao.getAll();
    }

    public List<Room> findByCodePrefix(String prefix) {
        return dao.findByCodePrefix(prefix);
    }

    public List<Room> findRoomsWithMachines() {
        return dao.findRoomsWithMachines();
    }

    public long countMachines(int roomId) {
        return dao.countMachines(roomId);
    }
}
