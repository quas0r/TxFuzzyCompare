package tutuka.compareTransactions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;

import tutuka.utils.EmptyFileException;
import tutuka.utils.StopWordsUtils;

public class CSVToBeanReader {
	private int pNameIndex;
	private int TxDateIndex;
	private int TxAmtIndex;
	private int TxNrtvIndex;
	private int TxDscrIndex;
	private int TxIDIndex;
	private int TxTypeIndex;
	private int WRefIndex;

	StopWordsUtils util = new StopWordsUtils();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	LocalDateTime transactionDate = null;
	Pattern pattern = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
	Splitter splitter = Splitter.on(pattern);

	/**
	 * Reads the file into a List of tutuka.compareTransactions.Transaction Objects
	 * 
	 * @param String
	 *            FileName
	 * @return List of transactions after appropriate validations and trimming<br>
	 */
	public List<Transaction> csvRead(String fileName) {
		logger.debug("Inside csvRead method");
		List<Transaction> TransactionsList = new ArrayList<Transaction>();
		long startTime = System.currentTimeMillis();
		try {
			InputStream inputFS = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
			String line = null;
			String[] headers = null;
			line = br.readLine();
			headers = line.split(",");

			/*
			 * This is done to obtain the indexes of the headers. Further in the parsing
			 * process, this is used so that order of the columns doesn't matter
			 */
			pNameIndex = Arrays.asList(headers).indexOf("ProfileName");
			TxDateIndex = Arrays.asList(headers).indexOf("TransactionDate");
			TxAmtIndex = Arrays.asList(headers).indexOf("TransactionAmount");
			TxNrtvIndex = Arrays.asList(headers).indexOf("TransactionNarrative");
			TxDscrIndex = Arrays.asList(headers).indexOf("TransactionDescription");
			TxIDIndex = Arrays.asList(headers).indexOf("TransactionID");
			TxTypeIndex = Arrays.asList(headers).indexOf("TransactionType");
			WRefIndex = Arrays.asList(headers).indexOf("WalletReference");

			TransactionsList = br.lines().parallel().map(mapToItem).collect(Collectors.toList());
			br.close();
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException, The file " + fileName + " cannot be found ");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("IOException from csvRead method when trying to read " + fileName + " into memory");
			e.printStackTrace();
		}

		logger.info("Total records in the file : " + TransactionsList.size());
		
		if(TransactionsList.size()==0) {
			logger.error("The file "+fileName+" has ZERO transactions");
			throw new EmptyFileException("The file "+fileName+" has ZERO transactions");
		}
		
		logger.info("TotalTime to read " + fileName + " is : " + (-startTime + (System.currentTimeMillis())) + "ms");

		return TransactionsList;

	}

	private Function<String, Transaction> mapToItem = (line) -> {
		Transaction transaction = new Transaction();
		/*
		 * Splitter from Guava library takes care of fields that have commas within
		 * double quotes as well eg.) Address fields with commas in them
		 */
		String[] p = splitter.splitToList(line).toArray(new String[0]);

		if (p.length > pNameIndex && pNameIndex >= 0) {
			if (StringUtils.trimToNull(p[pNameIndex]) != null) {
				transaction.setProfileName(p[pNameIndex].replaceAll("[^a-zA-Z ]+", ""));
			}
		}

		if (p.length > TxDateIndex && TxDateIndex >= 0) {
			if (StringUtils.trimToNull(p[TxDateIndex]) != null) {
				try {
					transactionDate = LocalDateTime.parse(p[TxDateIndex], formatter);
				} catch (Exception e) {
					logger.error("DateTimeParseException occured while trying to parse " +p[TxDateIndex]+ " in the Transaction Date Field. File may be corrupt");
					throw new TransactionDateException("DateTimeParseException occured while trying to parse "
							+ p[TxDateIndex] + " in the Transaction Date Field. File may be corrupt", p[TxDateIndex]);
				}
				if (util.isValidDate(transactionDate)) {
					transaction.setTransactionDate(transactionDate);
				}
			}
		}

		if (p.length > TxAmtIndex && TxAmtIndex >= 0) {
			if (StringUtils.trimToNull(p[TxAmtIndex]) != null) {
				transaction.setTransactionAmount(Long.valueOf(p[TxAmtIndex].replaceAll("[^\\p{Digit}-]+", "")));
			}
		}

		if (p.length > TxNrtvIndex && TxNrtvIndex >= 0) {
			if (StringUtils.trimToNull(p[TxNrtvIndex]) != null) {
				transaction.setTransactionNarrative(util.replaceStopWords(
						Normalizer.normalize(p[TxNrtvIndex], Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]+", "")));
			}
		}

		if (p.length > TxDscrIndex && TxDscrIndex >= 0) {
			if (StringUtils.trimToNull(p[4]) != null) {
				transaction.setTransactionDescription(p[TxDscrIndex].replaceAll("[^a-zA-Z]+", ""));
			}
		}

		if (p.length > TxIDIndex && TxIDIndex >= 0) {
			if (StringUtils.trimToNull(p[TxIDIndex]) != null) {
				transaction.setTransactionID(new BigInteger(p[TxIDIndex].replaceAll("[^\\p{Digit}]+", "")));
			}
		}

		if (p.length > TxTypeIndex & TxTypeIndex >= 0) {
			if (StringUtils.trimToNull(p[TxTypeIndex]) != null) {
				transaction.setTransactionType(Integer.parseInt(p[TxTypeIndex].replaceAll("[^\\p{Digit}]+", "")));
			}
		}

		if (p.length > WRefIndex && WRefIndex >= 0) {
			if (StringUtils.trimToNull(p[WRefIndex]) != null) {
				transaction.setWalletReference(p[WRefIndex].replaceAll("[^a-zA-Z0-9 _]+", ""));
			}
		}

		return transaction;

	};
}
