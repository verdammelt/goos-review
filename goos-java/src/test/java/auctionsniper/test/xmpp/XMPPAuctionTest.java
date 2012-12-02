package auctionsniper.test.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.utilities.FakeAuctionServer;
import auctionsniper.xmpp.XMPPAuctionHouse;
import org.jivesoftware.smack.XMPPConnection;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class XMPPAuctionTest {
    @Test
    public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        FakeAuctionServer auctionServer = new FakeAuctionServer("item54321");
        XMPPConnection connection = auctionServer.getConnection();
        auctionServer.startSellingItem();

        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        XMPPAuctionHouse auctionHouse = new XMPPAuctionHouse(connection);

        Auction auction = auctionHouse.auctionFor(auctionServer.getItemId());
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

        auction.join();
        auctionServer.hasReceivedJoinRequestFromSniper("auction-item54321@virgil.local/Auction");
        auctionServer.announceClosed();

        assertTrue("should have been closed", auctionWasClosed.await(2, TimeUnit.SECONDS));
    }

    private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            @Override
            public void auctionFailed() {
//                auctionWasClosed.countDown();
            }

            @Override
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource fromOtherBidder) {
                // not implemented
            }
        };
    }


}
