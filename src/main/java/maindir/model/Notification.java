package maindir.model;

import maindir.model.enums.Role;
import java.io.Serializable;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Appointment appointment;  // ← ID dell'appuntamento correlato
    private String message;
    private Role targetRole;     // ← PATIENT o SECRETARY
    private boolean isRead;

    public Notification() {
        this.isRead = false;
    }

    public Notification(Appointment appointment, String message, Role targetRole) {
        this();
        this.appointment = appointment;
        this.message = message;
        this.targetRole = targetRole;
    }

    public Notification(Long id, Appointment appointment, String message, Role targetRole, boolean isRead) {
        this.id = id;
        this.appointment = appointment;
        this.message = message;
        this.targetRole = targetRole;
        this.isRead = isRead;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public void setAppointment(Appointment appointment) {this.appointment = appointment; }

    public Appointment getAppointment() {
        return appointment;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Role getTargetRole() { return targetRole; }
    public void setTargetRole(Role targetRole) { this.targetRole = targetRole; }

    public boolean isRead() { return isRead; }
    public void markAsRead(boolean read) { isRead = read; }
}
