package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.StampedLock;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    StampedLock lock = new StampedLock();

    private final AtomicReference<Bid> latestBid = new AtomicReference<>();

    public boolean propose(Bid bid) {
        boolean exchanged = false;
        Bid currentBid;
        Bid newBid;

        do {
            currentBid = newBid = this.latestBid.get();

            if (currentBid != null && bid.getPrice() > currentBid.getPrice()) {
                exchanged = true;
                newBid = bid;
            }

            if (currentBid == null) {
                newBid = bid;
            }

        } while (!latestBid.compareAndSet(currentBid, newBid));


        if (exchanged) {
            notifier.sendOutdatedMessage(newBid);
            return true;
        }

        return false;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}

