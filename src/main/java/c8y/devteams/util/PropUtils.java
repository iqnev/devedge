package c8y.devteams.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ivelin Yanev
 * @since 01.09.2020
 *
 */
public class PropUtils {
	private static final Logger logger = LoggerFactory.getLogger(PropUtils.class);

	/**
	 * Private constructor to prevent instantiation.
	 */
	private PropUtils() {
		throw new UnsupportedOperationException();
	}

	public static Properties fromFile(String file) {
		Properties props = new Properties();
		fromFile(file, props);
		return props;
	}

	public static void fromFile(String file, Properties props) {
		try (FileReader reader = new FileReader(file)) {
			props.load(reader);
			logger.debug("Read configuration file, current configuration: {}", props);
		} catch (IOException iox) {
			logger.warn("Configuration file {} cannot be read, assuming empty configuration", file);
		}
	}
}
