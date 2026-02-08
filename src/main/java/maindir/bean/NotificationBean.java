package maindir.bean;
import maindir.model.enums.Role;

public class NotificationBean {
    private AppointmentBean appointment;
    private String message;
    private Role targetRole;
    private boolean isRead;

    public AppointmentBean getAppointment() {
        return appointment;
    }

    public void setAppointment(AppointmentBean appointment) {
        this.appointment = appointment;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Role getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(Role targetRole) {
        this.targetRole = targetRole;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
