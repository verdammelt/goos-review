package auctionsniper.test;

import auctionsniper.Column;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.SnipersTableModel;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JMock.class)
public class SnipersTableModelTest {
    private final Mockery context = new Mockery();
    private TableModelListener listener = context.mock(TableModelListener.class);
    private final SnipersTableModel model = new SnipersTableModel();

    @Before
    public void attachModelListener() {
        model.addTableModelListener(listener);
    }

    @Test
    public void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    public void setsUpColumnHeadings() {
        for (Column column : Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    public void setsSniperValuesInColumns() {
        context.checking(new Expectations() {{
            oneOf(listener).tableChanged(with(any(TableModelEvent.class))); // TODO: should be specific event matcher
        }});

        model.snipersStateChanged(new SniperSnapshot("item id", 555, 666, SniperState.BIDDING));

        assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
    }

    private void assertColumnEquals(Column column, String expected) {
        final int rowIndex = 0;
        final int columnIndex = column.ordinal();

        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }

    // TODO: should be used above - but not working right now.
//    private Matcher<TableModelEvent> aRowChangedEvent() {
//        return samePropertyValuesAs(new TableModelEvent(model, 0));
//    }
}
