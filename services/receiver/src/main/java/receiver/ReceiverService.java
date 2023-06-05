package receiver;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import receiver.model.Charge;
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



            // jsonObject = new JSONObject(invoice);

            String[] parts = receiverMessageString.split("::");
            String sender = parts[0];
            String content = parts[1];

            if(sender.equals("FROM_COLLECTOR")) {
                System.out.println("Receiver received message from collector: " + receiverMessageString);
            } else if(sender.equals("FROM_DISPATCHER")){
                System.out.println("Receiver received message from dispatcher: " + receiverMessageString);
            } else System.out.println("sender not found!");

            System.out.println("sender: " + sender);
            System.out.println("content: " + content);

            ReceiverPDFGeneratorMessage receiverPDFGeneratorMessage = new ReceiverPDFGeneratorMessage();



            if(sender.equals("FROM_COLLECTOR")){
                // System.out.println("sender is from collector");

                CollectorReceiverMessage collectorReceiverMessage = new Gson().fromJson(content, CollectorReceiverMessage.class);

                // receiverPDFGeneratorMessage.setInvoiceId(collectorReceiverMessage.getInvoiceId());
                // receiverPDFGeneratorMessage.setCustomerId(collectorReceiverMessage.getCustomerId());

                // IN ARRAY ENTRY hinzufügen


                for(int i = 0; i < DataCollectorService.getReceiverPDFGeneratorMessages().size(); i++) {

                    if(DataCollectorService.getReceiverPDFGeneratorMessages().get(i).getInvoiceId().equals(collectorReceiverMessage.getInvoiceId())){
                        for(int k = 0; k < DataCollectorService.getReceiverPDFGeneratorMessages().get(i).getStations().size(); k++) {

                            if(DataCollectorService.getReceiverPDFGeneratorMessages().get(i).getStations().get(k).getId() == collectorReceiverMessage.getStationId()) {

                                DataCollectorService.getReceiverPDFGeneratorMessages().get(i).getStations().get(k).setCharges(collectorReceiverMessage.getCharges());
                            }

                        }
                    }
                }

                // System.out.println("from collector received");

            } else if(sender.equals("FROM_DISPATCHER")){
                // System.out.println("sender is from_dispatcher");



                DispatcherReceiverMessage dispatcherReceiverMessage = new Gson().fromJson(content, DispatcherReceiverMessage.class);
                receiverPDFGeneratorMessage.setInvoiceId(dispatcherReceiverMessage.getInvoiceId());
                receiverPDFGeneratorMessage.setCustomerId(dispatcherReceiverMessage.getCustomerId());
                receiverPDFGeneratorMessage.setStations(dispatcherReceiverMessage.getAvailableStations());

                DataCollectorService.addReceiverPDFGeneratorMessage(receiverPDFGeneratorMessage);

                // System.out.println("from dispatcher received");


                // ARRAY SPEICHERN

            } else{
                System.out.println("Sender not found!");
            }

            // SEND TO PDF_Generator
            try {
                DataCollectorService.sendDataToPDFGenerator(receiverPDFGeneratorMessage.toJSON(), EXCHANGE_NAME);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }




            //int customerId = jsonObject.getAsString("customerId");

//
//            System.out.println("Service1 received message: " + content);
//            System.out.println("Service1 service chain: " + serviceChain);

//            // remove active service from chain
//            String[] serviceChainArray = serviceChain.split(",");
//            String nextService = "";
//
//            if (serviceChainArray.length > 1) {
//                System.out.println("next: " + serviceChainArray[1]);
//                nextService = serviceChainArray[1];
//                serviceChain = serviceChain.substring(serviceChain.indexOf(",") + 1);
//            } else {
//                // Kein weiteres Service in der Kette
//                nextService = "receiver";
//                serviceChain = "";
//            }
//
//            String reversed_content = reverseString(content);

//            try {
//                channel.basicPublish(EXCHANGE_NAME, nextService, null,
//                        (serviceChain + ":" + reversed_content).getBytes(StandardCharsets.UTF_8));
//                System.out.println("Service1 sent message to " + nextService);
//            }
//            catch (IOException e) {
//                throw new RuntimeException(e);
//            }

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
