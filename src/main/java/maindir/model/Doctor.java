package maindir.model;
import maindir.model.enums.Role;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.List;

public class Doctor extends User implements Serializable {
    private List<Service> services;
    private List<DayOfWeek> availableDays;

    public Doctor(){}
    public Doctor(Long id, Role role, String email, String fiscalCode, String name, String surname){
        super(id,role,email,fiscalCode,name, surname);
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public void setAvailableDays(List<DayOfWeek> availableDays) {
        this.availableDays = availableDays;
    }

    public List<Service> getServices() {
        return services;
    }
    public List<DayOfWeek> getAvailableDays(){return availableDays;}
}
