package maindir.model;

import maindir.model.enums.InvoiceStatus;

import java.io.Serializable;

public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Appointment appointment;
    private Double amount;
    private InvoiceStatus paymentStatus;


    public Invoice(Long id, Appointment appointment, Double amount, InvoiceStatus paymentStatus) {
        this.id = id;
        this.appointment = appointment;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }

    public Invoice(Appointment appointment, Double amount) {
        this.appointment = appointment;
        this.amount = amount;
        this.paymentStatus = InvoiceStatus.UNPAID;
    }


    public void markAsPaid() {
        this.paymentStatus = InvoiceStatus.PAID;
    }

    public Long getId() {
        return id;
    }

    public void setPaymentStatus(InvoiceStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public InvoiceStatus getPaymentStatus() {
        return paymentStatus;
    }
}
