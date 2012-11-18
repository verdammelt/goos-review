package auctionsniper;

import org.junit.Test;

import static auctionsniper.Column.*;
import static junit.framework.Assert.assertEquals;


public class ColumnTest {
    private final SniperSnapshot snapshot =
            new SniperSnapshot("item", 555, 666, SniperState.WINNING);

    @Test
    public void canGetFields() {
        assertEquals("item", ITEM_IDENTIFIER.valueIn(snapshot));
        assertEquals(555, LAST_PRICE.valueIn(snapshot));
        assertEquals(666, LAST_BID.valueIn(snapshot));
        assertEquals("winning", SNIPER_STATE.valueIn(snapshot));
    }

}
