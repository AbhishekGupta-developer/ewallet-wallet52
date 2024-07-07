package com.myorganisation.ewallet.wallet52.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorganisation.ewallet.wallet52.domain.Wallet;
import com.myorganisation.ewallet.wallet52.repository.WalletRepository;
import com.myorganisation.ewallet.wallet52.service.WalletService;
import com.myorganisation.ewallet.wallet52.service.resource.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Random;

@Service
public class WalletServiceImpl implements WalletService {

    Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    KafkaTemplate kafkaTemplate;

    private final String wallet_update_topic = "WALLET_UPDATE";

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public Wallet getUserWallet(String userId) {
        return walletRepository.findByUserId(Long.valueOf(userId));
    }

    @Override
    public Wallet createNewWallet(String userId) {
        Wallet wallet = new Wallet();
        wallet.setUserId(Long.valueOf(userId));
        wallet.setBalance(0.0);
        wallet.setActive(true);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet disableActiveWallet(String userId) {
        Wallet wallet = walletRepository.findByUserId(Long.valueOf(userId));
        if(Objects.nonNull(wallet)) {
            wallet.setActive(false);
            // if balance is not zero, trigger bank transaction
            return walletRepository.save(wallet);
        }

        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = NullPointerException.class, noRollbackFor = ArithmeticException.class)
    public void updateWallet(Transaction transaction) throws JsonProcessingException {
        try {
            if (Objects.nonNull(transaction) && transaction.getSenderId().equals(-99L)) {

                Wallet receiverWallet = walletRepository.findByUserId(transaction.getReceiverId());
                if (Objects.isNull(receiverWallet)) {
                    logger.error("Invalid receiver Id");
                }
                updateUserWallet(receiverWallet, transaction.getAmount());

            } else if (Objects.nonNull(transaction) && transaction.getReceiverId().equals(-99L)) {
                Wallet senderWallet = walletRepository.findByUserId(transaction.getSenderId());
                if (Objects.isNull(senderWallet)) {
                    logger.error("Invalid sender Id");
                }
                updateUserWallet(senderWallet, -1 * transaction.getAmount());
            } else if (Objects.nonNull(transaction)) {
                Wallet receiverWallet = walletRepository.findByUserId(transaction.getReceiverId());
                if (Objects.isNull(receiverWallet)) {
                    logger.error("Invalid receiver Id");
                }
                Wallet senderWallet = walletRepository.findByUserId(transaction.getSenderId());
                if (Objects.isNull(senderWallet)) {
                    logger.error("Invalid sender Id");
                }
                performTransaction(senderWallet, receiverWallet, transaction.getAmount());
            } else {
                logger.error("Invalid transaction status!");
            }
            transaction.setStatus("SUCCESS");
        } catch(Exception ex) {
            transaction.setStatus("FAILURE");
        } finally {
            kafkaTemplate.send(wallet_update_topic, mapper.writeValueAsString(transaction));
        }
    }

//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = NullPointerException.class)
    private void performTransaction(Wallet senderWallet, Wallet receiverWallet, Double amount) {
        try {
            Wallet senderWalletCopy = new Wallet();
            Wallet receiverWalletCopy = new Wallet();
            BeanUtils.copyProperties(receiverWallet, receiverWalletCopy);
            BeanUtils.copyProperties(senderWallet, senderWalletCopy);

            logger.info("starting ransaction between sender {} and receiver {}", senderWallet.getUserId(), receiverWallet.getUserId());

            senderWalletCopy.setBalance(senderWallet.getBalance() - amount);
            receiverWalletCopy.setBalance(receiverWallet.getBalance() + amount);

            walletRepository.save(senderWalletCopy);
            walletRepository.save(receiverWalletCopy);
        } catch(Exception ex) {
            logger.error("Exception while updating balance");
//            walletRepository.save(receiverWallet);
//            walletRepository.save(senderWallet);
            throw ex;
        }
    }

    private void updateUserWallet(Wallet receiverWallet, Double amount) {
        try {
            Wallet receiverWalletCopy = new Wallet();
            BeanUtils.copyProperties(receiverWallet, receiverWalletCopy);
            receiverWalletCopy.setBalance(receiverWallet.getBalance() + amount);
            walletRepository.save(receiverWalletCopy);
        } catch(Exception ex) {
            logger.error("Exception while updating balance");
            walletRepository.save(receiverWallet);
        }
    }


    /**
     *
     * Identify the type load/withdraw/transfer
     * if sender and receiver are valid
     *
     * 1. Load money
     *  sender -99
     *  receiver user's wallet
     *  update user wallet with amount in transaction
     *
     * 2. withdraw money
     *  sender user's wallet
     *  receiver is -99
     *  update user wallet with amount in transaction
     *
     * 3. Transfer
     *  check if sender has sufficient balance in wallet
     *  deduct the amount from sender's wallet
     *  add the amount in the receiver's wallet
     *
     *  in case of any failure rollback the state of both wallets
     *
     */
}
