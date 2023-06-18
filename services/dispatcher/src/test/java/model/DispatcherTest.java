package model;

import dispatcher.model.Station;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DispatcherTest {
    @Test
    void getStationUrl_ShouldReturn_localhost30015() {

        //Arrange
        var TestStation = new Station();
        TestStation.setId(4);
        TestStation.setUrl("localhost:30015");

        //Act
        var TestURL = TestStation.getUrl();

        //Assert
        assertEquals("localhost:30014", TestURL, "The URL of TestStation must be 'localhost:30015'!");

    }

}
