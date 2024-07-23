package com.thealgorithms.audiofilters;

/**
 * Implements an Infinite Impulse Response (IIR) filter with adjustable order and
 * coefficients. It takes in sample values and applies a weighted sum of past values
 * using the given coefficients, updating history vectors through feedback. The output
 * is a forecasted value representing the target variable.
 */
public class IIRFilter {

    private final int order;
    private final double[] coeffsA;
    private final double[] coeffsB;
    private final double[] historyX;
    private final double[] historyY;

    public IIRFilter(int order) throws IllegalArgumentException {
        if (order < 1) {
            throw new IllegalArgumentException("order must be greater than zero");
        }

        this.order = order;
        coeffsA = new double[order + 1];
        coeffsB = new double[order + 1];

        // Sane defaults
        coeffsA[0] = 1.0;
        coeffsB[0] = 1.0;

        historyX = new double[order];
        historyY = new double[order];
    }

    
    /**
     * Sets coefficients for polynomial equation from provided arrays. It checks if input
     * arrays have correct length and first element of first array is not zero, then
     * copies elements to internal arrays. If conditions are not met, it throws an exception.
     * 
     * @param aCoeffs 1D array of coefficients for which the length must match the order,
     * and its first element cannot be zero, upon successful validation allowing for
     * assignment to internal arrays `coeffsA`.
     * 
     * It must have a length equal to the order and its first element cannot be zero.
     * 
     * @param bCoeffs coefficients of another polynomial, which are stored in the `coeffsB`
     * array if the lengths of `bCoeffs` and `aCoeffs` match the order of the polynomial.
     */
    public void setCoeffs(double[] aCoeffs, double[] bCoeffs) throws IllegalArgumentException {
        if (aCoeffs.length != order) {
            throw new IllegalArgumentException("aCoeffs must be of size " + order + ", got " + aCoeffs.length);
        }

        if (aCoeffs[0] == 0.0) {
            throw new IllegalArgumentException("aCoeffs.get(0) must not be zero");
        }

        if (bCoeffs.length != order) {
            throw new IllegalArgumentException("bCoeffs must be of size " + order + ", got " + bCoeffs.length);
        }

        for (int i = 0; i <= order; i++) {
            coeffsA[i] = aCoeffs[i];
            coeffsB[i] = bCoeffs[i];
        }
    }

    /**
     * Updates a feedback filter using the provided `sample`. It calculates the new output
     * by processing the previous input and output values, updating the internal state
     * with the sample, and returning the resulting value. The calculation involves
     * weighted sums of past inputs and outputs.
     * 
     * @param sample new value to be processed and updated by the feedback mechanism,
     * which is used to compute the output result.
     * 
     * @returns a double value, the processed sample.
     */
    public double process(double sample) {
        double result = 0.0;

        // Process
        for (int i = 1; i <= order; i++) {
            result += (coeffsB[i] * historyX[i - 1] - coeffsA[i] * historyY[i - 1]);
        }
        result = (result + coeffsB[0] * sample) / coeffsA[0];

        // Feedback
        for (int i = order - 1; i > 0; i--) {
            historyX[i] = historyX[i - 1];
            historyY[i] = historyY[i - 1];
        }

        historyX[0] = sample;
        historyY[0] = result;

        return result;
    }
}