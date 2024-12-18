package org.example.ecommercefashion.dtos.response;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Data
public class BankTransactionResponse {

    private int status;
    private String error;
    private Messages messages;
    private List<Transaction> transactions;

    @Data
    public static class Messages {
        private boolean success;
    }

    @Data
    public static class Transaction {
        private String id;

        @JsonProperty("bank_brand_name")
        private String bankBrandName;

        @JsonProperty("account_number")
        private String accountNumber;

        @JsonProperty("transaction_date")
        private String transactionDate;

        @JsonProperty("amount_out")
        private String amountOut;

        @JsonProperty("amount_in")
        private Double amountIn;

        private String accumulated;

        @JsonProperty("transaction_content")
        private String transactionContent;

        @JsonProperty("reference_number")
        private String referenceNumber;

        private String code;

        @JsonProperty("sub_account")
        private String subAccount;

        @JsonProperty("bank_account_id")
        private String bankAccountId;
    }
}

