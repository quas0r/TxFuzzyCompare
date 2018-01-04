package tutuka.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileValidationsUtil {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String[] validHeaderColumns= {"ProfileName", "TransactionDate", "TransactionAmount", "TransactionNarrative", "TransactionDescription", "TransactionID", "TransactionType", "WalletReference"};
	
	/* 
	 * There is no VALID Magic Number allotted by IANA for the MIMEType CSV.
	 * MEMO by IANA - https://tools.ietf.org/html/rfc4180
	 * So resorting to simple FileName Extension Validation technique
	 */
	private void isValidFormat(String fileName) {
		if(!fileName.toLowerCase().endsWith(".csv")) {
			logger.error("The File " +fileName+ " is NOT a VALID CSV file!!");
			throw new FileExtensionException("The File " +fileName+ " is NOT a VALID CSV file!!");	
		}
	}
	
	private void validateHeaders(String fileName)  {
		String header = null;
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			header = br.readLine();
			br.close();
		} catch(IOException e) {
			logger.error("IOException trying to validate headers of " +fileName);
			e.printStackTrace();
		}
	        if (header != null) {
	            String[] headerColumns = header.split(",");
	            Arrays.sort(validHeaderColumns);
	            Arrays.sort(headerColumns);
	            if(Arrays.asList(headerColumns).contains(Arrays.asList(validHeaderColumns))){
	            	logger.error("The File "+fileName+" contains INVALID or NO Headers.");
	            	throw new InvalidHeaderException("The File "+fileName+" contains INVALID or NO Headers.");
	            }
	        }
	        else {
	        	logger.error("The File " +fileName+ " is EMPTY or CORRUPT!!");
	        	throw new EmptyFileException("The File " +fileName+ " is EMPTY or CORRUPT!!");
	        }
	}
	
	public void validate(String fileName) {
		isValidFormat(fileName);
		validateHeaders(fileName);
	}
}