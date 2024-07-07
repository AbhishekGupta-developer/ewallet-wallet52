package com.myorganisation.ewallet.wallet52.controller;

import com.myorganisation.ewallet.wallet52.domain.Wallet;
import com.myorganisation.ewallet.wallet52.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    WalletService walletService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserWallet(@PathVariable String userId) {
        Wallet wallet = walletService.getUserWallet(userId);
        return new ResponseEntity<>(wallet.toResponse(), HttpStatus.OK);
    }
}
