package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private Auction auction;
    private final SniperListener sniperListener;

    public AuctionSniper(Auction auction, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
    }

    public void auctionClosed() {
        sniperListener.sniperLost();
    }

    @Override public void currentPrice(int price, int increment) {
        auction.bid(price+increment);
        sniperListener.sniperBidding();
    }
}
