package auctionsniper;

import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.SwingThreadSniperListener;

import java.util.Collection;
import java.util.LinkedList;

public class SniperLauncher implements UserRequestListener {

    private final Collection<Auction> notToBeGCd = new LinkedList<>();
    private final AuctionHouse auctionHouse;
    private final SnipersTableModel snipers;

    SniperLauncher(AuctionHouse auctionHouse, SnipersTableModel snipers) {
        this.auctionHouse = auctionHouse;
        this.snipers = snipers;
    }

    @Override
    public void joinAuction(String itemId) {
        snipers.addSniper(SniperSnapshot.joining(itemId));
        Auction auction = auctionHouse.auctionFor(itemId);
        notToBeGCd.add(auction);
        AuctionSniper sniper = new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers));
        auction.addAuctionEventListener(sniper);
        auction.join();
    }
}
