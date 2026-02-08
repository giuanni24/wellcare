package maindir.controller;

import maindir.bean.NotificationBean;
import maindir.bean.UserBean;
import maindir.exceptions.ControllerException;
import maindir.exceptions.DAOException;
import maindir.model.*;
import maindir.model.enums.Role;
import maindir.persistance.dao.NotificationDAO;
import maindir.persistance.DAOFactory;
import maindir.utility.BeanModelConverter;

import java.util.ArrayList;
import java.util.List;

public class NotificationController {
    private final NotificationDAO notificationDAO;

    public NotificationController() {
        this.notificationDAO = DAOFactory.getNotificationDAO();
    }

    public List<NotificationBean> getUnreadNotificationsForPatient(UserBean patientBean) throws ControllerException {
        try {
            List<Notification> allNotifications = notificationDAO.getAll();
            List<NotificationBean> unreadNotifications = new ArrayList<>();

            for (Notification notification : allNotifications) {
                if (!notification.isRead() &&
                        notification.getTargetRole() == Role.PATIENT &&
                        notification.getAppointment() != null &&
                        notification.getAppointment().getPatient().getId().equals(patientBean.getId())) {

                    unreadNotifications.add(BeanModelConverter.notificationToBean(notification));

                    notification.markAsRead();
                    notificationDAO.update(notification);
                }
            }

            return unreadNotifications;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public List<NotificationBean> getUnreadNotificationsForSecretary() throws ControllerException {
        try {
            List<Notification> allNotifications = notificationDAO.getAll();
            List<NotificationBean> unreadNotifications = new ArrayList<>();

            for (Notification notification : allNotifications) {
                if (!notification.isRead() && notification.getTargetRole() == Role.SECRETARY) {

                    unreadNotifications.add(BeanModelConverter.notificationToBean(notification));

                    notification.markAsRead();
                    notificationDAO.update(notification);
                }
            }

            return unreadNotifications;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }




}
