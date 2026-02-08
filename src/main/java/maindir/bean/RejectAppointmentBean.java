package maindir.bean;

public class RejectAppointmentBean {
    private AppointmentBean appointment;
    private String rejectionReason;

    public AppointmentBean getAppointment() {
        return appointment;
    }

    public void setAppointment(AppointmentBean appointment) {
        this.appointment = appointment;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
