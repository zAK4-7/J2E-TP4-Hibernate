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
        for (Room r : roomService.getAll()) {
            System.out.println("  " + r);
        }

        System.out.println("Machines:");
        for (Machine m : machineService.getAll()) {
            System.out.println("  " + m + " -> room " + m.getRoom().getCode());
        }

        System.out.println("Machines of brand Dell: " + machineService.findByBrand("Dell"));
        System.out.println("Machines in room " + room.getCode() + ": " + roomService.countMachines(room.getId()));
        System.out.println("Data inserted successfully.");
    }
}
