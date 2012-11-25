package auctionsniper.test;

import auctionsniper.*;
import org.hamcrest.Matcher;
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
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

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
        SniperSnapshot joining = SniperSnapshot.joining("item id");
        SniperSnapshot bidding = joining.bidding(555, 666);
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));
            oneOf(listener).tableChanged(with(aRowChangedEvent()));
        }});

        model.addSniper(joining);
        model.snipersStateChanged(bidding);

        assertRowMatchesSnapshot(0, bidding);
    }

    @Test public void notifiesListenersWhenAddingASniper() {
        SniperSnapshot joining = SniperSnapshot.joining("item123");
        context.checking(new Expectations() {{
         oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
        }});

        assertEquals(0, model.getRowCount());

        model.addSniper(joining);

        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, joining);
    }

    @Test public void holdsSnipersInAdditionOrder() {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.addSniper(SniperSnapshot.joining("item 0"));
        model.addSniper(SniperSnapshot.joining("item 1"));

        assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
    }

    @Test public void updatesCorrectRowForSniper() {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.addSniper(SniperSnapshot.joining("item 0"));
        SniperSnapshot joining2 = SniperSnapshot.joining("item 1");
        model.addSniper(joining2);

        model.snipersStateChanged(joining2.bidding(1, 1));

        assertEquals(SnipersTableModel.textFor(SniperState.JOINING),
                cellValue(0, Column.SNIPER_STATE));
        assertEquals(SnipersTableModel.textFor(SniperState.BIDDING),
                cellValue(1, Column.SNIPER_STATE));
    }

    @Test(expected = Defect.class)
    public void throwsDefectIfNoExistingSniperForAnUpdate() {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        SniperSnapshot other = SniperSnapshot.joining("item 1");

        model.addSniper(SniperSnapshot.joining("item 0"));
        model.snipersStateChanged(other.bidding(1, 1));
    }

    private String cellValue(int row, Column column) {
        return model.getValueAt(row, column.ordinal()).toString();
    }

    private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
         for (int i = 0; i < model.getColumnCount(); i++) {
             assertEquals(Column.at(i).valueIn(snapshot), model.getValueAt(row, i));
         }
    }

    private Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }

    private Matcher<TableModelEvent> anInsertionAtRow(int row) {
        return samePropertyValuesAs(new TableModelEvent(model, row, 0, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    private Matcher<TableModelEvent> aRowChangedEvent() {
        return samePropertyValuesAs(new TableModelEvent(model, 0));
    }
}
