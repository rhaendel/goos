package test.endtoend.auctionsniper;

import org.jivesoftware.smack.XMPPException;
import org.junit.Test;
import test.integration.auctionsniper.ApplicationRunner;

import java.io.IOException;

public class AuctionSniperEndToEndFailingTest extends AuctionSniperEndToEndTestSetup {

    @Test
    public void sniperReportsInvalidAuctionMessageAndStopsRespondingToEvents() throws XMPPException, InterruptedException, IOException {
        String brokenMessage = "a broken message";
        auction.startSellingItem();
        auction2.startSellingItem();

        application.startBiddingIn(auction, auction2);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction.sendInvalidMessageContaining(brokenMessage);
        application.showsSniperHasFailed(auction);

        auction.reportPrice(520, 21, "other bidder");
        waitForAnotherAuctionEvent();

        application.reportsInvalidMessage(auction, brokenMessage);
        application.showsSniperHasFailed(auction);
    }

    private void waitForAnotherAuctionEvent() throws InterruptedException, XMPPException {
        auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auction2.reportPrice(600, 6, "other bidder");
        application.hasShownSniperIsBidding(auction2, 600, 606);
    }

}
