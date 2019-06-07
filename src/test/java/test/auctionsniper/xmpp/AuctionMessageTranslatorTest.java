package test.auctionsniper.xmpp;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.xmpp.AuctionMessageTranslator;
import auctionsniper.xmpp.XMPPFailureReporter;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class AuctionMessageTranslatorTest {

    private static final String SNIPER_ID = "sniper";
    private static final Chat UNUSED_CHAT = null;

    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();
    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private final XMPPFailureReporter failureReporter = context.mock(XMPPFailureReporter.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener, failureReporter);

    @Test
    public void notifiesAuctionClosedWhenMessageReceived() {
        context.checking(new Expectations() {{
            oneOf(listener).auctionClosed();
        }});

        translator.processMessage(UNUSED_CHAT, message("SOLVersion: 1.1; Event: CLOSE;"));
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
        context.checking(new Expectations() {{
            exactly(1).of(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
        }});

        translator.processMessage(UNUSED_CHAT, message("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;"));
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
        context.checking(new Expectations() {{
            exactly(1).of(listener).currentPrice(234, 5, PriceSource.FromSniper);
        }});

        translator.processMessage(UNUSED_CHAT, message("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";"));
    }

    @Test
    public void notifiesAuctionFailedWhenBadMessageReceived() {
        String badMessage = "a bad message";
        expectFailureWithMessage(badMessage);
        translator.processMessage(UNUSED_CHAT, message(badMessage));
    }

    @Test
    public void notifiesAuctionFailedWhenEventTypeMissing() {
        String incompleteMessage = "SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";";
        expectFailureWithMessage(incompleteMessage);
        translator.processMessage(UNUSED_CHAT, message(incompleteMessage));
    }

    private Message message(String body) {
        Message message = new Message();
        message.setBody(body);
        return message;
    }

    private void expectFailureWithMessage(final String badMessage) {
        context.checking(new Expectations() {{
            exactly(1).of(listener).auctionFailed();
            oneOf(failureReporter).cannotTranslateMessage(with(SNIPER_ID), with(badMessage), with(any(Exception.class)));
        }});
    }

}
