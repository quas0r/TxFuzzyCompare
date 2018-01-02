package tutuka.compareTransactions;

import java.time.format.DateTimeParseException;

public class TransactionDateException extends  DateTimeParseException {
    public TransactionDateException(String message, String parsedData) {
        super(message, parsedData, 0);
    }

    public TransactionDateException(String message, String parsedData, Throwable cause) {
    	super(message, parsedData, 0, cause);
    }
}
