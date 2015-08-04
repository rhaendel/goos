package test.endtoend.auctionsniper;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static java.lang.String.valueOf;
import static org.hamcrest.CoreMatchers.equalTo;

import javax.swing.table.JTableHeader;

import auctionsniper.ui.MainWindow;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

public class AuctionSniperDriver extends JFrameDriver {

    @SuppressWarnings("unchecked")
    public AuctionSniperDriver(int timeoutMillis) {
        super(new GesturePerformer(), //
                JFrameDriver.topLevelFrame( //
                        named(MainWindow.MAIN_WINDOW_NAME), //
                        showingOnScreen()), //
                new AWTEventQueueProber(timeoutMillis, 100));
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
