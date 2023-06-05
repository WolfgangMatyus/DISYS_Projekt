package generator;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import generator.model.ReceiverPDFGeneratorMessage;

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
            String receiverGeneratorString = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println("Generator received message: " + receiverGeneratorString);

            // jsonObject = new JSONObject(invoice);

            ReceiverPDFGeneratorMessage receiverPDFGeneratorMessage = new Gson().fromJson(receiverGeneratorString, ReceiverPDFGeneratorMessage.class);

            System.out.println("invoice_id: " + receiverPDFGeneratorMessage.getInvoiceId());
            System.out.println("customer_id: " + receiverPDFGeneratorMessage.getCustomerId());




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
