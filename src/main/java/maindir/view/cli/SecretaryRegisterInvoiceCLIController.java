package maindir.view.cli;

import maindir.bean.InvoiceBean;
import maindir.bean.AppointmentBean;
import maindir.controller.InvoiceController;
import maindir.exceptions.ControllerException;

import java.util.List;

public class SecretaryRegisterInvoiceCLIController {
    private final InvoiceController invoiceController;
    private final SecretaryRegisterInvoiceCLIView view;

    public SecretaryRegisterInvoiceCLIController() {
        this.invoiceController = new InvoiceController();
        this.view = new SecretaryRegisterInvoiceCLIView();
    }

    public void start() {
        // Raccogli codice fiscale
        String fiscalCode = view.collectFiscalCode();

        if (fiscalCode == null || fiscalCode.trim().isEmpty()) {
            view.displayError("Il codice fiscale non può essere vuoto!");
            return;
        }
        try{
        // Trova tutte le fatture non pagate per quel paziente
        AppointmentBean bean = new AppointmentBean();
        bean.setFiscalCode(fiscalCode);
        List<InvoiceBean> unpaidInvoices = invoiceController.getUnpaidInvoicesByFiscalCode(bean);

        if (unpaidInvoices.isEmpty()) {
            view.displayMessage("Nessuna fattura non pagata trovata per questo paziente.");
            return;
        }

        // Mostra le fatture
        view.displayUnpaidInvoices(unpaidInvoices);

        // Selezione fattura
        int selectedIndex = view.selectInvoice(unpaidInvoices.size());

        if (selectedIndex < 0 || selectedIndex >= unpaidInvoices.size()) {
            view.displayError("Selezione non valida!");
            return;
        }

        InvoiceBean selectedInvoice = unpaidInvoices.get(selectedIndex);

        // Recupera dettagli appuntamento
        AppointmentBean appointment = invoiceController.getAppointmentByInvoiceId(selectedInvoice);

        if (appointment == null) {
            view.displayError("Impossibile recuperare i dettagli dell'appuntamento!");
            return;
        }

        // Mostra dettagli
        view.displayInvoiceDetails(selectedInvoice, appointment);

        if (view.confirmPayment()) {
            boolean success = invoiceController.markAsPaid(selectedInvoice);
            if (success) {
                view.displayMessage("Pagamento regolarizzato con successo!");
            } else {
                view.displayError("Errore durante la regolarizzazione del pagamento!");
            }
        } else {
            view.displayMessage("Operazione annullata.");
        }}catch (ControllerException e){
            view.displayError(e.getMessage());
        }
    }
}
