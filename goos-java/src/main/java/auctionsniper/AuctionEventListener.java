package auctionsniper;

public interface AuctionEventListener {
    enum PriceSource {
        FromSniper, FromOtherBidder
    }

    public void auctionClosed();

    void currentPrice(
            int price, int increment, PriceSource fromOtherBidder);
}
