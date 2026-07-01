package com.jpmc.midascore;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class BalanceService {

    private final ConcurrentHashMap<Long, Float> balances = new ConcurrentHashMap<>();

    public void updateBalance(Long senderId, Long recipientId, Float amount) {
        balances.put(senderId, balances.getOrDefault(senderId, 0f) - amount);
        balances.put(recipientId, balances.getOrDefault(recipientId, 0f) + amount);
    }

    public Float getBalance(Long userId) {
        return balances.getOrDefault(userId, 0f);
    }
}
