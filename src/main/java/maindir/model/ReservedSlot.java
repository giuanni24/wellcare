package maindir.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservedSlot implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Doctor doctor;
    private LocalDate date;
    private LocalTime time;
    private DoctorUnavailability unavailabilityPeriod;  // ← Oggetto completo, non ID
    private boolean isBooked;



    // Costruttori
    public ReservedSlot() {
    }

    public ReservedSlot(Long id, Doctor doctor, LocalDate date, LocalTime time, DoctorUnavailability unavailabilityPeriod, boolean isBooked) {
        this.id = id;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.unavailabilityPeriod = unavailabilityPeriod;
        this.isBooked = isBooked;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }



    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public DoctorUnavailability getUnavailabilityPeriod() {
        return unavailabilityPeriod;
    }

    public void setUnavailabilityPeriod(DoctorUnavailability unavailabilityPeriod) {
        this.unavailabilityPeriod = unavailabilityPeriod;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void markAsBooked() {
        isBooked = true;
    }
    public void unmarkAsBooked(){
        isBooked = false;
    }

}
