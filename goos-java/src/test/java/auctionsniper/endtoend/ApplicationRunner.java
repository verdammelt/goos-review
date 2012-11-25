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
                    Main.main(arguments(auctions));
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
        for (FakeAuctionServer auction : auctions) {
            driver.showsSniperStatus(auction.getItemId(), 0, 0, textFor(SniperState.JOINING));
        }
    }

    private String[] arguments(FakeAuctionServer... auctions) {
        String[] arguments = new String[auctions.length + 3];
        arguments[0] = FakeAuctionServer.XMPP_HOSTNAME;
        arguments[1] = SNIPER_ID;
        arguments[2] = SNIPER_PASSWORD;
        for (int i = 0; i < auctions.length; i++) {
            arguments[i + 3] = auctions[i].getItemId();
        }

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
}
