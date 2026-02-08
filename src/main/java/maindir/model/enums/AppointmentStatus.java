package maindir.model.enums;

public enum AppointmentStatus {
    PENDING,        // Richiesta inviata, aspetta segreteria
    CONFIRMED,      // Segreteria ha confermato
    REJECTED,       // Segreteria ha rifiutato
    ARRIVED,        // Paziente arrivato (check-in)
    RESCHEDULING,   // In attesa di riprogrammazione
    CANCELED,       // Paziente ha annullato
    COMPLETED       // Visita completata
}
