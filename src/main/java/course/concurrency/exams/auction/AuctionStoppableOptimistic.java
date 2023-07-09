package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBid = new AtomicReference<>(new Bid(null, null, Long.MIN_VALUE));
    private volatile boolean bidStop = false;

    public boolean propose(Bid bid) {
        Bid currentBid;
        Bid newBid;

        do {

            if (bidStop) {
                return false;
            }

            currentBid = newBid = this.latestBid.get();

            if (currentBid != null && bid.getPrice() > currentBid.getPrice()) {
                newBid = bid;
            }

        } while (!bidStop && !latestBid.compareAndSet(currentBid, newBid));

        notifier.sendOutdatedMessage(newBid);

        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }

    public Bid stopAuction() {
        bidStop = true;
        return latestBid.get();
    }
}
