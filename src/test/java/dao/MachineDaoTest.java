package dao;

import entities.Machine;
import entities.Room;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CRUD, association and HQL tests for {@link MachineDao}.
 */
class MachineDaoTest {

    private static MachineDao machineDao;
    private static RoomDao roomDao;

    @BeforeAll
    static void setUp() {
        machineDao = new MachineDao();
        roomDao = new RoomDao();
    }

    private Room newRoom() {
        Room room = new Room("R-" + System.nanoTime(), "Room for machines");
        roomDao.create(room);
        return room;
    }

    @Test
    void createShouldPersistMachineWithItsRoom() {
        Room room = newRoom();
        Machine machine = new Machine("M-" + System.nanoTime(), "Dell");
        machine.setRoom(room);

        assertTrue(machineDao.create(machine));
        assertTrue(machine.getId() > 0);

        Machine found = machineDao.getById(machine.getId());
        assertNotNull(found);
        assertEquals(room.getId(), found.getRoom().getId());
    }

    @Test
    void updateShouldModifyTheBrand() {
        Room room = newRoom();
        Machine machine = new Machine("M-" + System.nanoTime(), "HP");
        machine.setRoom(room);
        machineDao.create(machine);

        machine.setBrand("Lenovo");
        assertTrue(machineDao.update(machine));

        assertEquals("Lenovo", machineDao.getById(machine.getId()).getBrand());
    }

    @Test
    void deleteShouldRemoveTheMachine() {
        Room room = newRoom();
        Machine machine = new Machine("M-" + System.nanoTime(), "Asus");
        machine.setRoom(room);
        machineDao.create(machine);

        assertTrue(machineDao.delete(machine));
        assertNull(machineDao.getById(machine.getId()));
    }

    @Test
    void findByBrandShouldReturnOnlyMatchingMachines() {
        Room room = newRoom();
        String brand = "Brand" + System.nanoTime();

        Machine first = new Machine("M-" + System.nanoTime(), brand);
        first.setRoom(room);
        machineDao.create(first);

        Machine second = new Machine("M-" + System.nanoTime(), brand);
        second.setRoom(room);
        machineDao.create(second);

        Machine other = new Machine("M-" + System.nanoTime(), "Other" + System.nanoTime());
        other.setRoom(room);
        machineDao.create(other);

        List<Machine> machines = machineDao.findByBrand(brand);

        assertEquals(2, machines.size());
        assertTrue(machines.stream().allMatch(m -> brand.equals(m.getBrand())));
    }

    @Test
    void findByRoomShouldReturnTheMachinesOfThatRoomOnly() {
        Room firstRoom = newRoom();
        Room secondRoom = newRoom();

        Machine inFirst = new Machine("M-" + System.nanoTime(), "Acer");
        inFirst.setRoom(firstRoom);
        machineDao.create(inFirst);

        Machine inSecond = new Machine("M-" + System.nanoTime(), "Acer");
        inSecond.setRoom(secondRoom);
        machineDao.create(inSecond);

        List<Machine> machines = machineDao.findByRoom(firstRoom);

        assertEquals(1, machines.size());
        assertEquals(inFirst.getReference(), machines.get(0).getReference());
    }

    @Test
    void searchByReferenceShouldIgnoreCase() {
        Room room = newRoom();
        String reference = "SRCH" + System.nanoTime();
        Machine machine = new Machine(reference, "Dell");
        machine.setRoom(room);
        machineDao.create(machine);

        List<Machine> machines = machineDao.searchByReference(reference.toLowerCase());

        assertTrue(machines.stream().anyMatch(m -> reference.equals(m.getReference())));
    }
}
