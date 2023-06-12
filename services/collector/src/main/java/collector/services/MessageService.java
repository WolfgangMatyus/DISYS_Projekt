package collector.services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class MessageService {

    public static void sendMessageToReceiver(String collectorMessage, String EXCHANGE_NAME) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);

        try (
                com.rabbitmq.client.Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
        )
        {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            channel.basicPublish(EXCHANGE_NAME, "receiver", null,
                    (collectorMessage).getBytes(StandardCharsets.UTF_8));
            System.out.println(" Sent charge data to receiver: '" + collectorMessage);

        }
    }

}
