package test.auctionsniper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

public class SniperSnapshotTest {

    @Test
    public void sniperStateEnumEquals() {
        SniperState state1 = SniperState.BIDDING;
        SniperState state2 = SniperState.BIDDING;

        assertEquals(state1.toString(), state2.toString());
        assertEquals(state1.hashCode(), state2.hashCode());
        assertTrue(state1.equals(state2));
    }

    @Test
    public void equalSniperSnapshotsAreEqual() {
        SniperSnapshot snap1 = new SniperSnapshot("item-1", 0, 0, SniperState.JOINING);
        SniperSnapshot snap2 = new SniperSnapshot("item-1", 0, 0, SniperState.JOINING);

        assertTrue(snap1.equals(snap2));
        assertTrue(snap2.equals(snap1));

        assertEquals(snap1.hashCode(), snap2.hashCode());
        assertEquals(snap1.toString(), snap2.toString());
    }

    @Test
    public void snapshotsWithDifferentItemAreNotEqual() {
        SniperSnapshot snap1 = new SniperSnapshot("item-1", 0, 0, SniperState.JOINING);
        SniperSnapshot snap2 = new SniperSnapshot("item-2", 0, 0, SniperState.JOINING);

        assertFalse(snap1.equals(snap2));
        assertFalse(snap2.equals(snap1));
    }

    @Test
    public void snapshotsWithDifferentPriceAreNotEqual() {
        SniperSnapshot snap1 = new SniperSnapshot("item-1", 0, 0, SniperState.JOINING);
        SniperSnapshot snap2 = new SniperSnapshot("item-1", 50, 0, SniperState.JOINING);

        assertFalse(snap1.equals(snap2));
        assertFalse(snap2.equals(snap1));
    }

    @Test
    public void snapshotsWithDifferentBidAreNotEqual() {
        SniperSnapshot snap1 = new SniperSnapshot("item-1", 0, 0, SniperState.JOINING);
        SniperSnapshot snap2 = new SniperSnapshot("item-1", 0, 50, SniperState.JOINING);

        assertFalse(snap1.equals(snap2));
        assertFalse(snap2.equals(snap1));
    }

    @Test
    public void snapshotsWithDifferentStateAreNotEqual() {
        SniperSnapshot snap1 = new SniperSnapshot("item-1", 0, 0, SniperState.JOINING);
        SniperSnapshot snap2 = new SniperSnapshot("item-1", 0, 0, SniperState.BIDDING);

        assertFalse(snap1.equals(snap2));
        assertFalse(snap2.equals(snap1));
    }

    @Test
    public void transitionsBetweenStates() {
        final String itemId = "item id";
        SniperSnapshot joining = SniperSnapshot.joining(itemId);
        SniperSnapshot bidding = joining.bidding(123, 234);

        assertEquals(new SniperSnapshot(itemId, 0, 0, SniperState.JOINING), joining);
        assertEquals(new SniperSnapshot(itemId, 123, 234, SniperState.BIDDING), bidding);
        assertEquals(new SniperSnapshot(itemId, 456, 234, SniperState.WINNING), bidding.winning(456));
        assertEquals(new SniperSnapshot(itemId, 123, 234, SniperState.LOST), bidding.closed());
        assertEquals(new SniperSnapshot(itemId, 678, 234, SniperState.WON), bidding.winning(678).closed());
    }

    @Test
    public void comparesItemIdentities() {
        assertTrue(SniperSnapshot.joining("item 1").isForSameItemAs(SniperSnapshot.joining("item 1")));
        assertFalse(SniperSnapshot.joining("item 1").isForSameItemAs(SniperSnapshot.joining("item 2")));
    }

}
