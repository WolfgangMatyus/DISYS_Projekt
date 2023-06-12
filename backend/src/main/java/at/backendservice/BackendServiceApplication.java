package at.backendservice;

import at.backendservice.services.InvoiceService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class BackendServiceApplication {

    public static void main(String[] args) throws IOException, TimeoutException {
        SpringApplication.run(BackendServiceApplication.class, args);
        startBackend();
    }

    private static void startBackend() throws IOException, TimeoutException {
        InvoiceService.setPDFReceiver();
    }

}
