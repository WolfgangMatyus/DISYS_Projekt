package receiver.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.UUID;

public class ReceiverPDFGeneratorMessage {
    private UUID invoiceId;
    private int customerId;
    private ArrayList<Station> stations = new ArrayList<Station>();


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

    public ArrayList<Station> getStations() {
        return stations;
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }

    public void addStation(Station station) {
        this.stations.add(station);
    }
}
