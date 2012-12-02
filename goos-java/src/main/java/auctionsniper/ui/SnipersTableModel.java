package auctionsniper.ui;

import auctionsniper.Column;
import auctionsniper.Defect;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SnipersTableModel extends AbstractTableModel {
    private static final String[] STATUS_TEXT = {
            "joining", "bidding", "winning", "lost", "won"
    };

    private final List<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();

    @Override
    public int getRowCount() {
        return snapshots.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    public void snipersStateChanged(SniperSnapshot newSnapshot) {
        int row = rowMatching(newSnapshot);
        snapshots.set(row, newSnapshot);
        fireTableRowsUpdated(row, row);
    }

    private int rowMatching(SniperSnapshot snapshot) {
        for (int i = 0; i < this.snapshots.size(); i++) {
            if (snapshots.get(i).isForSameItemAs(snapshot)) {
                return i;
            }
        }
        throw new Defect("No sniper found for item " + snapshot.itemId);
    }

    public void addSniper(SniperSnapshot snapshot) {
        this.snapshots.add(snapshot);
        fireTableRowsInserted(0, 0);
    }
}
