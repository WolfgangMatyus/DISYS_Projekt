package dispatcher.services;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import dispatcher.model.DispatcherCollectorMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class CollectorService {

    public static void sendStationToCollector(String station, String EXCHANGE_NAME) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);

        try (
                com.rabbitmq.client.Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
        )
        {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            channel.basicPublish(EXCHANGE_NAME, "collector", null,
                    (station).getBytes(StandardCharsets.UTF_8));
            System.out.println(" Sent station to collector: '" + station);

        }
    }

    public String collectorMessageToString(DispatcherCollectorMessage collectorMessage) {
        return new Gson().toJson(collectorMessage);
    }
}
