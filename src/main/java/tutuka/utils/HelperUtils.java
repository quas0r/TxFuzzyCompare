package tutuka.utils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelperUtils {
	// src/main/resources/
	private static final String PROPERTIESPATH = "../../stop_words.properties";
	HashMap<String, String> map = new HashMap<>();
	Properties properties = new Properties();
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public boolean isValidDate(LocalDateTime date) {
		LocalDateTime now = LocalDateTime.now();
		if (now.compareTo(date) >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * Replace certain stop words from the property file. Further improvements could
	 * use stemming techniques
	 * 
	 * @throws IOException
	 **/

	public String replaceStopWords(String string) {
		InputStream inputStream = HelperUtils.class.getResourceAsStream(PROPERTIESPATH);
		try {
			properties.load(inputStream);
			this.map.putAll(properties.entrySet().stream()
					.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString())));
			string = Arrays.stream(string.toUpperCase().split(" ")).map(s -> map.getOrDefault(s, s))
					.collect(Collectors.joining(" "));
		} catch (Exception e) {
			logger.warn(
					"ERROR Reading the Stop Words File!");
			e.printStackTrace();
		}
		return string;
	}

}
