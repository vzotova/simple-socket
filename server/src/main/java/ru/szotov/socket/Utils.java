package ru.szotov.socket;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class with utilities
 * 
 * @author szotov
 *
 */
public final class Utils {
	
	private static final Logger LOG = LogManager.getLogger(Utils.class);
	
	private Utils() {
		
	}
	
	/**
	 * Parse long value. If can't parse then returns null
	 * 
	 * @param line
	 *            input lane
	 * @return long value
	 */
	public static Long parseLong(String line) {	
		if (!isInteger(line)) {
			LOG.warn("Line [{}] is not an integer", line);
			return null;
		}
		
		return Long.parseLong(line);
	}

	/**
	 * Check string for an integer
	 * 
	 * @param line
	 *            input line
	 * @return result of checking
	 */
	public static boolean isInteger(String line) { 
		if (line == null) {
			return false;
		}
	    return line.matches("[-+]?\\d+");  
	}

	/**
	 * Decomposes into prime factors
	 * 
	 * @param number
	 *            number for decomposing
	 * @return result of decomposing
	 */
	public static List<Long> decompose(long number) {
		int sign = 1;
		if (number < 0) {
			number = -number;
			sign = -1;
		}
		
		List<Long> factors = new ArrayList<>();
		for (long i = 2; i <= number / i; i++) {
			while (number % i == 0) {
				factors.add(i);
				number /= i;
			}
		}
		if (number > 1) {
			factors.add(number);
			factors.set(0, sign * factors.get(0));
		}
		
		return factors;
	}
	
}
