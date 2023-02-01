package org.example;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Store {
    private final Map<String, TreeMap<Integer, Trade>> tradeMap = new HashMap<>();
    private final Lock lock = new ReentrantLock();

    public Store() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime nextRun = now.withHour(0).withMinute(0).withSecond(0);
        if (now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusDays(1);
        }
        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::updateExpiry,
                initialDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);
    }

    public Trade getTrade(String tradeId, int version) {
        TreeMap<Integer, Trade> trades = tradeMap.getOrDefault(tradeId, null);
        if (trades == null) {
            return null;
        }
        return trades.getOrDefault(version, null);
    }

    public List<Trade> getTrades(String tradeId) {
        return Optional.ofNullable(tradeMap.getOrDefault(tradeId, null)).stream()
                .flatMap(o -> o.values().stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void saveTrade(final Trade trade) {
        Date nowDate = new Date();
        if (trade.getMaturityDate().before(nowDate)) {
            return;
        }
        lock.lock();
        try {
            TreeMap<Integer, Trade> versionMap = tradeMap.computeIfAbsent(trade.getTradeId(), k -> new TreeMap<>());
            if (!versionMap.isEmpty() && versionMap.lastKey() > trade.getVersion()) {
                throw new RuntimeException("BadTradeVersionException");
            }
            trade.setCreatedDate(nowDate);
            trade.setExpired(false);
            versionMap.put(trade.getVersion(), trade);
        } finally {
            lock.unlock();
        }
    }

    private void updateExpiry() {
        lock.lock();
        try {
            Date nowDate = new Date();
            tradeMap.values()
                    .forEach(o -> o.values().stream()
                            .filter(v -> v.getMaturityDate().before(nowDate))
                            .forEach(t -> t.setExpired(true)));
        } finally {
            lock.unlock();
        }
    }
}