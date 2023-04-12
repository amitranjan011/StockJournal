package com.amit.journal.constants;

public interface Constants {
    String ID = "_id";
    String USERID_HEADER = "userId";
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

    double RETURN_PERCENT_UNSOLD_UNREALISED = 99999999;
//    double HOLDING_SOLD_UNREALIZED_RETURN_PERCENT = 99999999;
    String BSE_EXTENSION = ".BO";
    String NSE_EXTENSION = ".NS";
}
