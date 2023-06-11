package collector.model;

import java.util.UUID;

public class Charge {
    private int id;
    private int customerId;
    private double kwh;
    private UUID invoiceId;
    private double price;

    public Charge(int id, int customerId, double kwh, UUID invoiceId, double price) {
        this.id = id;
        this.customerId = customerId;
        this.kwh = kwh;
        this.invoiceId = invoiceId;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getKwh() {
        return kwh;
    }

    public void setKwh(double kwh) {
        this.kwh = kwh;
    }

    public UUID getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }
}
