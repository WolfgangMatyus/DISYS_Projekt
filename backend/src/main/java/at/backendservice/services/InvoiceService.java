package at.backendservice.services;

import at.backendservice.model.BackendDispatcherMessage;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import at.backendservice.model.PDFGeneratorBackendMessage;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Service
public class InvoiceService {

    private static final String EXCHANGE_NAME = "createInvoice";
    private static final String ROUTING_KEY = "backend";

    public BackendDispatcherMessage createInvoiceByCustomer(int customerId) {
        BackendDispatcherMessage invoice = new BackendDispatcherMessage();
        invoice.setCustomerId(customerId);
        return invoice;
    }


    public String sendToDispatcherService(String dispatcherMessage, String EXCHANGE_NAME) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);

        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
        )
        {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            channel.basicPublish(EXCHANGE_NAME, "dispatcher", null,
                    (dispatcherMessage).getBytes(StandardCharsets.UTF_8));
            System.out.println(" Sent invoice to dispatcher: '" + dispatcherMessage);

        }
        return dispatcherMessage;
    }

    public static void setPDFReceiver() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String queueName = channel.queueDeclare().getQueue();

        System.out.println("Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String pdfGeneratorBackendMessageJSON = new String(delivery.getBody(), StandardCharsets.UTF_8);

            PDFGeneratorBackendMessage pdfGeneratorBackendMessage = new Gson().fromJson(pdfGeneratorBackendMessageJSON, PDFGeneratorBackendMessage.class);

            String invoicePath = getInvoicesDirectoryPath() + "/" + pdfGeneratorBackendMessage.getInvoiceId() + ".pdf";

            try (FileOutputStream fileOutputStream = new FileOutputStream(invoicePath)) {
                fileOutputStream.write(pdfGeneratorBackendMessage.getPdfContent());
                System.out.println("PDF-Dokument gespeichert: " + invoicePath);
            } catch (Exception e) {
                e.printStackTrace();
            }


        };
        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

    }

    public static String getInvoicesDirectoryPath() {
        File classesDirectory = new File(InvoiceService.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File invoicesDirectory = new File(classesDirectory.getParent() + "/invoices");
        invoicesDirectory.mkdirs();

        return invoicesDirectory.getPath();
    }
}
