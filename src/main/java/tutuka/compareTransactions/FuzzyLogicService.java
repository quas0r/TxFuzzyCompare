package tutuka.compareTransactions;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tutuka.FileLoader;
import tutuka.utils.SimilarityUtil;

@Service
public class FuzzyLogicService {

	@Autowired
	private FuzzyLogicProperties fuzzyprop;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	SimilarityUtil simUtil = new SimilarityUtil();
	
	public List<TransactionReportWithScore> fuzzyLogicMatch(List<Transaction> tutukaTransactionList,
			List<Transaction> clientTransactionList, FileLoader fileLoader) {
		
		Collections.sort(tutukaTransactionList, new TransactionComparator());
		Collections.sort(clientTransactionList, new TransactionComparator());
		logger.info("Sorted the transactions Lists");
		List<TransactionReportWithScore> reportList = new ArrayList<>();
		for (Transaction tutukaTx : tutukaTransactionList) {
			logger.debug("Inside tutuka FOR LOOP   " + tutukaTx.getTransactionID() + " | "
					+ tutukaTx.getTransactionDescription());
			for (Transaction clientTx : clientTransactionList) {
				TransactionReportWithScore txReport = new TransactionReportWithScore();
				if (!clientTx.isMatched()) {

					logger.debug("Inside client FOR LOOP   " + clientTx.getTransactionID() + " | "
							+ clientTx.getTransactionDescription());

					String clientSetIdentifier = clientTx.getTransactionID().toString() + "|"
							+ clientTx.getTransactionDescription();

					logger.debug("clientSetIdentifier : " + clientSetIdentifier);

					logger.debug("INSIDE CLIENT and BOTH NOT DUPLICATE | TUTUKA : " + tutukaTx.getTransactionID()
							+ " | " + tutukaTx.getTransactionDescription() + " CLIENT: " + clientTx.getTransactionID()
							+ " | " + clientTx.getTransactionDescription());
					
					//The List is already sorted according to Transaction ID,Transaction Description
					if (tutukaTx.getTransactionID().compareTo(clientTx.getTransactionID()) <= 0
							&& !clientTx.isMatched()) {
						logger.debug("Inside isMatched LOOP");
						if (tutukaTx.getTransactionID().equals(clientTx.getTransactionID())) {
							if (tutukaTx.getTransactionDescription().equals(clientTx.getTransactionDescription())) {
								tutukaTx.setMatched(true);
								clientTx.setMatched(true);
								// WalletReference
								logger.debug("Begining Score Calculation for Transaction ID : "
										+ tutukaTx.getTransactionID() + " from File1 and " + clientTx.getTransactionID()
										+ " from second File");
								// StringUtils does null safe equals
								if (StringUtils.equals(tutukaTx.getWalletReference(), clientTx.getWalletReference())) {
									txReport.setMatchScore(txReport.getMatchScore() + fuzzyprop.valid_incr);
								} else {
									txReport.getReasons().append("| WalletReference Mismatch |");
								}

								/*
								 * TransactionDate The max time zone difference across Africa(In case the client
								 * reports a local Datetime) is 3hrs = 180min
								 */
								if ((tutukaTx.getTransactionDate() == null) ^ (clientTx.getTransactionDate() == null)) {
									txReport.getReasons().append("| TransactionDate Mismatch |");
								} else if (Objects.equals(tutukaTx.getTransactionDate(),
										clientTx.getTransactionDate())) {
									txReport.setMatchScore(txReport.getMatchScore() + fuzzyprop.valid_incr);
								} else if (Math.abs(
										Duration.between(tutukaTx.getTransactionDate(), clientTx.getTransactionDate())
												.toMinutes()) <= 180) {
									txReport.setMatchScore(txReport.getMatchScore() + fuzzyprop.fuzzy_incr);
									txReport.getReasons().append("| TransactionDate Fuzzy match |");
								} else {
									txReport.getReasons().append("| TransactionDate Mismatch |");
								}

								// TransactionAmount
								if ((tutukaTx.getTransactionAmount() == null) ^ (clientTx.getTransactionAmount() == null)) {
									txReport.getReasons().append("| TransactionAmount Mismatch |");
								} else if (Objects.equals(tutukaTx.getTransactionAmount(),
										clientTx.getTransactionAmount())) {
									txReport.setMatchScore(txReport.getMatchScore() + fuzzyprop.valid_incr);
								} else if (Math.abs(tutukaTx.getTransactionAmount()) == Math
										.abs(clientTx.getTransactionAmount())) {
									txReport.setMatchScore(txReport.getMatchScore() + fuzzyprop.fuzzy_incr);
									txReport.getReasons().append("| TransactionAmount Fuzzy match |");
								} else {
									txReport.getReasons().append("| TransactionAmount Mismatch |");
								}

								// TransactionType
								if (tutukaTx.getTransactionType() == clientTx.getTransactionType()) {
									txReport.setMatchScore(txReport.getMatchScore() + fuzzyprop.valid_incr);
								} else {
									txReport.getReasons().append("| TransactionType Mismatch |");
								}

								// ProfileName
								if ((tutukaTx.getProfileName() == null) ^ (clientTx.getProfileName() == null)) {
									txReport.getReasons().append("| ProfileName Mismatch |");
								} else if (Objects.equals(tutukaTx.getProfileName(), clientTx.getProfileName())) {
									txReport.setMatchScore(txReport.getMatchScore() + fuzzyprop.valid_incr);
								} else {
									txReport.getReasons().append("| ProfileName Mismatch |");
								}

								// TransactionNarrative
								if ((tutukaTx.getTransactionNarrative() == null)
										^ (clientTx.getTransactionNarrative() == null)) {
									txReport.getReasons().append("| TransactionNarrative Mismatch |");
								} else if (Objects.equals(tutukaTx.getTransactionNarrative(),
										clientTx.getTransactionNarrative())) {
									txReport.setMatchScore(txReport.getMatchScore() + fuzzyprop.tx_nrtv_valid_incr);
								} else {
									double similarityScore = simUtil.getSimilarityScore(
											tutukaTx.getTransactionNarrative(), clientTx.getTransactionNarrative());
									if (similarityScore > fuzzyprop.match_tolerance) {
										txReport.setMatchScore(txReport.getMatchScore() + fuzzyprop.tx_nrtv_valid_incr);
									} else if (similarityScore > fuzzyprop.probable_match_tolerance) {
										txReport.setMatchScore(
												txReport.getMatchScore() + fuzzyprop.tx_nrtv_probable_match_incr);
										txReport.getReasons().append("| TransactionNarrative Fuzzy Match |");
									} else if (similarityScore > fuzzyprop.probable_mismatch_tolerance) {
										txReport.setMatchScore(
												txReport.getMatchScore() + fuzzyprop.tx_nrtv_probable_mismatch_incr);
										txReport.getReasons().append("| TransactionNarrative Fuzzy Mismatch |");
									} else {
										txReport.getReasons().append("| TransactionNarrative Mismatch |");
									}
								}
								txReport.setFile1Name(fileLoader.getFileOne().getOriginalFilename());
								txReport.setProfileName1(tutukaTx.getProfileName());
								txReport.setTransactionAmount1(tutukaTx.getTransactionAmount());
								if (!(tutukaTx.getTransactionDate() == null)) {
									txReport.setTransactionDate1(tutukaTx.getTransactionDate().format(formatter));
								}
								txReport.setTransactionDescription1(tutukaTx.getTransactionDescription());
								txReport.setTransactionID1(tutukaTx.getTransactionID());
								txReport.setTransactionNarrative1(tutukaTx.getTransactionNarrative());
								txReport.setTransactionType1(tutukaTx.getTransactionType());
								txReport.setWalletReference1(tutukaTx.getWalletReference());
								txReport.setFile2Name(fileLoader.getFileTwo().getOriginalFilename());
								txReport.setProfileName2(clientTx.getProfileName());
								txReport.setTransactionAmount2(clientTx.getTransactionAmount());
								if (!(clientTx.getTransactionDate() == null)) {
									txReport.setTransactionDate2(clientTx.getTransactionDate().format(formatter));
								}
								txReport.setTransactionDescription2(clientTx.getTransactionDescription());
								txReport.setTransactionID2(clientTx.getTransactionID());
								txReport.setTransactionNarrative2(clientTx.getTransactionNarrative());
								txReport.setTransactionType2(clientTx.getTransactionType());
								txReport.setWalletReference2(clientTx.getWalletReference());

								Double score = txReport.getMatchScore();
								int scoreBitSum = ((score == fuzzyprop.total_score) ? 16 : 0)
										+ ((score >= fuzzyprop.permissible_match_lower && score < fuzzyprop.total_score)
												? 8
												: 0)
										+ ((score >= fuzzyprop.probable_match_lower
												&& score < fuzzyprop.permissible_match_lower) ? 4 : 0)
										+ ((score >= fuzzyprop.probable_mismatch_lower
												&& score < fuzzyprop.probable_match_lower) ? 2 : 0)
										+ ((score >= 0.0 && score < fuzzyprop.probable_mismatch_lower) ? 1 : 0);
								switch (scoreBitSum) {
								case 0:
								case 1:
									txReport.setStatus(Result.PERFECT_MISMATCH);
									break;
								case 2:
									txReport.setStatus(Result.PROBABLE_MISMATCH);
									break;
								case 4:
									txReport.setStatus(Result.PROBABLE_MATCH);
									break;
								case 8:
									txReport.setStatus(Result.PERMISSIBLE_MATCH);
									break;
								case 16:
									txReport.setStatus(Result.PERFECT_MATCH);
									break;
								}

								reportList.add(txReport);
							}
							break;
						}
					}
				}
			}

			if (!tutukaTx.isMatched()) {
				TransactionReportWithScore unmatchedTutukaReportRecord = new TransactionReportWithScore();
				unmatchedTutukaReportRecord.setFile1Name(fileLoader.getFileOne().getOriginalFilename());
				unmatchedTutukaReportRecord.setProfileName1(tutukaTx.getProfileName());
				unmatchedTutukaReportRecord.setTransactionAmount1(tutukaTx.getTransactionAmount());
				unmatchedTutukaReportRecord.setTransactionDate1(tutukaTx.getTransactionDate().format(formatter));
				unmatchedTutukaReportRecord.setTransactionDescription1(tutukaTx.getTransactionDescription());
				unmatchedTutukaReportRecord.setTransactionID1(tutukaTx.getTransactionID());
				unmatchedTutukaReportRecord.setTransactionNarrative1(tutukaTx.getTransactionNarrative());
				unmatchedTutukaReportRecord.setTransactionType1(tutukaTx.getTransactionType());
				unmatchedTutukaReportRecord.setWalletReference1(tutukaTx.getWalletReference());
				unmatchedTutukaReportRecord.setMatchScore(0);
				unmatchedTutukaReportRecord.setStatus(Result.UNMATCHED);
				unmatchedTutukaReportRecord.setReasons(new StringBuilder("Unmatched Transaction in File1"));
				reportList.add(unmatchedTutukaReportRecord);
			}

		}

		for (Transaction unMatchedClientTx : clientTransactionList) {
			TransactionReportWithScore unmatchedClientReportRecord = new TransactionReportWithScore();
			if (!unMatchedClientTx.isMatched()) {
				unmatchedClientReportRecord.setFile2Name(fileLoader.getFileTwo().getOriginalFilename());
				unmatchedClientReportRecord.setProfileName2(unMatchedClientTx.getProfileName());
				unmatchedClientReportRecord.setTransactionAmount2(unMatchedClientTx.getTransactionAmount());
				unmatchedClientReportRecord
						.setTransactionDate2(unMatchedClientTx.getTransactionDate().format(formatter));
				unmatchedClientReportRecord.setTransactionDescription2(unMatchedClientTx.getTransactionDescription());
				unmatchedClientReportRecord.setTransactionID2(unMatchedClientTx.getTransactionID());
				unmatchedClientReportRecord.setTransactionNarrative2(unMatchedClientTx.getTransactionNarrative());
				unmatchedClientReportRecord.setTransactionType2(unMatchedClientTx.getTransactionType());
				unmatchedClientReportRecord.setWalletReference2(unMatchedClientTx.getWalletReference());
				unmatchedClientReportRecord.setMatchScore(0);
				unmatchedClientReportRecord.setStatus(Result.UNMATCHED);
				unmatchedClientReportRecord.setReasons(new StringBuilder("Unmatched Transaction in File2"));
				reportList.add(unmatchedClientReportRecord);
			}

		}

		for (TransactionReportWithScore reportRecord : reportList) {
			logger.debug(reportRecord.getFile1Name() + " | " + reportRecord.getProfileName1() + " | "
					+ reportRecord.getTransactionDate1() + " | " + reportRecord.getTransactionDescription2() + " | "
					+ reportRecord.getTransactionNarrative1() + " | " + reportRecord.getTransactionType1() + " | "
					+ reportRecord.getTransactionID1() + " | " + reportRecord.getTransactionAmount1() + " | "
					+ reportRecord.getWalletReference1() + " | \n" + reportRecord.getFile2Name() + " | "
					+ reportRecord.getProfileName2() + " | " + reportRecord.getTransactionDate2() + " | "
					+ reportRecord.getTransactionDescription2() + " | " + reportRecord.getTransactionNarrative2()
					+ " | " + reportRecord.getTransactionType2() + " | " + reportRecord.getTransactionID2() + " | "
					+ reportRecord.getTransactionAmount2() + " | " + reportRecord.getWalletReference2() + " | "
					+ reportRecord.getMatchScore() + " | " + reportRecord.getReasons() + " | "
					+ reportRecord.getStatus() + "\n");
		}

		return reportList;
	}
}