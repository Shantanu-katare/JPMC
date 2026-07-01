package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @KafkaListener(
            topics = "trader-updates",
            groupId = "midas-group"
    )
    public void receive(Transaction transaction) {

        Long senderId = transaction.getSenderId();
        Long recipientId = transaction.getRecipientId();
        double amount = transaction.getAmount();

        UserRecord sender = userRepository.findById(senderId).orElse(null);
        UserRecord recipient = userRepository.findById(recipientId).orElse(null);

        // validation
        if (sender == null || recipient == null) {
            return;
        }

        if (sender.getBalance() < amount) {
            return;
        }

        // update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        userRepository.save(sender);
        userRepository.save(recipient);

        // save transaction
        TransactionRecord record = new TransactionRecord();
        record.setSender(sender);
        record.setRecipient(recipient);
        record.setAmount(amount);

        transactionRecordRepository.save(record);
    }
}
