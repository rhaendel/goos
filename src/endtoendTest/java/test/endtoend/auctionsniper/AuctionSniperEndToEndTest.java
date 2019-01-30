package test.endtoend.auctionsniper;

import org.jivesoftware.smack.XMPPException;
import org.junit.Test;

public class AuctionSniperEndToEndTest extends AuctionSniperEndToEndTestSetup {

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws XMPPException, InterruptedException {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }

}
