package test.auctionsniper;

import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.SniperState;

public class AuctionSniperTest {

    protected static final String ITEM_ID = "item-54321";

    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();

    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener, ITEM_ID);
    private final States sniperState = context.states("sniper");

    @Test
    public void reportsLostIfAuctionClosesImmediately() {
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperLost();
            }
        });

        sniper.auctionClosed();
    }

    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {
            {
                ignoring(auction);
                allowing(sniperListener).sniperBidding(with(any(SniperState.class)));
                then(sniperState.is("bidding"));

                atLeast(1).of(sniperListener).sniperLost();
                when(sniperState.is("bidding"));
            }
        });

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;

        context.checking(new Expectations() {
            {
                oneOf(auction).bid(bid);
                atLeast(1).of(sniperListener).sniperBidding(new SniperState(ITEM_ID, price, bid));
            }
        });

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperWinning();
            }
        });

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
    }

    @Test
    public void reportsIsBiddingAndLostAfterWinningButAPriceComesFromOtherBidder() {
        context.checking(new Expectations() {
            {
                ignoring(auction);
                atLeast(1).of(sniperListener).sniperWinning();
                then(sniperState.is("winning"));

                atLeast(1).of(sniperListener).sniperBidding(new SniperState(ITEM_ID, 168, 213));
                when(sniperState.is("winning"));
                then(sniperState.is("bidding"));

                atLeast(1).of(sniperListener).sniperLost();
                when(sniperState.is("bidding"));
            }
        });

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.currentPrice(168, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    public void reportsIsWinningAndWonAfterBiddingHigherThanOtherBidder() {
        context.checking(new Expectations() {
            {
                ignoring(auction);
                atLeast(1).of(sniperListener).sniperBidding(new SniperState(ITEM_ID, 123, 168));
                then(sniperState.is("bidding"));

                atLeast(1).of(sniperListener).sniperWinning();
                when(sniperState.is("bidding"));
                then(sniperState.is("winning"));

                atLeast(1).of(sniperListener).sniperWon();
                when(sniperState.is("winning"));
            }
        });

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(168, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {
            {
                ignoring(auction);
                allowing(sniperListener).sniperWinning();
                then(sniperState.is("winning"));

                atLeast(1).of(sniperListener).sniperWon();
                when(sniperState.is("winning"));
            }
        });

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }
}
