package test;

import entities.Machine;
import entities.Room;
import java.util.List;
import services.MachineService;
import services.RoomService;

/**
 * Manual smoke test: persists a room with two machines and reads the data back.
 * Run it with "mvn exec:java" once the MySQL database is available.
 */
public class MainTest {

    public static void main(String[] args) {
        RoomService roomService = new RoomService();
        MachineService machineService = new MachineService();

        Room room = new Room("IT-01", "Computer Lab");
        roomService.create(room);

        Machine dell = new Machine("M-101", "Dell");
        dell.setRoom(room);
        machineService.create(dell);

        Machine hp = new Machine("M-102", "HP");
        hp.setRoom(room);
        machineService.create(hp);

        System.out.println("=== Persisted data ===");

        System.out.println("Rooms:");
        for (Room persistedRoom : roomService.getAll()) {
            System.out.println("  Room{id=" + persistedRoom.getId()
                    + ", code='" + persistedRoom.getCode()
                    + "', label='" + persistedRoom.getLabel() + "'}");
        }

        // The room association is LAZY, so the machines are read through a query
        // executed while the Hibernate session is still open.
        System.out.println("Machines:");
        for (Room persistedRoom : roomService.getAll()) {
            for (Machine machine : machineService.findByRoom(persistedRoom)) {
                System.out.println("  Machine{id=" + machine.getId()
                        + ", reference='" + machine.getReference()
                        + "', brand='" + machine.getBrand() + "'}"
                        + " -> room " + persistedRoom.getCode());
            }
        }

        List<Machine> dellMachines = machineService.findByBrand("Dell");
        System.out.println("Machines of brand Dell: " + dellMachines.size());
        for (Machine machine : dellMachines) {
            System.out.println("  " + machine.getReference() + " / " + machine.getBrand());
        }

        System.out.println("Machines in room IT-01: " + roomService.countMachines(room.getId()));
        System.out.println("Data inserted successfully.");
    }
}
