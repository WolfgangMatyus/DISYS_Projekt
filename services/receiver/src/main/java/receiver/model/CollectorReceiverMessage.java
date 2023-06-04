package receiver.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.UUID;

public class CollectorReceiverMessage {

    private UUID invoiceId;
    private int customerId;
    private Integer stationId;
    private ArrayList<Charge> charges;

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

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public ArrayList<Charge> getCharges() {
        return charges;
    }

    public void setCharges(ArrayList<Charge> charges) {
        this.charges = charges;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
