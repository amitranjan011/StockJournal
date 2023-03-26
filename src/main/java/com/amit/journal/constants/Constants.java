package com.amit.journal.constants;

public interface Constants {
    String TRANSACTION_TYPE_BUY = "BUY";
    String TRANSACTION_TYPE_SELL = "SELL";
    String USERID = "userId";
    String SYMBOL = "symbol";
    String ENTRY_DATE = "entryDate";

    String TRANSACTION_DATE = "transactionDate";
    String EXIT_DATE = "sellDate";
    String POSITION_STATUS_OPEN = "Open";
    String POSITION_STATUS_CLOSED = "Closed";

    String POSITION_STATUS_key = "positionStatus";

    String TRANSACTIONS_DIR = "transactions";
    String HOLDING_DIR = "holding";
    String HOLDING_DATE = "date";

    double HOLDING_UNSOLD_RETURN_PERCENT = 99999999;
}
