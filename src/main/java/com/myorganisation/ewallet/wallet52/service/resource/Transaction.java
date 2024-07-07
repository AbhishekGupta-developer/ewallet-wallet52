package com.myorganisation.ewallet.wallet52.service.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    private Long senderId;
    private Long receiverId;
    private Double amount;
    private String status;
    private String transactionId;

}
