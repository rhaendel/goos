package test.endtoend.auctionsniper;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;

import static auctionsniper.ui.SnipersTableModel.textFor;
import static test.endtoend.auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;

class ApplicationRunner {

    private static final String SNIPER_ID = "sniper";
    private static final String SNIPER_PASSWORD = "sniper";
    static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + FakeAuctionServer.XMPP_HOSTNAME + "/" + Main.AUCTION_RESOURCE;

    private AuctionSniperDriver driver;

    void startBiddingIn(final FakeAuctionServer... auctions) {
        startSniper();
        for (FakeAuctionServer auction : auctions) {
            final String itemId = auction.getItemID();
            driver.startBiddingFor(itemId);
            driver.showsSniperStatus(itemId, 0, 0, textFor(SniperState.JOINING));
        }
    }

    private void startSniper() {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(arguments());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
    }

    private static String[] arguments() {
        String[] arguments = new String[3];
        arguments[0] = XMPP_HOSTNAME;
        arguments[1] = SNIPER_ID;
        arguments[2] = SNIPER_PASSWORD;
        return arguments;
    }

    void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    void showsSniperHasLostAuction() {
        driver.showsSniperStatus(textFor(SniperState.LOST));
    }

    void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemID(), lastPrice, lastBid, textFor(SniperState.BIDDING));
    }

    void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemID(), winningBid, winningBid, textFor(SniperState.WINNING));
    }

    void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemID(), lastPrice, lastPrice, textFor(SniperState.WON));
    }

}
