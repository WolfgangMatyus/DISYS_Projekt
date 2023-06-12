package at.backendservice.model;

import java.util.UUID;

public class PDFGeneratorBackendMessage {
    private UUID invoiceId;

    private byte[] pdfContent;


    public UUID getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }


    public byte[] getPdfContent() {
        return pdfContent;
    }

    public void setPdfContent(byte[] pdfContent) {
        this.pdfContent = pdfContent;
    }
}