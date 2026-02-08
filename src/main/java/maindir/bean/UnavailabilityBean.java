package maindir.bean;

import java.time.LocalDate;

public class UnavailabilityBean {
    private DoctorBean doctor;
    private LocalDate startDate;
    private LocalDate endDate;

    public UnavailabilityBean() {
    }

    public UnavailabilityBean(DoctorBean doctor, LocalDate startDate, LocalDate endDate) {
        this.doctor = doctor;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public DoctorBean getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorBean doctor) {
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
}
