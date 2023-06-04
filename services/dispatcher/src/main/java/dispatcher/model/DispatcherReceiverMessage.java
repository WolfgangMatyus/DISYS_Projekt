package dispatcher.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.UUID;

public class DispatcherReceiverMessage {
    private UUID invoiceId;
    private int customerId;

    private ArrayList<Station> availableStations = new ArrayList<Station>();

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

    public ArrayList<Station> getAvailableStations() {
        return availableStations;
    }

    public void setAvailableStations(ArrayList<Station> availableStations) {
        this.availableStations = availableStations;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
