package at.backendservice.controler;

import at.backendservice.model.Invoice;
import at.backendservice.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class BackendController {

    @Autowired
    private InvoiceService invoiceService;

    public BackendController() {
    }

    @PostMapping("/invoices/{customerID}")
    public String postInvoice(@PathVariable int customerID) {

        Invoice invoice = new Invoice("");

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

        return "test";
    }

    @GetMapping("/invoices/{customerID}")
    public String getInvoice(@PathVariable int customerID) {

        // get from file storage
        // return invoices pdf with download link and creation time
        // 404 if not available
        return "";
    }

}
