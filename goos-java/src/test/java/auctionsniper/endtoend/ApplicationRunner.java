package auctionsniper.endtoend;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;
import auctionsniper.utilities.FakeAuctionServer;

import static auctionsniper.ui.SnipersTableModel.textFor;

class ApplicationRunner {

    private static final String SNIPER_ID = "sniper";
    private static final String SNIPER_PASSWORD = "sniper";
    private AuctionSniperDriver driver;

    public static final String SNIPER_XMPP_ID =
            SNIPER_ID +
                    "@" + FakeAuctionServer.XMPP_HOSTNAME +
                    "/" + FakeAuctionServer.AUCTION_RESOURCE;

    public void startBiddingIn(final FakeAuctionServer... auctions) {
        startSniper();
        for (FakeAuctionServer auction : auctions) {
            final String itemId = auction.getItemId();
            driver.startBiddingFor(itemId, Integer.MAX_VALUE);
            driver.showsSniperStatus(itemId, 0, 0, textFor(SniperState.JOINING));
        }
    }

    public void startBiddingWithStopPrice(FakeAuctionServer auction, int stopPrice) {
        startSniper();
        final String itemId = auction.getItemId();
        driver.startBiddingFor(itemId, stopPrice);
        driver.showsSniperStatus(itemId, 0, 0, textFor(SniperState.JOINING));
    }

    private void startSniper() {
        Thread thread = new Thread("Test application") {
            @Override
            public void run() {
                try {
                    Main.main(arguments());
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
    }

    private String[] arguments() {
        String[] arguments = new String[3];
        arguments[0] = FakeAuctionServer.XMPP_HOSTNAME;
        arguments[1] = SNIPER_ID;
        arguments[2] = SNIPER_PASSWORD;

        return arguments;
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

    public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOSING));

    }

    public void showsSniperHasFailed(FakeAuctionServer auction) {
        driver.showsSniperStatus(auction.getItemId(), 0, 0, textFor(SniperState.FAILED));
    }

    public void reportsInvalidMessage(FakeAuctionServer auction, String brokenMessage) {
        //To change body of created methods use File | Settings | File Templates.
    }
}
