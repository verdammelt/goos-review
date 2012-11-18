package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction;
    private final SniperListener sniperListener;
    private boolean isWinning = false;
    private SniperSnapshot snapshot;

    public AuctionSniper(Auction auction, SniperListener sniperListener, String itemId) {
        this.auction = auction;
        this.sniperListener = sniperListener;
        this.snapshot = SniperSnapshot.joining(itemId);
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
        if (isWinning) {
            snapshot = snapshot.winning(price);
        } else {
            final int bid = price + increment;
            auction.bid(bid);
            snapshot = snapshot.bidding(price, bid);
        }
        System.out.println ("snapshot: " + snapshot);
        sniperListener.sniperStateChanged(snapshot);
    }
}
