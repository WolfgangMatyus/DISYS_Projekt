package generator.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import generator.model.PDFGeneratorBackendMessage;
import generator.model.ReceiverPDFGeneratorMessage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.concurrent.TimeoutException;

public class InvoicePDFService {

    private static final DecimalFormat df = new DecimalFormat("###.##");

    public static PDFGeneratorBackendMessage createPdfFromMessage(ReceiverPDFGeneratorMessage message) throws DocumentException, FileNotFoundException {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        // create title and header data
        Font title_font = FontFactory.getFont(FontFactory.COURIER, 18, BaseColor.BLACK);
        Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);

        String titleChunkString = "Invoice";
        String invoiceChunkString = "Invoice ID:     " + message.getInvoiceId().toString();
        String customerChunkString = "Customer ID:    " + message.getCustomerId();

        document.add(new Chunk(titleChunkString, title_font));
        document.add(new Paragraph("\n \n"));
        document.add(new Chunk(invoiceChunkString, font));
        document.add(new Paragraph("\n"));
        document.add(new Chunk(customerChunkString, font));
        document.add(new Paragraph("\n \n"));

        // create data-table
        PdfPTable table = new PdfPTable(2);
        table.setSpacingBefore(20f);
        float[] columnWidths = { 1.5f, 1f};
        table.setWidths(columnWidths);
        addRows(table, message);
        document.add(table);

        document.close();

        byte[] pdfContent = outputStream.toByteArray();

        PDFGeneratorBackendMessage pdfGeneratorBackendMessage = new PDFGeneratorBackendMessage();
        pdfGeneratorBackendMessage.setInvoiceId(message.getInvoiceId());
        pdfGeneratorBackendMessage.setPdfContent(pdfContent);

        return pdfGeneratorBackendMessage;
    }


    private static void addRows(PdfPTable table, ReceiverPDFGeneratorMessage message) {

        Double totalFromAllStations = 0.0;

        for(int i = 0; i < message.getStations().size(); i++) {

            Double total = 0.0;

            table.addCell("Station " + message.getStations().get(i).getId());
            table.addCell(" ");
            for(int k = 0; k < message.getStations().get(i).getCharges().size(); k++) {

                double price = message.getStations().get(i).getCharges().get(k).getPrice();
                double kwH = message.getStations().get(i).getCharges().get(k).getKwh();
                double priceByKwh = price * kwH;

                String charge = "   " + kwH + " kwH / " + df.format(price) + "€";
                table.addCell(charge);

                table.addCell(df.format(priceByKwh) + "€");

                total += priceByKwh;
            }
            table.addCell("   Total");
            table.addCell(df.format(total) + "€");

            totalFromAllStations += total;
        }
        table.addCell(" ");
        table.addCell(" ");
        table.addCell("Total from all stations");
        table.addCell(df.format(totalFromAllStations) + "€");
    }

    public static void sendPDFToBackend(String backendMessage, String EXCHANGE_NAME) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);

        try (
                com.rabbitmq.client.Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
        )
        {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            channel.basicPublish(EXCHANGE_NAME, "backend", null,
                    (backendMessage).getBytes(StandardCharsets.UTF_8));
            System.out.println(" Sent PDF to backend: '" + backendMessage);

        }
    }
}
