package maindir.model;

import java.io.Serializable;

public class SlotReservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private ReservedSlot slot;
    private Appointment appointment;

    // Costruttori
    public SlotReservation() {
    }

    public SlotReservation(Long id, ReservedSlot slot, Appointment appointment) {
        this.id = id;
        this.slot = slot;
        this.appointment = appointment;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReservedSlot getSlot() {
        return slot;
    }

    public void setSlot(ReservedSlot slot) {
        this.slot = slot;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

}
