package tutuka.compareTransactions;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

public class TransactionComparator implements Comparator<Transaction>{

	@Override
	public int compare(Transaction t1, Transaction t2) {
		//Compare Transaction ID and then Transaction Description in that order
		return ComparisonChain.start().
				compare(t1.getTransactionID(),t2.getTransactionID()).
				compare(t1.getTransactionDescription(),t2.getTransactionDescription()).
				result();
	}
	
}
