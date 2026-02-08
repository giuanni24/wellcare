package maindir.utility;

import maindir.bean.*;
import maindir.model.*;

import java.util.ArrayList;
import java.util.List;

public class BeanModelConverter {
    private BeanModelConverter(){}
    public static Appointment appointmentToModel(AppointmentBean bean) {
        User patient = userToModel(bean.getPatient());
        Doctor doctor = doctorToModel(bean.getDoctor());
        Service service = serviceToModel(bean.getService());

        return new Appointment(
                bean.getId(),
                patient,
                doctor,
                service,
                bean.getRequestedDate(),
                bean.getConfirmedTime(),
                bean.getStatus()
        );

    }

    public static AppointmentBean appointmentToBean(Appointment appointment) {
        AppointmentBean bean = new AppointmentBean();
        bean.setId(appointment.getId());
        bean.setPatient(userToBean(appointment.getPatient()));
        bean.setDoctor(doctorToBean(appointment.getDoctor()));
        bean.setService(serviceToBean(appointment.getService()));
        bean.setRequestedDate(appointment.getRequestedDate());
        bean.setStatus(appointment.getStatus());
        return bean;
    }

    public static UserBean userToBean(User user) {
        UserBean bean = new UserBean();
        bean.setId(user.getId());
        bean.setRole(user.getRole());
        bean.setEmail(user.getEmail());
        bean.setFiscalCode(user.getFiscalCode());
        bean.setName(user.getName());
        bean.setSurname(user.getSurname());
        return bean;
    }

    public static User userToModel(UserBean bean) {
        return new User(
                bean.getId(),
                bean.getRole(),
                bean.getEmail(),
                bean.getFiscalCode(),
                bean.getName(),
                bean.getSurname()
        );
    }

    public static Doctor doctorToModel(DoctorBean bean) {
        List<Service> services = new ArrayList<>();
        for (ServiceBean serviceBean : bean.getServices()) {
            services.add(serviceToModel(serviceBean));
        }

        Doctor doctor = new Doctor(
                bean.getId(),
                bean.getRole(),
                bean.getEmail(),
                bean.getFiscalCode(),
                bean.getName(),
                bean.getSurname()
        );
        doctor.setServices(services);
        doctor.setAvailableDays(bean.getAvailableDays());
        return doctor;
    }

    public static DoctorBean doctorToBean(Doctor doctor) {
        DoctorBean bean = new DoctorBean();
        bean.setId(doctor.getId());
        bean.setRole(doctor.getRole());
        bean.setEmail(doctor.getEmail());
        bean.setFiscalCode(doctor.getFiscalCode());
        bean.setName(doctor.getName());
        bean.setSurname(doctor.getSurname());
        bean.setAvailableDays(doctor.getAvailableDays());

        List<ServiceBean> serviceBeans = new ArrayList<>();
        for (Service service : doctor.getServices()) {
            serviceBeans.add(serviceToBean(service));
        }
        bean.setServices(serviceBeans);

        return bean;
    }

    public static Service serviceToModel(ServiceBean bean) {
        return new Service(
                bean.getId(),
                bean.getName(),
                bean.getBasePrice()
        );
    }

    public static ServiceBean serviceToBean(Service service) {
        ServiceBean bean = new ServiceBean();
        bean.setId(service.getId());
        bean.setName(service.getName());
        bean.setBasePrice(service.getBasePrice());
        return bean;
    }

    public static InvoiceBean invoiceToBean(Invoice invoice) {
        InvoiceBean bean = new InvoiceBean();
        bean.setId(invoice.getId());
        bean.setAmount(invoice.getAmount());
        bean.setPaymentStatus(invoice.getPaymentStatus());
        bean.setAppointmentBean(appointmentToBean(invoice.getAppointment()));
        return bean;
    }

    public static Invoice invoiceToModel(InvoiceBean bean){
        return new Invoice(bean.getId(), appointmentToModel(bean.getAppointmentBean()), bean.getAmount(), bean.getPaymentStatus());
    }

    public static NotificationBean notificationToBean(Notification notification) {
        NotificationBean bean = new NotificationBean();
        bean.setMessage(notification.getMessage());
        bean.setTargetRole(notification.getTargetRole());
        bean.setRead(notification.isRead());

        if (notification.getAppointment() != null) {
            bean.setAppointment(appointmentToBean(notification.getAppointment()));
        }

        return bean;
    }
}
