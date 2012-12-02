package auctionsniper.test;

import auctionsniper.*;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class SniperLauncherTest {
    private final Mockery context = new Mockery();
    private final States auctionState =
            context.states("auction state").startsAs("not joined");

    private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
    private final Auction auction = context.mock(Auction.class);
    private final SniperCollector sniperCollector = context.mock(SniperCollector.class);

    @Test public void addsNewSniperToCollectorAndThenJoinsAuction() {
        final String itemId = "item 123";
        context.checking(new Expectations() {{
            allowing(auctionHouse).auctionFor(itemId); will(returnValue(auction));
            oneOf(auction).addAuctionEventListener(with(sniperForItem(itemId)));
            when(auctionState.is("not joined"));
            oneOf(sniperCollector).addSniper(with(sniperForItem(itemId)));
            when(auctionState.is("not joined"));

            oneOf(auction).join();
            then(auctionState.is("joined"));
        }});

        new SniperLauncher(sniperCollector, auctionHouse).joinAuction(itemId);
    }

    private Matcher<AuctionSniper> sniperForItem(final String itemId) {
        return new BaseMatcher<AuctionSniper>() {
            @Override
            public boolean matches(Object item) {
                AuctionSniper sniper = (AuctionSniper)item;
                return sniper.getSnapshot().itemId.equals(itemId);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("hello");
            }
        };
    }
}
