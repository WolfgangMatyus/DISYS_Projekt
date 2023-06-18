import generator.model.PDFGeneratorBackendMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GeneratorTest {

    @Test
    void getPDFContent_shouldReturn(){

        // Arrange
        // mock acts like given returnValue
        var TestPDFContent = spy(PDFGeneratorBackendMessage.class);
        byte[] byteArray = {0x12, 0x34, (byte) 0xAB, (byte) 0xCD};
        when(TestPDFContent.getPdfContent()).thenReturn(byteArray);

        // Act
        var TestByteArray = TestPDFContent.getPdfContent();

        // Assert
        byte[] ControlByteArray = {0x12, 0x34, (byte) 0xAB, (byte) 0xCD};
        assertArrayEquals(ControlByteArray, TestByteArray, "The PDFContent must be {0x12, 0x34, (byte) 0xAB, (byte) 0xCD}'!");
    }

}
