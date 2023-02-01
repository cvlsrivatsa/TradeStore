package org.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class StoreTest {
    @InjectMocks
    Store store = new Store();

    @Test
    public void saveTradeTest() {
        Trade t = Trade.builder()
                .tradeId("T1")
                .version(1)
                .counterPartyId("CP-1")
                .bookId("B1")
                .maturityDate(new Date())
                .build();
        store.saveTrade(t);
        Trade t1 = store.getTrade("T1", 1);
        assertEquals(1, t1.getVersion().intValue());
    }
}