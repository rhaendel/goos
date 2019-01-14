package test.auctionsniper;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class AuctionSniperTest {

    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(sniperListener);

    @Test
    public void reportsLostWhenAuctionCloses() {
        context.checking(new Expectations() {{
            oneOf(sniperListener).sniperLost();
        }});

        sniper.auctionClosed();
    }
}
