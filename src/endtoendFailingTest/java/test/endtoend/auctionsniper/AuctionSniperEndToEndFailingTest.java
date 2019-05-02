package test.endtoend.auctionsniper;

import org.jivesoftware.smack.XMPPException;
import org.junit.Test;
import test.integration.auctionsniper.ApplicationRunner;

public class AuctionSniperEndToEndFailingTest extends AuctionSniperEndToEndTestSetup {

    @Test
    public void sniperLosesAnAuctionWhenThePriceIsTooHigh() throws XMPPException, InterruptedException {
        auction.startSellingItem();
        application.startBiddingWithStopPrice(auction, 1100);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction, 1000, 1098);

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1197, 10, "third party");
        application.hasShownSniperIsLosing(auction, 1197, 1098);

        auction.reportPrice(1207, 10, "fourth party");
        application.hasShownSniperIsLosing(auction, 1207, 1098);

        auction.announceClosed();
        application.showsSniperHasLostAuction(auction, 1207, 1098);
    }
}
