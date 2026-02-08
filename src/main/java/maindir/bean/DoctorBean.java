package maindir.bean;

import java.time.DayOfWeek;
import java.util.List;

public class DoctorBean extends UserBean{
    private List<ServiceBean> services;
    private List<DayOfWeek> availableDays;
    public List<ServiceBean> getServices() {
        return services;
    }

    public void setServices(List<ServiceBean> services) {
        this.services = services;
    }

    public List<DayOfWeek> getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(List<DayOfWeek> availableDays) {
        this.availableDays = availableDays;
    }
}

