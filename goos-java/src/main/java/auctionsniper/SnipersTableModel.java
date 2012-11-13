package auctionsniper;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {
    private String statusText = MainWindow.STATUS_JOINING;

    @Override
    public int getRowCount() { return 1; }

    @Override
    public int getColumnCount() { return 1; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) { return statusText; }

    public void setStatusText(String newStatusText) {
        statusText = newStatusText;
        fireTableCellUpdated(0, 0);
    }
}
