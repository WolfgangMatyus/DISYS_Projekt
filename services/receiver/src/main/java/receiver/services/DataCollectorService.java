package receiver.services;

import receiver.model.Invoice;

import java.util.ArrayList;

public class DataCollectorService {

    private ArrayList<Invoice> invoices;

    public ArrayList<Invoice> getInvoices() {
        return invoices;
    }

    public void addInvoice(Invoice invoice) {
        this.invoices.add(invoice);
    }
}
