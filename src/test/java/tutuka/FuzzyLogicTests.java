package tutuka;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import tutuka.compareTransactions.CSVToBeanReader;
import tutuka.compareTransactions.FuzzyLogicService;
import tutuka.compareTransactions.Result;
import tutuka.compareTransactions.Transaction;
import tutuka.compareTransactions.TransactionReportWithScore;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "fuzzy.valid_incr=1" })
public class FuzzyLogicTests {
	
	@Autowired
	FuzzyLogicService fuzzy;

	private List<Transaction> loadFile(String filename) throws Exception {
		File testFile = new File(getClass().getClassLoader().getResource(filename).getFile());
		CSVToBeanReader csvBeanReader = new CSVToBeanReader();
		List<Transaction> transactionsList = new ArrayList<Transaction>();
		transactionsList = csvBeanReader.csvRead(testFile.getAbsolutePath());
		return transactionsList;
	}

	private MultipartFile fileToMultipart(File file) {
		FileInputStream input = null;
		MultipartFile multipartFile = null;
		try {
			input = new FileInputStream(file);
			multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
			System.out.println(" file.getName() is : " + file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return multipartFile;
	}

	private FileLoader filesToFileLoader(File file1Name, File file2Name) {
		FileLoader fileLoader = new FileLoader();
		fileLoader.setFileOne(fileToMultipart(file1Name));
		fileLoader.setFileTwo(fileToMultipart(file2Name));
		return fileLoader;
	}
	
	
	
	/*	WalletReference Mismatch and 
	*	Transaction Amt Fuzzy Match
	*/
	@Test
	public void test_ProbableMisMatch() {
		File tutukaFile = new File("src/test/resources/tutuka_ProbableMisMatch.csv");
		File clientFile = new File("src/test/resources/client_ProbableMisMatch.csv");
		List<TransactionReportWithScore> report = new ArrayList<>();
		try {
			report = fuzzy.fuzzyLogicMatch(loadFile(tutukaFile.getName()), loadFile(clientFile.getName()),
					filesToFileLoader(tutukaFile, clientFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertThat(report.get(0).getStatus()).isEqualTo(Result.PROBABLE_MISMATCH);
		assertThat(report.get(0).getReasons().toString().contains("WalletReference Mismatch"));
		assertThat(report.get(0).getReasons().toString().contains("Transaction Amount Fuzzy match"));
		System.out.println(report.get(0).getReasons().toString());
	}
	
	@Test
	public void test_FuzzyAmountMatch() {
	File tutukaFile = new File("src/test/resources/tutuka_PermissbleAmtMatch.csv");
	File clientFile = new File("src/test/resources/client_PermissbleAmtMatch.csv");
	List<TransactionReportWithScore> report = new ArrayList<>();
	try {
		report = fuzzy.fuzzyLogicMatch(loadFile(tutukaFile.getName()), loadFile(clientFile.getName()),
				filesToFileLoader(tutukaFile, clientFile));
	} catch (Exception e) {
		e.printStackTrace();
	}
	assertThat(report.get(0).getStatus()).isEqualTo(Result.PERMISSIBLE_MATCH);
	assertThat(report.get(0).getReasons().toString().contains("Transaction Amount Fuzzy match"));
	}
	
	@Test
	public void test_FuzzyDateMatch() {
		File tutukaFile = new File("src/test/resources/tutuka_PermissbleDateMatch.csv");
		File clientFile = new File("src/test/resources/client_PermissbleDateMatch.csv");
		List<TransactionReportWithScore> report = new ArrayList<>();
		try {
			report = fuzzy.fuzzyLogicMatch(loadFile(tutukaFile.getName()), loadFile(clientFile.getName()),
					filesToFileLoader(tutukaFile, clientFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//First record contains a time difference of 2 hours
		assertThat(report.get(0).getStatus()).isEqualTo(Result.PERMISSIBLE_MATCH);
		assertThat(report.get(0).getReasons().toString().contains("TransactionDate Fuzzy match"));
		//Second record contains a time difference of 4 hours
		assertThat(report.get(1).getStatus()).isEqualTo(Result.PROBABLE_MATCH);
		assertThat(report.get(1).getReasons().toString().contains("TransactionDate Mismatch"));
	}
	
	@Test
	public void test_Perfect_Mismatch() {
		File tutukaFile = new File("src/test/resources/tutuka_PerfectMismatch.csv");
		File clientFile = new File("src/test/resources/client_PerfectMismatch.csv");
		List<TransactionReportWithScore> report = new ArrayList<>();
		try {
			report = fuzzy.fuzzyLogicMatch(loadFile(tutukaFile.getName()), loadFile(clientFile.getName()),
					filesToFileLoader(tutukaFile, clientFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertThat(report.get(0).getStatus()).isEqualTo(Result.PERFECT_MISMATCH);
		assertThat(report.get(0).getReasons().toString().contains("TransactionNarrative Mismatch"));
		assertThat(report.get(0).getReasons().toString().contains("ProfileName Mismatch"));
		assertThat(report.get(0).getReasons().toString().contains("TransactionType Mismatch"));
		assertThat(report.get(0).getReasons().toString().contains("TransactionAmount Mismatch"));
		assertThat(report.get(0).getReasons().toString().contains("TransactionDate Mismatch"));
		assertThat(report.get(0).getReasons().toString().contains("WalletReference Mismatch"));
		assertThat(report.get(0).getMatchScore()==0);
		
	}
	
	//Test to catch NULL values of other columns other than ID and Descr
	@Test
	public void catchesOtherNullValues() {
		File tutukaFile = new File("src/test/resources/tutuka_OthersNull.csv");
		File clientFile = new File("src/test/resources/client_OthersNull.csv");
		List<TransactionReportWithScore> report = new ArrayList<>();
		try {
			report = fuzzy.fuzzyLogicMatch(loadFile(tutukaFile.getName()), loadFile(clientFile.getName()),
					filesToFileLoader(tutukaFile, clientFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertThat(report.get(0).getStatus()).isEqualTo(Result.PERFECT_MISMATCH);
		assertThat(report.get(0).getReasons().toString().contains("TransactionNarrative Mismatch"));
		assertThat(report.get(0).getReasons().toString().contains("ProfileName Mismatch"));
		assertThat(report.get(0).getReasons().toString().contains("TransactionType Mismatch"));
		assertThat(report.get(0).getReasons().toString().contains("TransactionAmount Mismatch"));
		assertThat(report.get(0).getReasons().toString().contains("TransactionDate Mismatch"));
		assertThat(report.get(0).getReasons().toString().contains("WalletReference Mismatch"));
		assertThat(report.get(0).getMatchScore()==0);
	}

}
