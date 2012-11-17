package auctionsniper.endtoend;

import auctionsniper.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.ComponentSelector;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import javax.swing.*;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;

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

    public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String statusText) {
        JTableDriver table = new JTableDriver(this);
        table.hasRow(matching(
                withLabelText(itemId),
                withLabelText(String.valueOf(lastPrice)),
                withLabelText(String.valueOf(lastBid)),
                withLabelText(statusText)));
    }
}
