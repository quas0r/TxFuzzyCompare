package tutuka.compareTransactions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class FuzzyLogicProperties {

	@Value("${fuzzy.match_tolerance}")
	public double match_tolerance;
	
	@Value("${fuzzy.probable_match_tolerance}")
	public double probable_match_tolerance;
	
	@Value("${fuzzy.probable_mismatch_tolerance}")
	public double probable_mismatch_tolerance;
	
	@Value("${fuzzy.valid_incr}")
	public double valid_incr;
	
	@Value("${fuzzy.fuzzy_incr}")
	public double fuzzy_incr;
	
	@Value("${fuzzy.tx_nrtv_valid_incr}")
	public double tx_nrtv_valid_incr;
	
	@Value("${fuzzy.tx_nrtv_probable_match_incr}")
	public double tx_nrtv_probable_match_incr;
	
	@Value("${fuzzy.tx_nrtv_probable_mismatch_incr}")
	public double tx_nrtv_probable_mismatch_incr;
	
	@Value("${fuzzy.total_score}")
	public double total_score;
	
	@Value("${fuzzy.permissible_match_lower}")
	public double permissible_match_lower;
	
	@Value("${fuzzy.probable_match_lower}")
	public double probable_match_lower;
	
	@Value("${fuzzy.probable_mismatch_lower}")
	public double probable_mismatch_lower;
	
}
