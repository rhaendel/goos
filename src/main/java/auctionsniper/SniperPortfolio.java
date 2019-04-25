package auctionsniper;

import java.util.ArrayList;
import java.util.EventListener;

public class SniperPortfolio implements SniperCollector {

    private final ArrayList<AuctionSniper> snipers = new ArrayList<>();
    private PortfolioListener portfolioListener;

    @Override
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        portfolioListener.sniperAdded(sniper);
    }

    public void addPortfolioListener(PortfolioListener portfolioListener) {
        this.portfolioListener = portfolioListener;
    }

    public interface PortfolioListener extends EventListener {
        void sniperAdded(AuctionSniper sniper);
    }
}
