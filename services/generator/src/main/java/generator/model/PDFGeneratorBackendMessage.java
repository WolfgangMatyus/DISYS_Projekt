package generator.model;

import com.google.gson.Gson;

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

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
