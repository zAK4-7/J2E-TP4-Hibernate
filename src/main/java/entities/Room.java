package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * A room that groups several machines.
 * This is the "one" side of the room/machine association.
 */
@Entity
@Table(name = "rooms")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "label", nullable = false, length = 100)
    private String label;

    // A machine cannot exist without its room, hence the cascade and the orphan removal
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Machine> machines = new ArrayList<>();

    public Room() {
    }

    public Room(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * Adds a machine and keeps both sides of the association in sync.
     */
    public void addMachine(Machine machine) {
        machines.add(machine);
        machine.setRoom(this);
    }

    public void removeMachine(Machine machine) {
        machines.remove(machine);
        machine.setRoom(null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Machine> getMachines() {
        return machines;
    }

    public void setMachines(List<Machine> machines) {
        this.machines = machines;
    }

    @Override
    public String toString() {
        return "Room{id=" + id + ", code='" + code + "', label='" + label + "'}";
    }
}
