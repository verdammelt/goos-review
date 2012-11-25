package auctionsniper.endtoend;

import auctionsniper.Main;
import auctionsniper.MainWindow;
import auctionsniper.SniperState;

import static auctionsniper.SnipersTableModel.textFor;

public class ApplicationRunner {

    private static final String SNIPER_ID = "sniper";
    private static final String SNIPER_PASSWORD = "sniper";
    private AuctionSniperDriver driver;

    public static final String SNIPER_XMPP_ID =
            SNIPER_ID +
                    "@" + FakeAuctionServer.XMPP_HOSTNAME +
                    "/" + FakeAuctionServer.AUCTION_RESOURCE;

    public void startBiddingIn(final FakeAuctionServer... auctions) {
        Thread thread = new Thread("Test application") {
            @Override
            public void run() {
                try {
                    Main.main(FakeAuctionServer.XMPP_HOSTNAME, SNIPER_ID,
                              SNIPER_PASSWORD, auctions[0]
                            .getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
        driver.showsSniperStatus("", 0, 0, textFor(SniperState.JOINING));
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void showsSniperHasLostAuction(FakeAuctionServer auctionServer, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auctionServer.getItemId(), lastPrice, lastBid, textFor(SniperState.LOST));
    }

    public void hasShownSniperIsBidding(FakeAuctionServer auctionServer, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auctionServer.getItemId(), lastPrice, lastBid, textFor(SniperState.BIDDING));
    }

    public void hasShownSniperIsWinning(FakeAuctionServer auctionServer, int winningBid) {
        driver.showsSniperStatus(auctionServer.getItemId(), winningBid, winningBid, textFor(SniperState.WINNING));
    }

    public void showsSniperHasWonAuction(FakeAuctionServer auctionServer, int lastPrice) {
        driver.showsSniperStatus(auctionServer.getItemId(), lastPrice, lastPrice, textFor(SniperState.WON));
    }
}
