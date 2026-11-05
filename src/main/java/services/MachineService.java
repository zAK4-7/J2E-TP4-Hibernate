package services;

import dao.IDao;
import dao.MachineDao;
import entities.Machine;
import entities.Room;
import java.util.List;

/**
 * Business layer for machines.
 */
public class MachineService implements IDao<Machine> {

    private final MachineDao dao = new MachineDao();

    @Override
    public boolean create(Machine machine) {
        return dao.create(machine);
    }

    @Override
    public boolean update(Machine machine) {
        return dao.update(machine);
    }

    @Override
    public boolean delete(Machine machine) {
        return dao.delete(machine);
    }

    @Override
    public Machine getById(int id) {
        return dao.getById(id);
    }

    @Override
    public List<Machine> getAll() {
        return dao.getAll();
    }

    public List<Machine> findByBrand(String brand) {
        return dao.findByBrand(brand);
    }

    public List<Machine> findByRoom(Room room) {
        return dao.findByRoom(room);
    }

    public List<Machine> searchByReference(String keyword) {
        return dao.searchByReference(keyword);
    }
}
