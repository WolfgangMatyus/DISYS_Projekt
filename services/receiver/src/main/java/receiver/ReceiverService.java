package receiver;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import receiver.model.CollectorReceiverMessage;
import receiver.model.DispatcherReceiverMessage;
import receiver.model.ReceiverPDFGeneratorMessage;
import receiver.services.DataCollectorService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class ReceiverService {
    private static final String EXCHANGE_NAME = "createInvoice";
    private static final String ROUTING_KEY = "receiver";

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
            String receiverMessageString = new String(delivery.getBody(), StandardCharsets.UTF_8);

            String[] parts = receiverMessageString.split("::");
            String sender = parts[0];
            String content = parts[1];

            if(sender.equals("FROM_DISPATCHER")){
                System.out.println("Receiver received message from dispatcher: " + receiverMessageString);

                ReceiverPDFGeneratorMessage receiverPDFGeneratorMessage = new ReceiverPDFGeneratorMessage();

                DispatcherReceiverMessage dispatcherReceiverMessage = new Gson().fromJson(content, DispatcherReceiverMessage.class);
                receiverPDFGeneratorMessage.setInvoiceId(dispatcherReceiverMessage.getInvoiceId());
                receiverPDFGeneratorMessage.setCustomerId(dispatcherReceiverMessage.getCustomerId());
                receiverPDFGeneratorMessage.setStations(dispatcherReceiverMessage.getAvailableStations());

                DataCollectorService.addReceiverPDFGeneratorMessage(receiverPDFGeneratorMessage);

            } else if(sender.equals("FROM_COLLECTOR")){
                System.out.println("Receiver received message from collector: " + receiverMessageString);

                CollectorReceiverMessage collectorReceiverMessage = new Gson().fromJson(content, CollectorReceiverMessage.class);

                // add station data to collection
                for(int i = 0; i < DataCollectorService.getReceiverPDFGeneratorMessages().size(); i++) {

                    if(DataCollectorService.getReceiverPDFGeneratorMessages().get(i).getInvoiceId().equals(collectorReceiverMessage.getInvoiceId())){
                        for(int k = 0; k < DataCollectorService.getReceiverPDFGeneratorMessages().get(i).getStations().size(); k++) {

                            if(DataCollectorService.getReceiverPDFGeneratorMessages().get(i).getStations().get(k).getId() == collectorReceiverMessage.getStationId()) {

                                DataCollectorService.getReceiverPDFGeneratorMessages().get(i).getStations().get(k).setCharges(collectorReceiverMessage.getCharges());
                                DataCollectorService.getReceiverPDFGeneratorMessages().get(i).getStations().get(k).setReceived(Boolean.TRUE);
                            }

                        }
                    }
                }
                // check if all charges are received
                if(DataCollectorService.isAllCollectorDataCompleted(collectorReceiverMessage.getInvoiceId())) {
                    try {
                        ReceiverPDFGeneratorMessage message = DataCollectorService.getReceiverPDFGeneratorMessageByInvoiceId(collectorReceiverMessage.getInvoiceId());
                        DataCollectorService.sendDataToPDFGenerator(message.toJSON(), EXCHANGE_NAME);
                    } catch (TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                }

            } else System.out.println("Sender not found!");
        };
        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }
}
