package test.endtoend.auctionsniper;

import org.junit.After;

public class AuctionSniperEndToEndTestSetup {

    protected final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    protected final FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");
    protected final ApplicationRunner application = new ApplicationRunner();

    @After
    public void stopAuction() {
        auction.stop();
        auction2.stop();
    }

    @After
    public void stopApplication() {
        application.stop();
    }

}
