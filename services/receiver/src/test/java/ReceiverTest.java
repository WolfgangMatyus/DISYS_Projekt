import org.junit.jupiter.api.Test;
import receiver.model.ReceiverPDFGeneratorMessage;

import java.util.UUID;

import static com.google.gson.internal.bind.TypeAdapters.UUID;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReceiverTest {

    @Test
    void getInvoiceID_shouldReturn(){

        // Arrange
        // mock acts like given returnValue
        var TestGeneratorMessage = mock(ReceiverPDFGeneratorMessage.class);
        when(TestGeneratorMessage.getInvoiceId()).thenReturn(java.util.UUID.fromString("ea43deeb-14a5-4625-9c44-4c691e4a924c"));

        // act
        var invoiceID = TestGeneratorMessage.getInvoiceId();

        // Assert
        assertEquals(java.util.UUID.fromString("ea43deeb-14a5-4625-9c44-4c691e4a924c"), invoiceID, "The invoiceID must be '123F6'!");
    }

}
