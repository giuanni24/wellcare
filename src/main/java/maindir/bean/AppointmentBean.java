package maindir.bean;



import maindir.model.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentBean {
    private long id;
    private UserBean patient;
    private DoctorBean doctor;
    private ServiceBean service;
    private String fiscalCode;
    private LocalDate requestedDate;
    private LocalTime confirmedTime;
    private AppointmentStatus status;
    private String rejectionReason;
    private String report;

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalTime getConfirmedTime() {
        return confirmedTime;
    }

    public void setConfirmedTime(LocalTime confirmedTime) {
        this.confirmedTime = confirmedTime;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public UserBean getPatient() {
        return patient;
    }

    public void setPatient(UserBean patient) {
        this.patient = patient;
    }

    public DoctorBean getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorBean doctor) {
        this.doctor = doctor;
    }

    public ServiceBean getService() {
        return service;
    }

    public void setService(ServiceBean service) {
        this.service = service;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }
}
