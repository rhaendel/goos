package test.auctionsniper;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.AuctionSniper;
import auctionsniper.Item;
import auctionsniper.SniperCollector;
import auctionsniper.SniperLauncher;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class SniperLauncherTest {

    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();
    private final States auctionState = context.states("auction state").startsAs("not joined");
    private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
    private final Auction auction = context.mock(Auction.class);
    private final SniperCollector sniperCollector = context.mock(SniperCollector.class);

    private final SniperLauncher launcher = new SniperLauncher(auctionHouse, sniperCollector);

    @Test
    public void addsNewSniperToCollectorAndThenJoinsAuction() {
        final Item item =  new Item("item-123", 456);
        context.checking(new Expectations() {{
            allowing(auctionHouse).auctionFor(item); will(returnValue(auction));

            oneOf(auction).addAuctionEventListener(with(sniperForItem(item)));
                    when(auctionState.is("not joined"));
            oneOf(sniperCollector).addSniper(with(sniperForItem(item)));
                    when(auctionState.is("not joined"));

            oneOf(auction).join(); then(auctionState.is("joined"));
        }});

        launcher.joinAuction(item);
    }

    private Matcher<AuctionSniper> sniperForItem(Item item) {
        return new FeatureMatcher<AuctionSniper, String>(equalTo(item.identifier), "a sniper with a snapshot for item", "snapshot item") {
            @Override
            protected String featureValueOf(AuctionSniper actual) {
                return actual.getSnapshot().itemId;
            }
        };
    }

}
