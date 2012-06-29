package auctionsniper;

public interface AuctionEventListener {
    public void auctionClosed();

    void currentPrice(int price, int increment);
}
