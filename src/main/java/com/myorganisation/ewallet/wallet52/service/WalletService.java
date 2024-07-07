package com.myorganisation.ewallet.wallet52.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myorganisation.ewallet.wallet52.domain.Wallet;
import com.myorganisation.ewallet.wallet52.service.resource.Transaction;

public interface WalletService {

    Wallet getUserWallet(String userId);
    Wallet createNewWallet(String userId);
    Wallet disableActiveWallet(String userId);
    void updateWallet(Transaction transaction) throws JsonProcessingException;
}
