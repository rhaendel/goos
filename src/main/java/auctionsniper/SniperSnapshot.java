package auctionsniper;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class SniperSnapshot {

    public final String itemId;
    public final int lastPrice;
    public final int lastBid;

    public SniperSnapshot(String itemId, int lastPrice, int lastBid) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(itemId).append(lastPrice).append(lastBid).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof SniperSnapshot) {
            final SniperSnapshot other = (SniperSnapshot) obj;
            return new EqualsBuilder().append(itemId, other.itemId).append(lastPrice, other.lastPrice).append(lastBid, other.lastBid)
                    .isEquals();
        }
        return false;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append(itemId).append(lastPrice).append(lastBid).toString();
    }
}
