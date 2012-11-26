package auctionsniper.integration;

import auctionsniper.MainWindow;
import auctionsniper.SnipersTableModel;
import auctionsniper.UserRequestListener;
import auctionsniper.endtoend.AuctionSniperDriver;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {
    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test public void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<String> buttonProbe =
                new ValueMatcherProbe<String>(equalTo("thing"),
                        "join request");

        mainWindow.addUserRequestListener(
                new UserRequestListener() {
                    public void joinAuction(String itemId) {
                        buttonProbe.setReceivedValue(itemId);
                    }
                });

        driver.startBiddingFor("thing");
        driver.check(buttonProbe);
    }

}
