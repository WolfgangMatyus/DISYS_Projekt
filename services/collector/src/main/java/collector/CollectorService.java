package collector;

import collector.model.Charge;
import collector.model.CollectorReceiverMessage;
import collector.services.MessageService;
import collector.services.StationService;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import collector.model.DispatcherCollectorMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class CollectorService {
    private static final String EXCHANGE_NAME = "createInvoice";
    private static final String ROUTING_KEY = "collector";

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
            String collectorMessageJson = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println("Collector received message: " + collectorMessageJson);

            // jsonObject = new JSONObject(invoice);

            DispatcherCollectorMessage dispatcherCollectorMessage = new Gson().fromJson(collectorMessageJson, DispatcherCollectorMessage.class);


            ArrayList<Charge> charges = StationService.getChargesForCustomerFromDB(
                    "jdbc:postgresql://" + dispatcherCollectorMessage.getStationURL() + "/stationdb",
                    "postgres",
                    "postgres",
                    dispatcherCollectorMessage.getCustomerId(),
                    dispatcherCollectorMessage.getInvoiceId());


            System.out.println("invoiceId: " + dispatcherCollectorMessage.getInvoiceId());
            System.out.println("customerId: " + dispatcherCollectorMessage.getCustomerId());
            System.out.println("stationURL: " + dispatcherCollectorMessage.getStationURL());

            CollectorReceiverMessage collectorReceiverMessage = new CollectorReceiverMessage();

            collectorReceiverMessage.setStationId(dispatcherCollectorMessage.getStationId());
            collectorReceiverMessage.setInvoiceId(dispatcherCollectorMessage.getInvoiceId());
            collectorReceiverMessage.setCustomerId(dispatcherCollectorMessage.getCustomerId());
            collectorReceiverMessage.setCharges(charges);

            try {
                MessageService.sendMessageToReceiver("FROM_COLLECTOR::" +collectorReceiverMessage.toJSON(), EXCHANGE_NAME);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }


        };
        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }

    public static String reverseString(String revers) {
        StringBuilder sb=new StringBuilder(revers);
        sb.reverse();
        return sb.toString();
    }
}
