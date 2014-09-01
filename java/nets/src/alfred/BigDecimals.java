package alfred;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BigDecimals {
	
	public static final int DEFAULT_PRECISION = 40;
	public static final MathContext MATH_CONTEXT = new MathContext(DEFAULT_PRECISION, RoundingMode.HALF_UP);
	
	private BigDecimals() {
		// utility class
	}
	
	public static BigDecimal exp(BigDecimal value) {
		return exp(value, DEFAULT_PRECISION);
	}
	
	public static BigDecimal exp(BigDecimal value, int precision) {
		MathContext mc = new MathContext(precision + 2);
		BigDecimal retVal = BigDecimal.ZERO;
		BigDecimal oldVal;
		int n = 0;
		do {
			oldVal = retVal;
			retVal = retVal.add(value.pow(n, mc).divide(factorial(n), mc), mc);
			n++;
		} while (!retVal.equals(oldVal));
		return retVal.round(new MathContext(precision));
	}

	public static BigDecimal factorial(long value) {
		BigDecimal retVal = BigDecimal.ONE;
		for (long i = value; i > 0; i--)
			retVal = retVal.multiply(new BigDecimal(i));
		return retVal;
	}
	
}
