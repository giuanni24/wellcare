package maindir.controller;

import maindir.bean.AppointmentBean;
import maindir.bean.UserBean;
import maindir.exceptions.ControllerException;
import maindir.exceptions.DAOException;
import maindir.model.Appointment;
import maindir.model.User;
import maindir.persistance.DAOFactory;
import maindir.persistance.dao.AppointmentDAO;
import maindir.persistance.dao.UserDAO;
import maindir.utility.BeanModelConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AcceptController {

    public UserBean findByCF(String cf) throws ControllerException {
        UserBean patient = null;
        try{
        UserDAO userDAO = DAOFactory.getUserDAO();
        assert  userDAO != null;


        List<User> user = null;
        user = userDAO.getAll();
        for(User u: user){
            if(u.getFiscalCode().equalsIgnoreCase(cf))
                patient = BeanModelConverter.userToBean(u);
        }}catch(DAOException e){
            throw new ControllerException(e.getMessage(), e);
        }
        return patient;
    }

    public List<AppointmentBean> findTodayAppointmentsByPatient(UserBean user) throws ControllerException{
        List<AppointmentBean> userAppointments = new ArrayList<>();
        try {
            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            assert appointmentDAO != null;

            List<Appointment> allAppointments = appointmentDAO.getAll();

            for (Appointment a : allAppointments) {
                if (user.getId().equals(BeanModelConverter.appointmentToBean(a).getPatient().getId()) && a.getRequestedDate().equals(LocalDate.now()))
                    userAppointments.add(BeanModelConverter.appointmentToBean(a));
            }
            if (userAppointments.isEmpty())
                throw new ControllerException("Non ci sono prenotazioni per oggi per " + user.getFiscalCode());
        }catch (DAOException e){
            throw new ControllerException(e.getMessage(), e);
        }
        return userAppointments;
    }

    public void acceptPatient(AppointmentBean bean) throws ControllerException {
        try {
            Appointment appointment = BeanModelConverter.appointmentToModel(bean);

            appointment.setArrived();

            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            appointmentDAO.update(appointment);
        }catch(DAOException e){
            throw new ControllerException(e.getMessage(), e);
        }
    }

}
