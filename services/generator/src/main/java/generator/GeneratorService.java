package generator;

import com.google.gson.Gson;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import generator.model.ReceiverPDFGeneratorMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

public class GeneratorService {
    private static final String EXCHANGE_NAME = "createInvoice";
    private static final String ROUTING_KEY = "generator";
    private static final DecimalFormat df = new DecimalFormat("###.##");

    public static void main(String[] args) throws Exception {
        startService();
    }

    public static void startService() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String queueName = channel.queueDeclare().getQueue();

        System.out.println("Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String receiverGeneratorString = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println("Generator received message: " + receiverGeneratorString);

            ReceiverPDFGeneratorMessage receiverPDFGeneratorMessage = new Gson().fromJson(receiverGeneratorString, ReceiverPDFGeneratorMessage.class);

            try {
                Document document = createPdfFromMessage(receiverPDFGeneratorMessage);

                document.close();
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }

        };
        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }

    public static Document createPdfFromMessage(ReceiverPDFGeneratorMessage message) throws DocumentException, FileNotFoundException {
        Document document = new Document();

        File classesDirectory = new File(GeneratorService.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File invoicesDirectory = new File(classesDirectory.getParent() + "/invoices");
        invoicesDirectory.mkdirs();

        String invoicePath = invoicesDirectory.getPath() + "/" + message.getInvoiceId() + ".pdf";

        PdfWriter.getInstance(document, new FileOutputStream(invoicePath));

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
        return document;
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
}
