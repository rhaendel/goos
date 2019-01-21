package test.auctionsniper;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class AuctionSniperTest {

    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);
    private final States sniperState = context.states("sniper");

    @Test
    public void reportsLostWhenAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperLost();
        }});

        sniper.auctionClosed();
    }

    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperBidding();
            then(sniperState.is("bidding"));

            atLeast(1).of(sniperListener).sniperLost();
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        context.checking(new Expectations() {{
            oneOf(auction).bid(price + increment);
            atLeast(1).of(sniperListener).sniperBidding();
        }});

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperWinning();
        }});

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
    }

    @Test
    public void reportsIsBiddingAndLostAfterWinningButAPriceComesFromOtherBidder() {
        context.checking(new Expectations() {{
            ignoring(auction);
            atLeast(1).of(sniperListener).sniperWinning();
            then(sniperState.is("winning"));

            atLeast(1).of(sniperListener).sniperBidding();
            when(sniperState.is("winning"));
            then(sniperState.is("bidding"));

            atLeast(1).of(sniperListener).sniperLost();
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.currentPrice(168, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    public void reportsIsWinningAndWonAfterBiddingHigherThanOtherBidder() {
        context.checking(new Expectations() {{
            ignoring(auction);
            atLeast(1).of(sniperListener).sniperBidding();
            then(sniperState.is("bidding"));

            atLeast(1).of(sniperListener).sniperWinning();
            when(sniperState.is("bidding"));
            then(sniperState.is("winning"));

            atLeast(1).of(sniperListener).sniperWon();
            when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(168, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperWinning();
            then(sniperState.is("winning"));

            atLeast(1).of(sniperListener).sniperWon();
            when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }
}
