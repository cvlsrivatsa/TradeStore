package org.example;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;

@Data
@Builder
public class Trade {
    @NonNull
    private String tradeId;
    @NonNull
    private Integer version;
    private String counterPartyId;
    private String bookId;
    @NonNull
    private Date maturityDate;
    private Date createdDate;
    private boolean isExpired;
}
