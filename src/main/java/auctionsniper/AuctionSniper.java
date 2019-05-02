package auctionsniper;

import java.util.ArrayList;

public class AuctionSniper implements AuctionEventListener {

    private final ArrayList<SniperListener> sniperListeners = new ArrayList<>();
    private final Auction auction;
    private SniperSnapshot snapshot;

    public AuctionSniper(Item item, Auction auction) {
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(item.identifier);
    }

    @Override
    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
        case FromSniper:
            snapshot = snapshot.winning(price);
            break;
        case FromOtherBidder:
            final int bid = price + increment;
            auction.bid(bid);
            snapshot = snapshot.bidding(price, bid);
            break;
        }
        notifyChange();
    }

    private void notifyChange() {
        for (SniperListener listener : sniperListeners) {
            listener.sniperStateChanged(snapshot);
        }
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }

    public void addSniperListener(SniperListener sniperListener) {
        sniperListeners.add(sniperListener);
    }
}
