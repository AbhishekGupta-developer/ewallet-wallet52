package com.myorganisation.ewallet.wallet52.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorganisation.ewallet.wallet52.service.WalletService;
import com.myorganisation.ewallet.wallet52.service.resource.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class TransactionServiceConsumer {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    WalletService walletService;

    Logger logger = LoggerFactory.getLogger(TransactionServiceConsumer.class);

    @KafkaListener(topics = "TRANS_CREATED", groupId = "transactionGroup")
    public void handleTransactions(String message) {
        logger.info("transaction message received {}", message);
        try {
            Transaction transaction = mapper.readValue(message, Transaction.class);
            walletService.updateWallet(transaction);
        } catch(JsonProcessingException ex) {
            logger.error("error in deserialization the json message");
        }
    }
}
