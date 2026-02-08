package maindir.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import maindir.bean.AppointmentBean;
import maindir.bean.InvoiceBean;
import maindir.controller.InvoiceController;
import maindir.exceptions.ControllerException;

import java.util.List;

public class SecretaryRegisterInvoiceGUIController {

    @FXML private TextField txtFiscalCode;
    @FXML private VBox invoiceContainer;
    @FXML private Label lblStatus;
    @FXML private Button btnHome;

    // Popup
    @FXML private VBox popupConfirm;
    @FXML private Label lblConfirmDetails;

    private InvoiceController invoiceController;
    private InvoiceBean selectedInvoice;

    @FXML
    public void initialize() {
        this.invoiceController = new InvoiceController();
        hidePopup();
        if (lblStatus != null) {
            lblStatus.setVisible(false);
            lblStatus.setManaged(false);
        }
    }

    @FXML
    private void handleSearch() {
        String cf = txtFiscalCode.getText().trim();
        invoiceContainer.getChildren().clear();
        hideStatus();
        try{
        if (cf.isEmpty()) {
            showStatus("Inserire un Codice Fiscale", true);
            return;
        }
        AppointmentBean bean = new AppointmentBean();
        bean.setFiscalCode(cf);
        List<InvoiceBean> unpaid = invoiceController.getUnpaidInvoicesByFiscalCode(bean);

        if (unpaid == null || unpaid.isEmpty()) {
            showStatus("Nessuna fattura non pagata trovata.", false);
            return;
        }

        for (InvoiceBean inv : unpaid) {
            invoiceContainer.getChildren().add(createInvoiceRow(inv));
        }}catch(ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private HBox createInvoiceRow(InvoiceBean inv) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10; -fx-border-color: #DDD; -fx-border-width: 0 0 1 0;");

        Label lblInfo = new Label("Fattura #" + inv.getId() + " - Importo: €" + inv.getAmount());
        lblInfo.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnPay = new Button("PAGA");
        btnPay.setStyle("-fx-background-color: #FF8C42; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 10;");
        btnPay.setOnAction(e -> openConfirmation(inv));

        row.getChildren().addAll(lblInfo, spacer, btnPay);
        return row;
    }

    private void openConfirmation(InvoiceBean inv) {
        this.selectedInvoice = inv;
        lblConfirmDetails.setText("Confermi il pagamento della fattura #" + inv.getId() + " di €" + inv.getAmount() + "?");
        showPopup();
    }

    @FXML
    private void executePayment() {
        if (selectedInvoice == null) {
            showStatus("Selezionare una fattura.", true);
            return;
        }
        try{
        boolean success = invoiceController.markAsPaid(selectedInvoice);
        if (success) {
            showStatus("Fattura #" + selectedInvoice.getId() + " regolarizzata.", false);
            hidePopup();
            handleSearch();
        } else {
            showStatus("Errore nel salvataggio del pagamento.", true);
        }}catch(ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void closePopup() {
        hidePopup();
    }

    private void showStatus(String msg, boolean isError) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: " + (isError ? "red" : "green") + ";");
        lblStatus.setManaged(true);
        lblStatus.setVisible(true);
    }

    private void hideStatus() {
        if (lblStatus == null) return;
        lblStatus.setVisible(false);
        lblStatus.setManaged(false);
    }

    private void showPopup() {
        if (popupConfirm == null) return;
        popupConfirm.setManaged(true);
        popupConfirm.setVisible(true);
    }

    private void hidePopup() {
        if (popupConfirm == null) return;
        popupConfirm.setVisible(false);
        popupConfirm.setManaged(false);
    }

    @FXML
    private void handleHome(ActionEvent event) {
        Navigator.getInstance().goTo(event, "SecretaryHomepage.fxml");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        handleHome(event);
    }

    @FXML
    private void handleMouseEnter(javafx.scene.input.MouseEvent e) {
        Node n = (Node) e.getSource();
        n.setScaleX(1.1);
        n.setScaleY(1.1);
    }

    @FXML
    private void handleMouseExit(javafx.scene.input.MouseEvent e) {
        Node n = (Node) e.getSource();
        n.setScaleX(1.0);
        n.setScaleY(1.0);
    }
}
