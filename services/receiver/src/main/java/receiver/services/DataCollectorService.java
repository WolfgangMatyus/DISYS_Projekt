package receiver.services;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import receiver.model.ReceiverPDFGeneratorMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class DataCollectorService {

    private static ArrayList<ReceiverPDFGeneratorMessage> receiverPDFGeneratorMessages = new ArrayList<>();

    public static ArrayList<ReceiverPDFGeneratorMessage> getReceiverPDFGeneratorMessages() {
        return receiverPDFGeneratorMessages;
    }

    public static void addReceiverPDFGeneratorMessage(ReceiverPDFGeneratorMessage receiverPDFGeneratorMessage) {
        receiverPDFGeneratorMessages.add(receiverPDFGeneratorMessage);
    }

    public static void sendDataToPDFGenerator(String receiverPDFGeneratorMessage, String EXCHANGE_NAME) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);

        try (
                com.rabbitmq.client.Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
        )
        {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            channel.basicPublish(EXCHANGE_NAME, "generator", null,
                    (receiverPDFGeneratorMessage).getBytes(StandardCharsets.UTF_8));
            System.out.println(" Sent data to PDFGenerator: '" + receiverPDFGeneratorMessage);

        }
    }


}
