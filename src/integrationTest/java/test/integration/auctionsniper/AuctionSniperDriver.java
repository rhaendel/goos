package test.integration.auctionsniper;

import auctionsniper.Item;
import auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JButtonDriver;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.driver.JTextFieldDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import static auctionsniper.ui.MainWindow.NEW_ITEM_ID_NAME;
import static auctionsniper.ui.MainWindow.NEW_ITEM_STOP_PRICE_NAME;
import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static java.lang.String.valueOf;
import static org.hamcrest.CoreMatchers.equalTo;

public class AuctionSniperDriver extends JFrameDriver {

    @SuppressWarnings("unchecked")
    public AuctionSniperDriver(int timeoutMillis) {
        super(new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(MainWindow.MAIN_WINDOW_NAME),
                        showingOnScreen()),
                        new AWTEventQueueProber(timeoutMillis, 100));
    }

    public void startBiddingFor(Item item) {
        textField(NEW_ITEM_ID_NAME).replaceAllText(item.identifier);
        textField(NEW_ITEM_STOP_PRICE_NAME).replaceAllText(String.valueOf(item.stopPrice));
        bidButton().click();
    }

    private JTextFieldDriver textField(String name) {
        JTextFieldDriver newItemId = new JTextFieldDriver(this, JTextField.class, named(name));
        newItemId.focusWithMouse();
        return newItemId;
    }

    @SuppressWarnings("unchecked")
    private JButtonDriver bidButton() {
        return new JButtonDriver(this, JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
    }

    @SuppressWarnings("unchecked")
    public void showsSniperStatus(String statusText) {
        new JTableDriver(this).hasCell(withLabelText(equalTo(statusText)));
    }

    @SuppressWarnings("unchecked")
    public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String statusText) {
        JTableDriver table = new JTableDriver(this);
        table.hasRow(matching( //
                withLabelText(itemId), withLabelText(valueOf(lastPrice)), //
                withLabelText(valueOf(lastBid)), withLabelText(statusText)));
    }

    @SuppressWarnings("unchecked")
    public void hasColumnTitles() {
        JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
        headers.hasHeaders(matching(withLabelText("Item"), withLabelText("Last Price"), withLabelText("Last Bid"), withLabelText("State")));
    }

}
