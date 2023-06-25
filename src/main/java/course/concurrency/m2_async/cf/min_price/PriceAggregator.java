package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class PriceAggregator {

    private static final int MAX_WAITING_TIME = 2800;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);
    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        List<CompletableFuture<Double>> shopPrices = shopIds.stream()
                .map(shopId -> CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executorService)
                        .completeOnTimeout(Double.NaN, MAX_WAITING_TIME, TimeUnit.MILLISECONDS)
                        .exceptionally(throwable -> {
                            System.out.println("ERROR in async getPrice method: " + throwable.getMessage());
                            return Double.NaN;
                        }))
                .collect(Collectors.toList());

        try {
            return CompletableFuture.allOf(shopPrices.toArray(new CompletableFuture[0]))
                    .thenApply(future -> shopPrices.stream()
                            .map(CompletableFuture::join)
                            .min(Double::compareTo)
                            .orElse(Double.NaN))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
