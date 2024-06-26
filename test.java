package com.thealgorithms.audiofilters;

/**
 * takes in an integer `order` and creates internal arrays for coefficients `coeffsA`
 * and `coeffsB`, as well as history vectors `historyX` and `historyY`. The `setCoeffs()`
 * method sets the coefficients for the neural network, and the `process()` method
 * applies a weighted sum of past values and updates the history vectors using feedback.
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
     * sets the coefficients for a neural network. It checks if the input arrays have the
     * correct length and does not allow zero values in the first position, then assigns
     * the coefficients to internal arrays.
     * 
     * @param aCoeffs 1st polynomial coefficients.
     * 
     * @param bCoeffs 2nd set of coefficients to be multiplied with the `aCoeffs`.
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
     * takes a sample value and applies a weighted sum of past values based on an order-0
     * coeffients, updates history vectors using feedback mechanism.
     * 
     * @param sample 0th element of the sequence being processed, which is used to
     * initialize the feedforward network's inputs.
     * 
     * @returns a calculated value representing the forecasted value of the target variable.
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