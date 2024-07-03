package com.thealgorithms.audiofilters;

/**
 * Is used to implement an Internal Model (IIR) filter, which is a type of recurrent
 * neural network (RNN). The class takes in the order of the filter, as well as
 * coefficients for the two polynomial terms that make up the filter. The `process`
 * method takes in a sample value and applies the weighted sum of past values based
 * on the coefficient values, updating the history vectors using feedback mechanism.
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
     * Sets the coefficients of a and b based on user input. It checks that the length
     * of `aCoeffs` is equal to `order`, and that `aCoeffs[0]` is not zero. Finally, it
     * assigns the values of `aCoeffs` and `bCoeffs` to the `coeffsA` and `coeffsB` arrays
     * respectively.
     * 
     * @param aCoeffs 1D array of coefficients for the linear combination of polynomial
     * terms in the `setCoeffs()` method.
     * 
     * * Length: `aCoeffs.length` must equal `order`, which is a constant integer specified
     * in the function definition.
     * * Non-zero value: The first element of `aCoeffs`, `aCoeffs.get(0)`, must not be zero.
     * 
     * @param bCoeffs 2nd array of coefficients that are used to compute the output of
     * the `setCoeffs` function.
     * 
     * * `bCoeffs` has length `order`, which is the same as the output coeffs generated
     * by the function.
     * * The elements of `bCoeffs` are taken from a set of values, possibly including
     * zero and non-zero numbers.
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
     * Takes a sample value, processes it through a set of coefficients, and returns the
     * processed value with feedback applied to previous values.
     * 
     * @param sample initial value of the feedback loop, which is used to calculate the
     * output result in the `process()` function.
     * 
     * @returns a double value representing the result of feeding a given input sample
     * through a recurrent neural network.
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