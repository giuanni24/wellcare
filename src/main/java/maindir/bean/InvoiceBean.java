package maindir.bean;

import maindir.model.enums.InvoiceStatus;

public class InvoiceBean {
    private Long id;
    private Double amount;
    private InvoiceStatus paymentStatus;
    private AppointmentBean appointmentBean;

    public AppointmentBean getAppointmentBean() {
        return appointmentBean;
    }

    public void setAppointmentBean(AppointmentBean appointmentBean) {
        this.appointmentBean = appointmentBean;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setPaymentStatus(InvoiceStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
