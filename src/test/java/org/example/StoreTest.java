package org.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class StoreTest {
    @InjectMocks
    Store store = new Store();

    @Test
    public void saveTradeTestBasic() {
        Trade t = Trade.builder()
                .tradeId("T1")
                .version(1)
                .counterPartyId("CP-1")
                .bookId("B1")
                .maturityDate(new Date())
                .build();
        assertTrue(store.saveTrade(t));
        Trade t1 = store.getTrade("T1", 1);
        assertEquals(1, t1.getVersion().intValue());
    }

    @Test
    public void saveTradeTestDuplicateTradeSameVersion() {
        Trade t = Trade.builder()
                .tradeId("T1")
                .version(1)
                .counterPartyId("CP-2")
                .bookId("B1")
                .maturityDate(new Date())
                .build();
        assertTrue(store.saveTrade(t));
        Trade t1 = Trade.builder()
                .tradeId("T1")
                .version(1)
                .counterPartyId("CP-3")
                .bookId("B1")
                .maturityDate(new Date())
                .build();
        assertTrue(store.saveTrade(t1));
        List<Trade> tradeList = store.getTrades("T1");
        assertEquals(1, tradeList.size());
        assertEquals(1, tradeList.get(0).getVersion().intValue());
        assertEquals("CP-3", tradeList.get(0).getCounterPartyId());
    }

    @Test
    public void saveTradeTestDuplicateTradeNewVersion() {
        Trade t = Trade.builder()
                .tradeId("T1")
                .version(1)
                .counterPartyId("CP-2")
                .bookId("B1")
                .maturityDate(new Date())
                .build();
        assertTrue(store.saveTrade(t));
        Trade t1 = Trade.builder()
                .tradeId("T1")
                .version(2)
                .counterPartyId("CP-3")
                .bookId("B1")
                .maturityDate(new Date())
                .build();
        assertTrue(store.saveTrade(t1));
        List<Trade> tradeList = store.getTrades("T1");
        assertEquals(2, tradeList.size());
    }

    @Test(expected = RuntimeException.class)
    public void saveTradeTestDuplicateTradeOldVersion() {
        Trade t = Trade.builder()
                .tradeId("T1")
                .version(1)
                .counterPartyId("CP-2")
                .bookId("B1")
                .maturityDate(new Date())
                .build();
        assertTrue(store.saveTrade(t));
        Trade t1 = Trade.builder()
                .tradeId("T1")
                .version(0)
                .counterPartyId("CP-3")
                .bookId("B1")
                .maturityDate(new Date())
                .build();
        store.saveTrade(t1);
    }

    @Test
    public void saveTradeTestDuplicateTradeOldMaturityDate() {
        Date nowDate = new Date();
        Trade t = Trade.builder()
                .tradeId("T1")
                .version(1)
                .counterPartyId("CP-2")
                .bookId("B1")
                .maturityDate(nowDate)
                .build();
        assertTrue(store.saveTrade(t));
        Trade t1 = Trade.builder()
                .tradeId("T1")
                .version(2)
                .counterPartyId("CP-3")
                .bookId("B1")
                .maturityDate(new Date(nowDate.getTime() - 60 * 1000))
                .build();
        assertFalse(store.saveTrade(t1));
        List<Trade> tradeList = store.getTrades("T1");
        assertEquals(1, tradeList.size());
        assertEquals(1, tradeList.get(0).getVersion().intValue());
    }

}