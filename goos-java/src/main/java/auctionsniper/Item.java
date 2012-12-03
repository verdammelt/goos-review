package auctionsniper;

public class Item {
    public final String itemId;
    private final int stopPrice;

    public Item(String itemId, int stopPrice) {
        this.itemId = itemId;
        this.stopPrice = stopPrice;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId='" + itemId + '\'' +
                ", stopPrice=" + stopPrice +
                '}';
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (stopPrice != item.stopPrice) return false;
        if (itemId != null ? !itemId.equals(item.itemId) : item.itemId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = itemId != null ? itemId.hashCode() : 0;
        result = 31 * result + stopPrice;
        return result;
    }

    public boolean allowsBid(int bid) {
        return bid <= stopPrice;
    }
}
