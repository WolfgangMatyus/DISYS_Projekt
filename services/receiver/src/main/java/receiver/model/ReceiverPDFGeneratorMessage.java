package receiver.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.UUID;

public class ReceiverPDFGeneratorMessage {
    private UUID invoiceId;
    private int customerId;
    private ArrayList<CollectorReceiverMessage> stationcharges = new ArrayList<CollectorReceiverMessage>();


    public UUID getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public ArrayList<CollectorReceiverMessage> getStationcharges() {
        return stationcharges;
    }

    public void setStationcharges(ArrayList<CollectorReceiverMessage> stationcharges) {
        this.stationcharges = stationcharges;
    }
}
