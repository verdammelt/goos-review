package auctionsniper.endtoend;

import auctionsniper.Main;
import auctionsniper.MainWindow;

public class ApplicationRunner {
    private String itemId;

    private static final String SNIPER_ID = "sniper";
    private static final String SNIPER_PASSWORD = "sniper";
    private AuctionSniperDriver driver;
    public static final String SNIPER_XMPP_ID =
            SNIPER_ID +
            "@" + FakeAuctionServer.XMPP_HOSTNAME +
            "/" + FakeAuctionServer.AUCTION_RESOURCE;

    public void startBiddingIn(final FakeAuctionServer auction) {
        itemId = auction.getItemId();

        Thread thread = new Thread("Test application") {
            @Override
            public void run() {
                try {
                    Main.main(FakeAuctionServer.XMPP_HOSTNAME, SNIPER_ID,
                              SNIPER_PASSWORD, auction
                            .getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus("", 0, 0, MainWindow.STATUS_JOINING);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(itemId, 0, 0, MainWindow.STATUS_LOST);
    }

    public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, MainWindow.STATUS_BIDDING);
    }

    public void hasShownSniperIsWinning(int winningBid) {
        driver.showsSniperStatus(itemId, winningBid, winningBid, MainWindow.STATUS_WINNING);
    }

    public void showsSniperHasWonAuction(int lastPrice) {
        driver.showsSniperStatus(itemId, lastPrice, lastPrice, MainWindow.STATUS_WON);
    }
}
