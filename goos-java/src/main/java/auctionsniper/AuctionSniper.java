package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction;
    private final SniperListener sniperListener;
    private boolean isWinning = false;
    private final String itemId;

    public AuctionSniper(Auction auction, SniperListener sniperListener, String itemId) {
        this.auction = auction;
        this.sniperListener = sniperListener;
        this.itemId = itemId;
    }

    public void auctionClosed() {
        if (isWinning) {
            sniperListener.sniperWon();
        }
        else {
            sniperListener.sniperLost();
        }
    }

    @Override public void currentPrice(
            int price, int increment, PriceSource priceSource) {
        isWinning = priceSource == PriceSource.FromSniper;
        switch (priceSource) {
            case FromSniper:
                sniperListener.sniperWinning();
                break;
            case FromOtherBidder:
                int bid = price + increment;
                auction.bid(bid);
                sniperListener.sniperBidding(new SniperState(itemId, price, bid));
        }
    }
}
