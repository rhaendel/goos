package test.auctionsniper.ui;

import auctionsniper.AuctionSniper;
import auctionsniper.Item;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.SnipersTableModel.Column;
import org.hamcrest.Matcher;
import org.hamcrest.beans.SamePropertyValuesAs;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class SnipersTableModelTest {

    private static final String ITEM_ID = "item 0";

    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();
    private final TableModelListener listener = context.mock(TableModelListener.class);
    private final SnipersTableModel model = new SnipersTableModel();
    private final AuctionSniper sniper = new AuctionSniper(new Item(ITEM_ID, 999), null);

    @Before
    public void attachModelListener() {
        model.addTableModelListener(listener);
    }

    @Test
    public void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    public void setsSniperValuesInColumns() {
        SniperSnapshot bidding = sniper.getSnapshot().bidding(555, 666);
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));
            oneOf(listener).tableChanged(with(aChangeInRow(0)));
        }});

        model.sniperAdded(sniper);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(0, bidding);
    }

    @Test
    public void setsUpColumnHeadings() {
        for (Column column : Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    public void notifiesListenersWhenAddingASniper() {
        context.checking(new Expectations() {{
            oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
        }});

        assertEquals(0, model.getRowCount());
        model.sniperAdded(sniper);

        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, SniperSnapshot.joining(ITEM_ID));
    }

    @Test
    public void holdsSnipersInAdditionOrder() {
        final AuctionSniper sniper2 = new AuctionSniper(new Item("item 2", 999), null);
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.sniperAdded(sniper);
        model.sniperAdded(sniper2);

        assertEquals(ITEM_ID, cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item 2", cellValue(1, Column.ITEM_IDENTIFIER));
    }

    @Test
    public void updatesCorrectRowForSniper() {
        final AuctionSniper sniper1 = new AuctionSniper(new Item("item 1", 999), null);
        final AuctionSniper sniper2 = new AuctionSniper(new Item("item 2", 999), null);
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));
            oneOf(listener).tableChanged(with(aChangeInRow(1)));
        }});


        model.sniperAdded(sniper);
        model.sniperAdded(sniper1);
        model.sniperAdded(sniper2);

        assertStateInRow(SniperState.JOINING, 0);
        assertStateInRow(SniperState.JOINING, 1);
        assertStateInRow(SniperState.JOINING, 2);

        model.sniperStateChanged(sniper1.getSnapshot().bidding(123, 123));

        assertStateInRow(SniperState.JOINING, 0);
        assertStateInRow(SniperState.BIDDING, 1);
        assertStateInRow(SniperState.JOINING, 2);
    }

    @Test(expected = auctionsniper.exception.Defect.class)
    public void throwsDefectIfNoExistingSniperForAnUpdate() {
        model.sniperStateChanged(SniperSnapshot.joining("item").bidding(123, 123));
    }

    private void assertStateInRow(SniperState expectedState, int row) {
        assertEquals(SnipersTableModel.textFor(expectedState), cellValue(row, Column.SNIPER_STATE));
    }

    private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
        assertEquals(snapshot.itemId, cellValue(row, Column.ITEM_IDENTIFIER));
        assertEquals(snapshot.lastPrice, cellValue(row, Column.LAST_PRICE));
        assertEquals(snapshot.lastBid, cellValue(row, Column.LAST_BID));
        assertEquals(SnipersTableModel.textFor(snapshot.state), cellValue(row, Column.SNIPER_STATE));
    }

    private Object cellValue(int rowIndex, Column column) {
        return model.getValueAt(rowIndex, column.ordinal());
    }

    private Matcher<TableModelEvent> aChangeInRow(int row) {
        return samePropertyValueAs(new TableModelEvent(model, row));
    }

    private Matcher<TableModelEvent> anInsertionAtRow(final int row) {
        return samePropertyValuesAs(new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    private Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }

    private SamePropertyValuesAs<TableModelEvent> samePropertyValueAs(TableModelEvent expectedBean) {
        return new SamePropertyValuesAs<>(expectedBean);
    }
}
