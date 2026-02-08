package maindir.persistance;

import maindir.persistance.dao.*;
import maindir.persistance.file.*;
import maindir.persistance.jdbc.*;
import maindir.persistance.memory.*;

public class DAOFactory {

    public enum PersistenceMode {
        MEMORY,  // Demo
        FILE,    // Full - FileSystem
        JDBC     // Full - Database
    }
    private static PersistenceMode mode;

    public static void setMode(PersistenceMode newMode){
        mode = newMode;
    }

    public static UserDAO getUserDAO() {
        return switch(mode) {
            case MEMORY -> MemoryUserDAO.getInstance();
            case FILE -> FSUserDAO.getInstance();
            case JDBC -> JDBCUserDAO.getInstance();
            default -> null;
        };
    }

    public static DoctorUnavailabilityDAO getDoctorUnavailabilityDAO() {
        return switch(mode) {
            case MEMORY -> MemoryDoctorUnavailabilityDAO.getInstance();
            case FILE -> FSDoctorUnavailabilityDAO.getInstance();
            case JDBC -> JDBCDoctorUnavailabilityDAO.getInstance();
            default -> null;
        };
    }

    public static ReservedSlotDAO getReservedSlotDAO() {
        return switch(mode) {
            case MEMORY -> MemoryReservedSlotDAO.getInstance();
            case FILE -> FSReservedSlotDAO.getInstance();
            case JDBC -> JDBCReservedSlotDAO.getInstance();
            default -> null;
        };
    }

    public static SlotReservationDAO getSlotReservationDAO() {
        return switch(mode) {
            case MEMORY -> MemorySlotReservationDAO.getInstance();
            case FILE -> FSSlotReservationDAO.getInstance();
            case JDBC -> JDBCSlotReservationDAO.getInstance();
            default -> null;
        };
    }


    public static AppointmentDAO getAppointmentDAO() {
        return switch(mode) {
            case MEMORY -> MemoryAppointmentDAO.getInstance();
            case FILE -> FSAppointmentDAO.getInstance(); // FILE mode non esiste per Appointment
            case JDBC -> JDBCAppointmentDAO.getInstance();
        };
    }

    public static InvoiceDAO getInvoiceDAO() {
        return switch(mode) {
            case MEMORY -> MemoryInvoiceDAO.getInstance();
            case FILE -> FSInvoiceDAO.getInstance(); // FILE mode non esiste per Invoice
            case JDBC -> JDBCInvoiceDAO.getInstance();
        };
    }

    public static NotificationDAO getNotificationDAO(){
        return switch (mode){
            case MEMORY -> MemoryNotificationDAO.getInstance();
            case FILE -> FSNotificationDAO.getInstance(); // FILE mode non esiste per Invoice
            case JDBC -> JDBCNotificationDAO.getInstance();
        };
    }

    public static ServiceDAO getServiceDAO(){
        return switch (mode){
            case MEMORY -> MemoryServiceDAO.getInstance();
            case FILE -> FSServiceDAO.getInstance(); // FILE mode non esiste per Invoice
            case JDBC -> JDBCServiceDAO.getInstance();
        };
    }

}
