package course.concurrency.exams.auction;

public class AuctionPessimistic implements Auction {

    private final Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private Bid latestBid;

    public boolean propose(Bid bid) {

        boolean changed = false;
        synchronized (this) {
            if (this.latestBid == null) {
                this.latestBid = bid;
                return true;
            }

            if (bid.getPrice() > latestBid.getPrice()) {
                latestBid = bid;
                changed = true;
            }
        }

        if (changed) {
            notifier.sendOutdatedMessage(latestBid);
            return true;
        }

        return false;
    }

    public synchronized Bid getLatestBid() {
        return latestBid;
    }
}
