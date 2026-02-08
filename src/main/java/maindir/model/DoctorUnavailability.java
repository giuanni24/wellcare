package maindir.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DoctorUnavailability implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Doctor doctor;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;

    // Costruttori
    public DoctorUnavailability() {
    }

    public DoctorUnavailability(Long id, Doctor doctor, LocalDate startDate, LocalDate endDate, LocalDateTime createdAt) {
        this.id = id;
        this.doctor = doctor;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    // Metodo per verificare se il periodo è scaduto (oltre 24 ore)
    public boolean isExpired() {
        return createdAt.isBefore(LocalDateTime.now().minusHours(24));
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
