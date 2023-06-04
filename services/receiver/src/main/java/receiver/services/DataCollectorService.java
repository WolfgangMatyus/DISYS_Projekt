package receiver.services;


import receiver.model.ReceiverPDFGeneratorMessage;

import java.util.ArrayList;

public class DataCollectorService {

    private static ArrayList<ReceiverPDFGeneratorMessage> receiverPDFGeneratorMessages;

    public static ArrayList<ReceiverPDFGeneratorMessage> getReceiverPDFGeneratorMessages() {
        return receiverPDFGeneratorMessages;
    }

    public static void addReceiverPDFGeneratorMessage(ReceiverPDFGeneratorMessage receiverPDFGeneratorMessage) {
        receiverPDFGeneratorMessages.add(receiverPDFGeneratorMessage);
    }


}
