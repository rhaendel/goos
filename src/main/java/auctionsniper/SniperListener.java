package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {

    void sniperLost();

    void sniperBidding(SniperSnapshot sniperSnapshot);

    void sniperWinning();

    void sniperWon();

}
