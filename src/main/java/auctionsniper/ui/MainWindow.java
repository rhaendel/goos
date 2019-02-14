package auctionsniper.ui;

import auctionsniper.SniperSnapshot;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Container;

public class MainWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    private static final String APPLICATION_TITLE = "Auction Sniper";
    private static final String SNIPERS_TABLE_NAME = "Snipers";
    private final SnipersTableModel snipers = new SnipersTableModel();

    public MainWindow() {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fillContentPane(JTable snipersTable) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable() {
        final JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    public void sniperStatusChanged(SniperSnapshot sniperSnapshot) {
        snipers.sniperStateChanged(sniperSnapshot);
    }
}
