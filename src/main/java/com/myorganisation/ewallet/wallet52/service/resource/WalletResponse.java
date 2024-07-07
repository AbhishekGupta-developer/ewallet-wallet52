package com.myorganisation.ewallet.wallet52.service.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletResponse {

    private String userId;
    private String balance;
    private String type;
    private LocalDateTime lastUpdated;

}
