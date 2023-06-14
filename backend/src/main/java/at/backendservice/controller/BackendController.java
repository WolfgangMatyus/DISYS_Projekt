package at.backendservice.controller;

import at.backendservice.model.BackendDispatcherMessage;
import at.backendservice.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/v1")
public class BackendController {

    @Autowired
    private InvoiceService invoiceService;

    public BackendController() {
    }

    @PostMapping("/invoices/{customerID}")
    public String postInvoice(@PathVariable int customerID) throws IOException, TimeoutException {

        BackendDispatcherMessage dispatcherMessage = invoiceService.createInvoiceByCustomer(customerID);

        return invoiceService.sendToDispatcherService(dispatcherMessage.toJSON(), "createInvoice");
    }

    @GetMapping("/invoices/{invoiceID}")
    public String getInvoice(@PathVariable UUID invoiceID) throws IOException {
        String storagePath = InvoiceService.getInvoicesDirectoryPath();
        String filename = invoiceID + ".pdf";
        String filePath = storagePath + "/" + filename;

        // Assuming the file is stored locally, read it into a byte array
        byte[] fileData = readFromFile(filePath);

        // Convert the byte array to Base64 encoded string
        String base64Data = Base64.getEncoder().encodeToString(fileData);

        return base64Data;
    }

    private byte[] readFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] data = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        }

        return data;
    }


    // ----------------------------------------------------------------------------------------
    // ABLAUF:
    // ----------------------------------------------------------------------------------------

    // start here with rabbitMQ
    // send_to: dispatcher (customer_id)

    // dispatcher
    // receive: spring (customer_id)
    // get all host:port from stations
    // for every entry -> send_to: collector [host:port, customer_id, invoice_id]
    // send_to: receiver ("data gathering job started", list station_ids, invoice_id, customer_id)


    // collector
    // receive: dispatcher (host:port, customer_id, invoice_id)
    // get kwh from host:port for customer || NICE TO HAVE: where invoice is not set
    // NICE TO HAVE: set invoice id to entries of the stationdb
    // send_to: receiver (sum(kwh), station_id, customer_id, invoice_id)


    // receiver
    // receive: dispatcher ("data gathering job started", list station_ids, invoice_id, customer_id)
    // receive: collector (kwh, station_id, customer_id, invoice_id)
    // add invoice to list: [{customer_id: "", invoice_id: "", collector:[{station_id: 1, kwh_sum: 0}, {station_id: 2, kwh_sum: 0}, {station_id: 3, kwh_sum: 0}]}, {...}]
    // all specific data received -> send_to: pdfgenerator (invoice)
    // delete invoice from list


    // pdfgenerator
    // receive: receiver (invoice)
    // create pdf
    // save pdf to file-storage

    // ----------------------------------------------------------------------------------------

    // Messages:
    // chain: rot (dispatcher), grün (collector), blau (receiver), gelb (pdf generator)
    // chain2: rot (dispatcher), lila (receiver), gelb (pdfgenerator)

}
