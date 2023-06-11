package at.backendservice.services;

import at.backendservice.model.BackendDispatcherMessage;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Service
public class InvoiceService {

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
}
