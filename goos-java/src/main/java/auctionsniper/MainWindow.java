package auctionsniper;

import auctionsniper.lib.Announcer;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {
    private final Announcer<UserRequestListener> userRequests =
            Announcer.to(UserRequestListener.class);

    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPERS_TABLE_NAME = "sniper status";
    public static final String APPLICATION_TITLE = "foobar";
    public static final String JOIN_BUTTON_NAME = "join button";
    public static final String NEW_ITEM_ID_NAME = "item id";

    public MainWindow(SnipersTableModel snipers) {
        super("Auction Sniper");
        setTitle(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(snipers), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void addUserRequestListener(UserRequestListener listener) {
        userRequests.addListener(listener);
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());

        final JTextField itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        controls.add(itemIdField);

        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        joinAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                userRequests.announce().joinAuction(itemIdField.getText());
            }
        });
        controls.add(joinAuctionButton);

        return controls;
    }

    private void fillContentPane(JTable snipersTable, JPanel addItemPanel) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(addItemPanel, BorderLayout.NORTH);
        contentPane.add(addItemPanel, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable(TableModel snipers) {
        final JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }
}
