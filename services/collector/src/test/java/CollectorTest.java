import collector.model.Charge;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectorTest {

    @Test
    void CustomerID_shouldReturn2(){

        // Arrange
        var TestCharge = new Charge(99, 2, 35, null, 25);

        // Act
        var TestCustomerID = TestCharge.getCustomerId();

        // Assert
        assertEquals(2, TestCustomerID, "The CustomerID should be 2!");
    }

}
