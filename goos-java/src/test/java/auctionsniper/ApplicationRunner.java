package auctionsniper;

public class ApplicationRunner {
    private static final String
            SNIPER_ID = "sniper";
    private static final String SNIPER_PASSWORD = "sniper";
    private AuctionSniperDriver driver;
    public static final String SNIPER_XMPP_ID =
            SNIPER_ID +
            "@" + FakeAuctionServer.XMPP_HOSTNAME +
            "/" + FakeAuctionServer.AUCTION_RESOURCE;

    public void startBiddingIn(final FakeAuctionServer auction) {
        Thread thread = new Thread("Test application") {
            @Override
            public void run() {
                try {
                    Main.main(FakeAuctionServer.XMPP_HOSTNAME,
                              SNIPER_ID, SNIPER_PASSWORD,
                              auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(MainWindow.STATUS_JOINING);
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(MainWindow.STATUS_LOST);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void hasShownSniperIsBidding() {
        driver.showsSniperStatus(MainWindow.STATUS_BIDDING);
    }
}
