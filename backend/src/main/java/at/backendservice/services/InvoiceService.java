package at.backendservice.services;

import at.backendservice.model.Invoice;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

@Service
public class InvoiceService {

    public Invoice createInvoiceByCustomer(int customerId) {
        Invoice invoice = new Invoice();
        invoice.setCustomerId(customerId);
        return invoice;
    }

    public String invoiceToString(Invoice invoice) {
        return new Gson().toJson(invoice);
    }


    public void sendToDispatcherService(String invoiceMessage, String EXCHANGE_NAME) throws IOException, TimeoutException {
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
                    (invoiceMessage).getBytes(StandardCharsets.UTF_8));
            System.out.println(" Sent invoice to dispatcher: '" + invoiceMessage);

        }
    }
}
