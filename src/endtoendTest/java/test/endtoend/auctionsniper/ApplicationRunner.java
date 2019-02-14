package test.endtoend.auctionsniper;

import static auctionsniper.ui.SnipersTableModel.textFor;
import auctionsniper.Main;
import auctionsniper.SniperState;

public class ApplicationRunner {

    private static final String SNIPER_ID = "sniper";
    private static final String SNIPER_PASSWORD = "sniper";
    public static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + FakeAuctionServer.XMPP_HOSTNAME + "/" + Main.AUCTION_RESOURCE;

    private AuctionSniperDriver driver;
    private String itemId;

    public void startBiddingIn(final FakeAuctionServer auction) {
        this.itemId = auction.getItemID();

        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(FakeAuctionServer.XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemID());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(textFor(SniperState.JOINING));
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(textFor(SniperState.LOST));
    }

    public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, textFor(SniperState.BIDDING));
    }

    public void hasShownSniperIsWinning(int winningBid) {
        driver.showsSniperStatus(itemId, winningBid, winningBid, textFor(SniperState.WINNING));
    }

    public void showsSniperHasWonAuction(int lastPrice) {
        driver.showsSniperStatus(itemId, lastPrice, lastPrice, textFor(SniperState.WON));
    }

}
