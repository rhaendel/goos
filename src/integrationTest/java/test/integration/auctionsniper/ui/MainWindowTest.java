package test.integration.auctionsniper.ui;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;
import test.integration.auctionsniper.AuctionSniperDriver;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {

    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<>(equalTo("an item-id"), "join request");

        mainWindow.addUserRequestListener(buttonProbe::setReceivedValue);

        driver.startBiddingFor("an item-id");
        driver.check(buttonProbe);
    }

}
