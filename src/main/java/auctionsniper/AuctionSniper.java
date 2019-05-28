package auctionsniper;

import auctionsniper.util.Announcer;

public class AuctionSniper implements AuctionEventListener {

    private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
    private final Auction auction;
    private final Item item;
    private SniperSnapshot snapshot;

    public AuctionSniper(Item item, Auction auction) {
        this.auction = auction;
        this.item = item;
        this.snapshot = SniperSnapshot.joining(item.identifier);
    }

    @Override
    public void auctionClosed() {
        snapshot = snapshot.closed();
        listeners.announce().sniperStateChanged(snapshot);
    }

    @Override
    public void auctionFailed() {
        snapshot = snapshot.failed();
        listeners.announce().sniperStateChanged(snapshot);
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
        case FromSniper:
            snapshot = snapshot.winning(price);
            break;
        case FromOtherBidder:
            final int bid = price + increment;
            if (item.allowsBid(bid)) {
                auction.bid(bid);
                snapshot = snapshot.bidding(price, bid);
            } else {
                snapshot = snapshot.losing(price);
            }
            break;
        }
        listeners.announce().sniperStateChanged(snapshot);
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }

    public void addSniperListener(SniperListener sniperListener) {
        listeners.addListener(sniperListener);
    }
}
