package course.concurrency.exams.auction;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private final Notifier notifier;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private Bid latestBid = new Bid(null, null, Long.MIN_VALUE);
    private volatile boolean bidStop = false;


    public boolean propose(Bid bid) {

        boolean changed = false;

        if (bidStop) {
            return false;
        }

        synchronized (this) {
            if (bid.getPrice() > latestBid.getPrice() && !bidStop) {
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

    public Bid stopAuction() {
        bidStop = true;
        return latestBid;
    }
}
