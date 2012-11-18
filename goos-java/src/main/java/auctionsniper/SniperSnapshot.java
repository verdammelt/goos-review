package auctionsniper;

public class SniperSnapshot {
    public final String itemId;
    public final int lastPrice;
    public final int lastBid;
    public final SniperState state;

    public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState state) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
        this.state = state;
    }

    public SniperSnapshot bidding(int newLastPrice, int newLastBid) {
        return new SniperSnapshot(itemId, newLastPrice, newLastBid, SniperState.BIDDING);
    }
    public SniperSnapshot winning(int newLastPrice) {
        return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.WINNING);
    }
    public static SniperSnapshot joining(String itemId) {
        return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
    }

    @Override
    public String toString() {
        return "SniperSnapshot{" +
                "itemId='" + itemId + '\'' +
                ", lastPrice=" + lastPrice +
                ", lastBid=" + lastBid +
                ", state=" + state +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SniperSnapshot that = (SniperSnapshot) o;

        if (lastBid != that.lastBid) return false;
        if (lastPrice != that.lastPrice) return false;
        if (itemId != null ? !itemId.equals(that.itemId) : that.itemId != null)
            return false;
        //noinspection RedundantIfStatement
        if (state != that.state) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = itemId != null ? itemId.hashCode() : 0;
        result = 31 * result + lastPrice;
        result = 31 * result + lastBid;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }

    public SniperSnapshot closed() {
        return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());
    }
}
