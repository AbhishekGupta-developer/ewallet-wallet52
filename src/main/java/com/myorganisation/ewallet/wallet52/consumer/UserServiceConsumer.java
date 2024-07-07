package com.myorganisation.ewallet.wallet52.consumer;

import com.myorganisation.ewallet.wallet52.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserServiceConsumer {

    Logger logger = LoggerFactory.getLogger(UserServiceConsumer.class);

    @Autowired
    WalletService walletService;

    @KafkaListener(topics = "USER_CREATED", groupId = "wallet52Grp")
    public void createNewUserWallet(String message) {
        logger.info("received Message: {}", message);
        walletService.createNewWallet(message);
    }

    @KafkaListener(topics = "USER_DELETED", groupId = "wallet52Grp")
    public void disableWalletForUser(String message) {
        logger.info("received Message: {}", message);
        walletService.disableActiveWallet(message);
    }


}
