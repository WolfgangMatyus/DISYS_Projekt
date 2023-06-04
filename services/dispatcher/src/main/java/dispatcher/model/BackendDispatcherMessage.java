package dispatcher.model;

import java.util.UUID;

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


}
