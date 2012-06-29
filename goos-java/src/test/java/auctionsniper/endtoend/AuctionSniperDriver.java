package auctionsniper.endtoend;

import auctionsniper.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.ComponentSelector;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import javax.swing.*;

import static org.hamcrest.core.IsEqual.equalTo;

@SuppressWarnings("unchecked")
public class AuctionSniperDriver extends JFrameDriver {
    public AuctionSniperDriver(int timeoutInMillis) {
        super(new GesturePerformer(), getTopLevelFrame(),
              new AWTEventQueueProber(timeoutInMillis, 100));
    }

    private static ComponentSelector<JFrame> getTopLevelFrame() {
        return topLevelFrame(named(MainWindow.MAIN_WINDOW_NAME),
                             showingOnScreen());
    }

    public void showsSniperStatus(String statusText) {
        new JLabelDriver(this, named(MainWindow.SNIPER_STATUS_NAME))
                .hasText(equalTo(statusText));
    }
}
