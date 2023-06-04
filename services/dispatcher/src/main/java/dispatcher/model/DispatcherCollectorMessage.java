package dispatcher.model;

import com.google.gson.Gson;

import java.util.UUID;

public class DispatcherCollectorMessage {

    private UUID invoiceId;
    private int customerId;
    private Integer stationId;
    private String stationURL;

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

    public String getStationURL() {
        return stationURL;
    }

    public void setStationURL(String stationURL) {
        this.stationURL = stationURL;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
