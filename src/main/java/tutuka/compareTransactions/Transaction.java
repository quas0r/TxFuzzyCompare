package tutuka.compareTransactions;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class Transaction {
	
	private String profileName;
	private LocalDateTime transactionDate;
	private Long transactionAmount;
	private String transactionNarrative;
	private String transactionDescription;
	private BigInteger transactionID;
	private int transactionType;
	private String walletReference;
	
	//Useful in reducing the Cartesian cross matching complexity [Still O(n^2) though]
	private boolean isMatched;
	
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}
	public Long getTransactionAmount() {
		return transactionAmount;
	}
	public void setTransactionAmount(Long transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	public String getTransactionNarrative() {
		return transactionNarrative;
	}
	public void setTransactionNarrative(String transactionNarrative) {
		this.transactionNarrative = transactionNarrative;
	}
	public String getTransactionDescription() {
		return transactionDescription;
	}
	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}
	public BigInteger getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(BigInteger transactionID) {
		this.transactionID = transactionID;
	}
	public int getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}
	public String getWalletReference() {
		return walletReference;
	}
	public void setWalletReference(String walletReference) {
		this.walletReference = walletReference;
	}
	public boolean isMatched() {
		return isMatched;
	}
	public void setMatched(boolean isMatched) {
		this.isMatched = isMatched;
	}

}
