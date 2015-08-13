package auctionsniper.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.exception.Defect;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {

    private static final long serialVersionUID = 1L;

    private static final String[] STATUS_TEXT = { "Joining", "Bidding", "Winning", "Lost", "Won" };

    private final ArrayList<SniperSnapshot> snapshots = new ArrayList<>();

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public int getRowCount() {
        return snapshots.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        int row = rowMatching(newSnapshot);
        this.snapshots.set(row, newSnapshot);
        fireTableRowsUpdated(row, row);
    }

    private int rowMatching(SniperSnapshot snapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (snapshot.isForSameItemAs(snapshots.get(i))) {
                return i;
            }
        }
        throw new Defect("Cannot find match for " + snapshot);
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    public enum Column {

        ITEM_IDENTIFIER("Item") {
            @Override
            public Object valueIn(SniperSnapshot snapshot) {
                return snapshot.itemId;
            }
        },

        LAST_PRICE("Last Price") {
            @Override
            public Object valueIn(SniperSnapshot snapshot) {
                return snapshot.lastPrice;
            }
        },

        LAST_BID("Last Bid") {
            @Override
            public Object valueIn(SniperSnapshot snapshot) {
                return snapshot.lastBid;
            }
        },

        SNIPER_STATE("State") {
            @Override
            public Object valueIn(SniperSnapshot snapshot) {
                return SnipersTableModel.textFor(snapshot.state);
            }
        };

        public final String name;

        private Column(String name) {
            this.name = name;
        }

        public static Column at(int offset) {
            return values()[offset];
        }

        abstract public Object valueIn(SniperSnapshot snapshot);
    }

    public void addSniper(SniperSnapshot snapshot) {
        snapshots.add(snapshot);
        fireTableRowsInserted(snapshots.size() - 1, snapshots.size() - 1);
    }

}
