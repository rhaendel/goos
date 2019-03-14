package auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.Main;
import auctionsniper.util.Announcer;

public class XMPPAuction implements Auction {

    private static final String AUCTION_ID_FORMAT = Main.ITEM_ID_AS_LOGIN + "@%s/" + Main.AUCTION_RESOURCE;

    private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
    private final Chat chat;

    public XMPPAuction(XMPPConnection connection, String itemId) {
        this.chat = connection.getChatManager().createChat(auctionId(itemId, connection),
                new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce()));
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

    @Override
    public void bid(int amount) {
        sendMessage(String.format(Main.BID_COMMAND_FORMAT, amount));
    }

    @Override
    public void join() {
        sendMessage(Main.JOIN_COMMAND_FORMAT);
    }

    private void sendMessage(String message) {
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener listener) {
        this.auctionEventListeners.addListener(listener);
    }
}