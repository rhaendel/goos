package test.auctionsniper;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;

@SuppressWarnings("deprecation")
@RunWith(JMock.class)
public class AuctionSniperTest {

    private final Mockery context = new Mockery();
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);

    @Test
    public void reportsLostWhenAuctionCloses() {
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperLost();
            }
        });

        sniper.auctionClosed();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        context.checking(new Expectations() {
            {
                oneOf(auction).bid(price + increment);
                atLeast(1).of(sniperListener).sniperBidding();
            }
        });

        sniper.currentPrice(price, increment, PriceSource.FromSniper);
    }
}
