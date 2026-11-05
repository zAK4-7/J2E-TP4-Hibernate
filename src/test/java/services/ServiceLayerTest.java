package services;

import entities.Machine;
import entities.Room;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the business layer delegates correctly to the DAO layer.
 */
class ServiceLayerTest {

    @Test
    void roomServiceShouldCreateAndReadRooms() {
        RoomService service = new RoomService();
        Room room = new Room("S-" + System.nanoTime(), "Service room");

        assertTrue(service.create(room));
        assertEquals("Service room", service.getById(room.getId()).getLabel());
    }

    @Test
    void machineServiceShouldCreateAndCountMachines() {
        RoomService roomService = new RoomService();
        MachineService machineService = new MachineService();

        Room room = new Room("S-" + System.nanoTime(), "Counting room");
        roomService.create(room);

        Machine machine = new Machine("M-" + System.nanoTime(), "Dell");
        machine.setRoom(room);
        machineService.create(machine);

        assertEquals(1L, roomService.countMachines(room.getId()));
    }
}
