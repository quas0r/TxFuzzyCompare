package tutuka.compareTransactions;

import java.math.BigInteger;

public class TransactionReportWithScore {
	private String File1Name;
	private String profileName1;
	private String transactionDate1;
	private Long transactionAmount1;
	private String transactionNarrative1;
	private String transactionDescription1;
	private BigInteger transactionID1;
	private int transactionType1;
	private String walletReference1;
	
	private String File2Name;
	private String profileName2;
	private String transactionDate2;
	private Long transactionAmount2;
	private String transactionNarrative2;
	private String transactionDescription2;
	private BigInteger transactionID2;
	private int transactionType2;
	private String walletReference2;
	
	/*
	 * Used to flag unmatched transactions as well as to reduce the no of cross matching 
	 * attempts once matched with a transaction of another file
	*/ 
	private double matchScore;
	
	private StringBuilder Reasons  = new StringBuilder("");
		
	private Enum<Result> Status;
	
	public String getFile1Name() {
		return File1Name;
	}

	public void setFile1Name(String file1Name) {
		File1Name = file1Name;
	}

	public String getProfileName1() {
		return profileName1;
	}

	public void setProfileName1(String profileName1) {
		this.profileName1 = profileName1;
	}

	public String getTransactionDate1() {
		return transactionDate1;
	}

	public void setTransactionDate1(String transactionDate1) {
		this.transactionDate1 = transactionDate1;
	}

	public Long getTransactionAmount1() {
		return transactionAmount1;
	}

	public void setTransactionAmount1(Long transactionAmount1) {
		this.transactionAmount1 = transactionAmount1;
	}

	public String getTransactionNarrative1() {
		return transactionNarrative1;
	}

	public void setTransactionNarrative1(String transactionNarrative1) {
		this.transactionNarrative1 = transactionNarrative1;
	}

	public String getTransactionDescription1() {
		return transactionDescription1;
	}

	public void setTransactionDescription1(String transactionDescription1) {
		this.transactionDescription1 = transactionDescription1;
	}

	public BigInteger getTransactionID1() {
		return transactionID1;
	}

	public void setTransactionID1(BigInteger transactionID1) {
		this.transactionID1 = transactionID1;
	}

	public int getTransactionType1() {
		return transactionType1;
	}

	public void setTransactionType1(int transactionType1) {
		this.transactionType1 = transactionType1;
	}

	public String getWalletReference1() {
		return walletReference1;
	}

	public void setWalletReference1(String walletReference1) {
		this.walletReference1 = walletReference1;
	}



	public String getFile2Name() {
		return File2Name;
	}

	public void setFile2Name(String file2Name) {
		File2Name = file2Name;
	}

	public String getProfileName2() {
		return profileName2;
	}

	public void setProfileName2(String profileName2) {
		this.profileName2 = profileName2;
	}

	public String getTransactionDate2() {
		return transactionDate2;
	}

	public void setTransactionDate2(String transactionDate2) {
		this.transactionDate2 = transactionDate2;
	}

	public Long getTransactionAmount2() {
		return transactionAmount2;
	}

	public void setTransactionAmount2(Long transactionAmount2) {
		this.transactionAmount2 = transactionAmount2;
	}

	public String getTransactionNarrative2() {
		return transactionNarrative2;
	}

	public void setTransactionNarrative2(String transactionNarrative2) {
		this.transactionNarrative2 = transactionNarrative2;
	}

	public String getTransactionDescription2() {
		return transactionDescription2;
	}

	public void setTransactionDescription2(String transactionDescription2) {
		this.transactionDescription2 = transactionDescription2;
	}

	public BigInteger getTransactionID2() {
		return transactionID2;
	}

	public void setTransactionID2(BigInteger transactionID2) {
		this.transactionID2 = transactionID2;
	}

	public int getTransactionType2() {
		return transactionType2;
	}

	public void setTransactionType2(int transactionType2) {
		this.transactionType2 = transactionType2;
	}

	public String getWalletReference2() {
		return walletReference2;
	}

	public void setWalletReference2(String walletReference2) {
		this.walletReference2 = walletReference2;
	}

	public double getMatchScore() {
		return matchScore;
	}

	public void setMatchScore(double matchScore) {
		this.matchScore = matchScore;
	}

	public Enum<Result> getStatus() {
		return Status;
	}

	public void setStatus(Enum<Result> status) {
		Status = status;
	}

	public StringBuilder getReasons() {
		return Reasons;
	}

	public void setReasons(StringBuilder reasons) {
		Reasons = reasons;
	}
}