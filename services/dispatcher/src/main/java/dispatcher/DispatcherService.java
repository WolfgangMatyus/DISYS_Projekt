package dispatcher;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import dispatcher.model.DispatcherCollectorMessage;
import dispatcher.model.BackendDispatcherMessage;
import dispatcher.model.DispatcherReceiverMessage;
import dispatcher.model.Station;
import dispatcher.services.CollectorService;
import dispatcher.services.ReceiverService;
import dispatcher.services.StationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class DispatcherService {
    private static final String EXCHANGE_NAME = "createInvoice";
    private static final String ROUTING_KEY = "dispatcher";

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
            String dispatcherMessageJSON = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println("Dispatcher received message: " + dispatcherMessageJSON);


            BackendDispatcherMessage backendDispatcherMessage = new Gson().fromJson(dispatcherMessageJSON, BackendDispatcherMessage.class);

            System.out.println("invoiceId: " + backendDispatcherMessage.getInvoiceId());
            System.out.println("customerId: " + backendDispatcherMessage.getCustomerId());

            ArrayList<Station> stations = StationService.getStationsFromDB("jdbc:postgresql://localhost:30002/stationdb", "postgres", "postgres");

            DispatcherReceiverMessage dispatcherReceiverMessage = new DispatcherReceiverMessage();
            dispatcherReceiverMessage.setInvoiceId(backendDispatcherMessage.getInvoiceId());
            dispatcherReceiverMessage.setCustomerId(backendDispatcherMessage.getCustomerId());
            dispatcherReceiverMessage.setAvailableStations(stations);

            // SEND TO RECEIVER
            try {
                ReceiverService.sendStationsToReceiver("FROM_DISPATCHER::" + dispatcherReceiverMessage.toJSON(), EXCHANGE_NAME);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }

            // SEND TO COLLECTOR'S
            for(int i = 0; i < dispatcherReceiverMessage.getAvailableStations().size(); i++) {

                DispatcherCollectorMessage dispatcherCollectorMessage = new DispatcherCollectorMessage();
                dispatcherCollectorMessage.setInvoiceId(backendDispatcherMessage.getInvoiceId());
                dispatcherCollectorMessage.setCustomerId(backendDispatcherMessage.getCustomerId());
                dispatcherCollectorMessage.setStationId(dispatcherReceiverMessage.getAvailableStations().get(i).getId());
                dispatcherCollectorMessage.setStationURL(dispatcherReceiverMessage.getAvailableStations().get(i).getUrl());


                try {
                    CollectorService.sendStationToCollector(dispatcherCollectorMessage.toJSON(), EXCHANGE_NAME);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }

        };
        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }
}
