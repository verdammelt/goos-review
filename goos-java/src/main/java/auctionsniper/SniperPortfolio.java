package auctionsniper;

import java.util.ArrayList;

public class SniperPortfolio implements SniperCollector {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final ArrayList<AuctionSniper> snipers = new ArrayList<AuctionSniper>();
    private final ArrayList<PortfolioListener> portfolioListeners = new ArrayList<PortfolioListener>();

    public void addPortfolioListener(PortfolioListener listener) {
        portfolioListeners.add(listener);
    }

    @Override
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        for (PortfolioListener listener : portfolioListeners) {
            listener.sniperAdded(sniper);
        }
    }
}
