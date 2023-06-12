package generator;

import com.google.gson.Gson;
import com.itextpdf.text.*;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import generator.model.PDFGeneratorBackendMessage;
import generator.model.ReceiverPDFGeneratorMessage;
import generator.services.InvoicePDFService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class GeneratorService {
    private static final String EXCHANGE_NAME = "createInvoice";
    private static final String ROUTING_KEY = "generator";

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
            String receiverPDFGeneratorMessageString = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println("Generator received message from receiver: " + receiverPDFGeneratorMessageString);

            ReceiverPDFGeneratorMessage receiverPDFGeneratorMessage = new Gson().fromJson(receiverPDFGeneratorMessageString, ReceiverPDFGeneratorMessage.class);

            System.out.println("Try to create PDF: " + receiverPDFGeneratorMessage.getInvoiceId() + ".pdf");
            try {
                PDFGeneratorBackendMessage pdfGeneratorBackendMessage = InvoicePDFService.createPdfFromMessage(receiverPDFGeneratorMessage);

                // send pdf back to backend
                try {
                    InvoicePDFService.sendPDFToBackend(pdfGeneratorBackendMessage.toJSON(), EXCHANGE_NAME);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }

            } catch (DocumentException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        };
        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }
}
