package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {

    void sniperLost();

    void sniperWinning();

    void sniperWon();

    void sniperStateChanged(SniperSnapshot sniperSnapshot);

}
