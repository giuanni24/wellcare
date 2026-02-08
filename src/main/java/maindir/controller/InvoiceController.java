package maindir.controller;

import maindir.bean.*;
import maindir.exceptions.ControllerException;
import maindir.exceptions.DAOException;
import maindir.model.*;
import maindir.persistance.DAOFactory;
import maindir.persistance.dao.InvoiceDAO;
import maindir.utility.BeanModelConverter;

import java.util.ArrayList;
import java.util.List;

public class InvoiceController {
    public InvoiceController(){
        //Comment
    }
    public List<InvoiceBean> getUnpaidInvoicesByFiscalCode(AppointmentBean app) throws ControllerException {
        try {
            InvoiceDAO invoiceDAO = DAOFactory.getInvoiceDAO();
            assert invoiceDAO != null;
            List<Invoice> invoices = invoiceDAO.getAll();
            List<InvoiceBean> bean = new ArrayList<>();
            for(Invoice inv: invoices){
                if(inv.getAppointment().getPatient().getFiscalCode().equals(app.getPatient().getFiscalCode()))
                    bean.add(BeanModelConverter.invoiceToBean(inv));
            }
            return bean;
        }catch(DAOException e){
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public AppointmentBean getAppointmentByInvoiceId(InvoiceBean bean) throws ControllerException {
        try {
            InvoiceDAO invoiceDAO = DAOFactory.getInvoiceDAO();
            List<Invoice> invoices = invoiceDAO.getAll();

            for (Invoice inv : invoices) {
                if (inv.getId().equals(bean.getId())) {
                    Appointment appointment = inv.getAppointment();
                    return BeanModelConverter.appointmentToBean(appointment);
                }
            }
            return null;

        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }


    public boolean markAsPaid(InvoiceBean bean) throws ControllerException {
        Invoice inv = BeanModelConverter.invoiceToModel(bean);
        inv.markAsPaid();
        try {
            InvoiceDAO invoiceDAO = DAOFactory.getInvoiceDAO();
            assert invoiceDAO != null;
            invoiceDAO.update(inv);
        }catch(DAOException e){
            throw new ControllerException(e.getMessage(), e);
        }
        return true;
    }

}
