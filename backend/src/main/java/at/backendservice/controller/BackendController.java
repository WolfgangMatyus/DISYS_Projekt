package at.backendservice.controller;

import at.backendservice.model.BackendDispatcherMessage;
import at.backendservice.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

        invoiceService.sendToDispatcherService(dispatcherMessage.toJSON(), "createInvoice");

        return invoiceService.sendToDispatcherService(dispatcherMessage.toJSON(), "createInvoice");
    }

        // get from file storage
        @GetMapping("/invoices/{invoiceID}")
        public String getInvoice(@PathVariable UUID invoiceID) {

            // get from file storage
            return "waiting for file";
        }

        public ResponseEntity<Resource> getInvoice(@PathVariable String invoiceID) {
            // Pfade oder Speicherort der Dateien im File Storage
            String storagePath = "Pfad/zum/File/Storage/";
            String filename = invoiceID + ".pdf";

            // Vollständiger Pfad zur PDF-Datei
            String fullPath = storagePath + filename;

            // Versuche, die Datei zu laden
            try {
                Resource fileResource = new FileSystemResource(fullPath);

                if (fileResource.exists()) {
                    // Wenn die Datei existiert, gebe sie als Response zurück
                    // return invoices pdf with download link and creation time

                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
                    headers.add(HttpHeaders.LOCATION, fullPath);

                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(fileResource);

                    // 404 if not available -> in Projekt_UI InvoiceController

                } else {
                    // Wenn die Datei nicht gefunden wurde, gebe einen Fehler zurück
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            } catch (Exception e) {
                // Bei Fehlern während des Ladens gebe einen Fehler zurück
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

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
