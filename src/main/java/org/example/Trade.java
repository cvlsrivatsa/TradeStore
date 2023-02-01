package org.example;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;

@Getter
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
    @Setter
    private Date createdDate;
    @Setter
    private boolean isExpired;
}
