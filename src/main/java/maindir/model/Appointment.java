package maindir.model;

import maindir.exceptions.DAOException;
import maindir.model.enums.AppointmentStatus;
import maindir.observer.NotificationObserver;
import maindir.observer.Subject;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment extends Subject implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Long id;
    protected User patient;
    protected Doctor doctor;
    protected Service service;
    protected LocalDate requestedDate;
    protected LocalTime confirmedTime;
    protected AppointmentStatus status;
    protected String rejectionReason;
    protected String report;  // ← AGGIUNGI QUESTO



    public void notifyExternal() throws DAOException {
        notifyObservers(this);
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Appointment(User patient, Doctor doctor, Service service, LocalDate requestedDate) {
        this.patient = patient;
        this.doctor = doctor;
        this.service = service;
        this.requestedDate = requestedDate;
        this.attach(NotificationObserver.getInstance());
        this.status = AppointmentStatus.PENDING;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public Appointment(Long id, User patient, Doctor doctor, Service service, LocalDate requestedDate, LocalTime confirmedTime, AppointmentStatus status) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.service = service;
        this.requestedDate = requestedDate;
        this.confirmedTime = confirmedTime;
        this.status = status;
    }

    public void setStatusConfirmed(LocalTime confirmedTime) throws DAOException {
        this.status = AppointmentStatus.CONFIRMED;
        this.confirmedTime = confirmedTime;
        notifyObservers(this);
    }

    public void setStatusRejected(String rejectionReason) throws DAOException {
        this.status = AppointmentStatus.REJECTED;
        this.rejectionReason = rejectionReason;
        notifyObservers(this);
    }
    public void setStatusRescheduling() throws DAOException {
        this.status = AppointmentStatus.RESCHEDULING;
        notifyObservers(this);
    }
    public void setArrived(){
        this.status = AppointmentStatus.ARRIVED;
    }
    public void setCanceled(){
        this.status = AppointmentStatus.CANCELED;
    }
    public boolean isPending() {
        return AppointmentStatus.PENDING.equals(status);
    }

    public boolean isConfirmed() {
        return AppointmentStatus.CONFIRMED.equals(status);
    }

    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }

    public boolean isRejected() {
        return AppointmentStatus.REJECTED.equals(status);
    }
    public boolean isRescheduled(){
        return AppointmentStatus.RESCHEDULING.equals(status);
    }
    public User getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Service getService() {
        return service;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public LocalTime getConfirmedTime() {
        return confirmedTime;
    }

    public void setConfirmedTime(LocalTime confirmedTime) {
        this.confirmedTime = confirmedTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



}
