package maindir.model;

import java.io.Serializable;

public class Service implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Long id;
    protected String name;
    protected double basePrice;


    public Service() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public Service(String name, double basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    public Service(Long id, String name, double basePrice) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
