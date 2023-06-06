package receiver.model;

public class Charge {
    private int id;
    private int customerId;
    private double kwh;
    private String invoiceId;

    public Charge(int id, int customerId, double kwh, String invoiceId) {
        this.id = id;
        this.customerId = customerId;
        this.kwh = kwh;
        this.invoiceId = invoiceId;
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

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

}
