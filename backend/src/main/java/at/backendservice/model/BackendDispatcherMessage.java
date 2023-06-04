package at.backendservice.model;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class BackendDispatcherMessage {
    private UUID invoiceId;
    private int customerId;

    public BackendDispatcherMessage() {
        this.invoiceId = UUID.randomUUID();;
    }

    public UUID getInvoiceId() {
        return invoiceId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }


    public String toJSON() {
        return new Gson().toJson(this);
    }

}
