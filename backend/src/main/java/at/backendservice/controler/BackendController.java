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

        // ABLAUF:
        // rabbitMQ send to dispatcher

        // dispatcher
        // receive: spring send_to: collector, receiver

        // collector
        // receive: dispatcher send_to: receiver

        // receiver
        // receive: dispatcher, collector send_to: pdfgenerator

        // pdfgenerator -> file-storage

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
