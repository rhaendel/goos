package test.endtoend.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import static java.lang.String.format;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FakeAuctionServer {

    private final SingleMessageListener messageListener = new SingleMessageListener();

    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String XMPP_HOSTNAME = "localhost";
    private static final String AUCTION_PASSWORD = "auction";

    private final String itemId;
    private final XMPPConnection connection;
    private Chat currentChat;

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        this.connection = new XMPPConnection(XMPP_HOSTNAME);
    }

    public void startSellingItem() throws XMPPException {
        connection.connect();
        connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
        connection.getChatManager().addChatListener((chat, createdLocally) -> {
            currentChat = chat;
            chat.addMessageListener(messageListener);
        });
    }

    public void reportPrice(int price, int increment, String bidder) throws XMPPException {
        currentChat.sendMessage(format("SQLVersion: 1.1; Event: PRICE; CurrentPrice: %d, Increment: %d; Bidder: %s:",
                price, increment, bidder));
    }

    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        messageListener.receivesAMessage(is(anything()));
    }

    public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
        messageListener.receivesAMessage(equalTo(format("SQLVersion: 1.1; Command: BID; Price: %d;", bid)));
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage(new Message());
    }

    public void stop() {
        connection.disconnect();
    }

    public String getItemID() {
        return itemId;
    }

}
