package alfred;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;

import alfred.NetTrainSpecification.Builder;
import alfred.scaling.ScaleFunctions.ScaleFunctionType;
import alfred.util.BigDecimals;
import alfred.util.TimeUtils;

import com.google.common.base.Throwables;

/**
 * Simple rectangular neural network.
 *
 * @author saf
 *
 */
public class RectNetFixed extends Net {
    private static final double NEGATIVE_INFINITY = -1000000;
    private static final double INFINITY = 1000000;
    public static final int DEFAULT_RETRIES = 5;
    // Inputs to network
    protected InputImpl[] inputs;
    // Every neuron with the same i is in the
    // same "layer". Indexed as [col][row].
    protected FixedNeuron[][] neurons;
    // X is depth of network
    protected int x;
    // Y is height of network (number of inputs)
    protected int y;
    // There's only one final output neuron
    // since this is built to make booleans.
    protected FixedNeuron output;
    // Prints debug output when true.
    protected boolean verbose = false;
    private NetDataSpecification netData = null;
    private TimingInfo timingInfo = null;
    private TrainingSummary trainingSummary = null;

    /**
     * Constructs a new RectNet with 10 inputs and 5 layers of network.
     */
    public RectNetFixed() {
        this.x = 5;
        this.y = 10;
        init();
    }

    /**
     * Constructs a new RectNet with given depth and number of inputs.
     *
     * @param depth
     *            number of layers in the network
     * @param numInputs
     *            number of inputs to the network
     */
    public RectNetFixed(int depth, int numInputs) {
        if (depth < 1 || numInputs < 1) {
            throw new IllegalArgumentException("Depth and numinputs must be >= 1");
        }
        this.x = depth;
        this.y = numInputs;
        init();
    }

    /**
     * Constructs a new RectNet with given depth and number of inputs. Sets the
     * verbose boolean as given.
     *
     * @param depth
     *            number of layers in the network
     * @param numInputs
     *            number of inputs to the network
     * @param verbose
     *            true when RectNet displays debug output.
     */
    public RectNetFixed(int depth, int numInputs, boolean verbose) {
        if (depth < 1 || numInputs < 1) {
            throw new IllegalArgumentException("Depth and numinputs must be >= 1");
        }
        this.x = depth;
        this.y = numInputs;
        this.verbose = verbose;
        init();
    }

    /**
     * Gets the weight between two neurons. Only works for internal layers (not
     * the output neuron layer).
     *
     * @param leftCol
     *            column number of neuron to left of connection
     * @param leftRow
     *            row number of neuron to left of connection
     * @param rightCol
     *            column number of neuron to right of connection
     * @param rightRow
     *            row number of neuron to right of connection
     * @return weight from right neuron to left neuron.
     */
    public BigDecimal getWeight(int leftCol, int leftRow, int rightCol, int rightRow) {
        Validate.isTrue(leftCol >= 0);
        Validate.isTrue(leftRow >= 0);
        Validate.isTrue(rightCol >= 0);
        Validate.isTrue(rightRow >= 0);
        Validate.isTrue(leftCol < this.x);
        Validate.isTrue(rightCol < this.x);
        Validate.isTrue(leftRow < this.y);
        Validate.isTrue(rightRow < this.y);
        return this.neurons[rightCol][rightRow].getWeight(leftRow);
    }

    /**
     * Returns the width of this net
     *
     * @return the width of this net
     */
    public int getX() {
        return x;
    }

    /**
     * returns the height of this net
     *
     * @return the height of this net
     */
    public int getY() {
        return y;
    }

    public void setData(NetDataSpecification dataSpec) {
        this.netData = dataSpec;
    }

    public void setTimingInfo(TimingInfo timingInfo) {
        this.timingInfo = timingInfo;
    }

    public TimingInfo getTimingInfo() {
        return this.timingInfo;
    }

    public boolean hasTimeExpired() {
        return getTimingInfo().hasTimeExpired();
    }

    public NetDataSpecification getDataSpec() {
        return this.netData;
    }

    public TrainingSummary getTrainingSummary() {
        return trainingSummary;
    }

    /**
     * Returns the weight from the output neuron to an input specified by the
     * given row.
     *
     * @param leftRow
     *            the column containing the neuron to the left of the output
     *            neuron.
     * @return the weight from the output neuron to the neuron in leftRow
     */
    public BigDecimal getOutputNeuronWeight(int leftRow) {
        Validate.isTrue(leftRow >= 0);
        Validate.isTrue(leftRow < this.y);
        return this.output.getWeight(leftRow);
    }

    /**
     * Sets the weight from the output neuron to an input specified by the given
     * row
     *
     * @param leftRow
     *            the row containing the neuron to the left of the output
     *            neuron.
     * @param w
     *            the new weight from the output neuron to the neuron at leftRow
     */
    public void setOutputNeuronWeight(int leftRow, BigDecimal w) {
        Validate.isTrue(leftRow >= 0);
        Validate.isTrue(leftRow < this.y);
        this.output.setWeight(leftRow, w);
    }

    /**
     * Sets the weight between two neurons to the given value w. Only works for
     * internal layers (not the output neuron layer).
     *
     * @param leftCol
     *            column number of neuron to left of connection
     * @param leftRow
     *            row number of neuron to left of connections
     * @param rightCol
     *            column number of neuron to right of connection
     * @param rightRow
     *            row number of neuron to right of connection
     * @param w
     *            weight to set on connection.
     */
    public void setWeight(int leftCol, int leftRow, int rightCol, int rightRow,
            BigDecimal w) {
        Validate.isTrue(leftCol >= 0);
        Validate.isTrue(leftRow >= 0);
        // right column should be >= 1 because the weights from first row to
        // inputs should never be changed
        Validate.isTrue(rightCol >= 1);
        Validate.isTrue(rightCol - leftCol == 1);
        Validate.isTrue(rightRow >= 0);
        Validate.isTrue(leftCol < this.y);
        Validate.isTrue(rightCol < this.y);
        Validate.isTrue(leftRow < this.x);
        Validate.isTrue(rightRow < this.x);
        this.neurons[rightCol][rightRow].setWeight(leftRow, w);
    }

    /**
     * Initializes the RectNet by: 1) creating neurons and inputs as necessary
     * 2) connecting neurons to the inputs 3) connecting neurons to each other
     * 4) connecting neurons to the output
     *
     * Initial weights are specified by initNum(), allowing random initial
     * weights, or some other set.
     */
    private void init() {
        initEmptyNeurons();
        // Make connections between neurons and inputs.
        initNeuronConnections();
    }

    private void initNeuronConnections() {
        for (int j = 0; j < this.y; j++) {
            this.neurons[0][j].addInput(this.inputs[j], BigDecimal.ONE);
        }
        // Make connections between neurons and neurons.
        for (int leftCol = 0; leftCol < this.x - 1; leftCol++) {
            int rightCol = leftCol + 1;
            for (int leftRow = 0; leftRow < this.y; leftRow++) {
                for (int rightRow = 0; rightRow < this.y; rightRow++) {
                    this.neurons[rightCol][rightRow].addInput(
                            this.neurons[leftCol][leftRow], initNum());
                }
            }
        }
        // Make connections between output and neurons.
        for (int j = 0; j < this.y; j++) {
            this.output.addInput(this.neurons[this.x - 1][j], initNum());
        }
    }

    private void initEmptyNeurons() {
        // Initialize arrays to blank neurons and inputs.
        this.inputs = new InputImpl[y];
        this.neurons = new FixedNeuron[x][y];
        this.output = new FixedNeuron(this.y);
        // Name the neurons for possible debug. This is not a critical
        // step.
        this.output.setName("output");
        for (int j = 0; j < this.y; j++) {
            this.inputs[j] = new InputImpl();
            // initialize the first row
            this.neurons[0][j] = new FixedNeuron(1);
            this.neurons[0][j].setName("(" + 0 + "," + j + ")");
            for (int i = 1; i < this.x; i++) {
                this.neurons[i][j] = new FixedNeuron(this.y);
                this.neurons[i][j].setName("(" + i + "," + j + ")");
            }
        }
    }

    /**
     * Allows network weight initialization to be changed in one location.
     *
     * @return a double that can be used to initialize weights between network
     *         connections.
     */
    private BigDecimal initNum() {
        return BigDecimal.valueOf((Math.random() - .5) * 1.0);
    }

    /**
     * Sets the inputs of this network to the values given. Length of inpts must
     * be equal to the "height" of the network.
     *
     * @param inpts
     *            array of double to set as network inputs.
     */
    public void setInputs(BigDecimal[] inpts) {
        Validate.isTrue(inpts.length == this.y);
        for (int j = 0; j < this.y; j++) {
            this.inputs[j].setValue(inpts[j]);
        }
    }

    /**
     * Returns the output value from this network run.
     */
    @Override
    public BigDecimal getOutput() {
        BigDecimal[] outs = new BigDecimal[this.y];
        BigDecimal[] ins = new BigDecimal[this.y];
        for (int j = 0; j < this.y; j++) {
            // require recursion (depth = 0) here.
            ins[j] = this.neurons[0][j].getOutput();
        }
        // indexing must start at 1, because we've already computed
        // the output from the 0th row in the previous 3 lines.
        for (int i = 1; i < this.x; i++) {
            for (int j = 0; j < this.y; j++) {
                outs[j] = this.neurons[i][j].getOutput(ins);
            }
            ins = outs;
            outs = new BigDecimal[this.y];
        }
        BigDecimal d = this.output.getOutput(ins);
        return d;
    }

    /**
     * Trains the network on a given input with a given output the number of
     * times specified by iterations. Trains via a backpropagation algorithm.
     *
     * @param inpts
     *            input values for the network.
     * @param desired
     *            what the result of the network should be.
     * @param learningConstant
     *            the "rate" at which the network learns
     * @param iterations
     *            number of times to train the network.
     * @throws InterruptedException
     */
    public void train(BigDecimal[] inpts, BigDecimal desired, int iterations,
            BigDecimal learningConstant) throws InterruptedException {
        Validate.isTrue(iterations > 0);
        Validate.isTrue(inpts.length == this.y);
        for (int lcv = 0; lcv < iterations && !hasTimeExpired(); lcv++) {
            doIteration(inpts, desired, learningConstant);
        }
    }

    /**
     * Denormalizes targets and estimates.
     */
    public static void writeAugoutFile(String filename, RectNetFixed net) {
        StringBuilder sb = new StringBuilder();
        TrainingSummary summary = net.getTrainingSummary();
        sb.append("Training stop reason: ").append(summary.getStopReason().getExplanation()).append("\n");
        sb.append("Time trained: ").append(summary.getSecondsElapsed()).append("\n");
        sb.append("Rounds trained: ").append(summary.getRoundsTrained()).append("\n");
        sb.append("RMS Error: ").append(summary.getRmsError()).append("\n");
        for (InputsAndTarget trainDatum : net.getDataSpec().getTrainData()) {
            writeDataLine(sb, trainDatum, net);
        }
        for (InputsAndTarget predictionDatum : net.getDataSpec().getPredictionData()) {
            writePredictionLine(sb, predictionDatum, net);
        }
        try {
            FileUtils.writeStringToFile(new File(filename), sb.toString());
        } catch (Throwable t) {
            System.err.println("Unable to write to file " + filename);
            t.printStackTrace();
        }
    }

    private static void writePredictionLine(StringBuilder sb, InputsAndTarget predictionDatum, RectNetFixed net) {
        sb.append(predictionDatum.getDate()).append(" ");
        sb.append("NULL").append(" ");

        net.setInputs(predictionDatum.getInputs());
        BigDecimal trainedEstimate = net.getOutput();
        trainedEstimate = net.getDataSpec().denormalize(trainedEstimate);
        sb.append(trainedEstimate.doubleValue()).append(" ");

        sb.append("NULL").append("\n");
    }

    private static void writeDataLine(StringBuilder sb, InputsAndTarget trainDatum, RectNetFixed net) {
        sb.append(trainDatum.getDate()).append(" ");

        BigDecimal target = trainDatum.getTarget();
        target = net.getDataSpec().denormalize(target);
        sb.append(target.doubleValue()).append(" ");

        net.setInputs(trainDatum.getInputs());
        BigDecimal trainedEstimate = net.getOutput();
        trainedEstimate = net.getDataSpec().denormalize(trainedEstimate);
        sb.append(trainedEstimate.doubleValue()).append(" ");

        sb.append(Math.abs(target.doubleValue() - trainedEstimate.doubleValue()));
        sb.append("\n");
    }

    private void doIteration(BigDecimal[] inpts, BigDecimal desired, BigDecimal learningConstant) throws InterruptedException {
        checkInterrupted();
        // Set the inputs
        setInputs(inpts);
        // Compute the last node error
        BigDecimal deltaF = getOutputError(desired);
        if (verbose) {
            System.out.print("DeltaF (smaller is better): " + deltaF);
        }
        // For each interior node, compute the weighted error
        // deltas are of the form
        // delta[col][row]
        BigDecimal[][] deltas = computeInteriorDeltas(deltaF);
        // now that we have the deltas, we can change the weights
        // again, we special case the last neuron
        updateLastNeuronWeights(learningConstant, deltaF);
        // now we do the same for the internal nodes
        updateInteriorNodeWeights(learningConstant, deltas);
    }

    private void updateInteriorNodeWeights(BigDecimal learningConstant,
            BigDecimal[][] deltas) {
        int leftCol;
        int leftRow;
        int rightCol;
        int rightRow;
        for (leftCol = this.x - 2; leftCol >= 0; leftCol--) {
            rightCol = leftCol + 1;
            for (leftRow = 0; leftRow < this.y; leftRow++) {
                for (rightRow = 0; rightRow < this.y; rightRow++) {
                    // w' = w + r*i*delta
                    // r is the learning constant
                    // i is the output from the leftward neuron
                    BigDecimal dw = learningConstant.multiply(this.neurons[leftCol][leftRow]
                                    .getLastOutput()).multiply(deltas[rightCol][rightRow]);
                    this.neurons[rightCol][rightRow].changeWeight(leftRow,
                            dw);
                    if (verbose) {
                        System.out.println(leftCol + "," + leftRow + "->" + rightCol + "," + rightRow);
                        System.out.println(this.neurons[rightCol][rightRow].getWeight(leftRow));
                    }
                }
            }
        }
    }

    private void updateLastNeuronWeights(BigDecimal learningConstant,
            BigDecimal deltaF) {
        for (int j = 0; j < this.y; j++) {
            // w' = w + r*i*delta
            // r is the learning constant
            // i is the output from the leftward neuron
            BigDecimal dw = learningConstant.multiply(this.neurons[this.x - 1][j].getLastOutput()).multiply(deltaF);
            this.output.changeWeight(j, dw);
        }
    }

    private BigDecimal[][] computeInteriorDeltas(BigDecimal deltaF) {
        BigDecimal[][] deltas = new BigDecimal[this.x + 1][this.y];
        // spoof the rightmost deltas
        for (int j = 0; j < y; j++) {
            deltas[this.x][j] = deltaF;
        }
        int leftCol = 0;
        int leftRow = 0;
        int rightCol = 0;
        int rightRow = 0;
        for (leftCol = this.x - 1; leftCol >= 0; leftCol--) {
            rightCol = leftCol + 1;
            for (leftRow = 0; leftRow < this.y; leftRow++) {
                BigDecimal lastOutput = this.neurons[leftCol][leftRow].getLastOutput();
                // since we're using alpha = 3 in the neurons
                // 3 * lastOutput * (1 - lastOutput);
                BigDecimal delta = BigDecimal.valueOf(3).multiply(lastOutput).multiply(BigDecimal.ONE.subtract(lastOutput));
                BigDecimal summedRightWeightDelta = BigDecimal.ZERO;
                for (rightRow = 0; rightRow < this.y; rightRow++) {
                    if (rightCol == this.x) {
                        summedRightWeightDelta = summedRightWeightDelta.add(this.output.getWeight(leftRow).multiply(deltaF));
                        // without the break, we were adding too many of the
                        // contributions of the output node when computing
                        // the deltas value for the layer immediately left
                        // of it.
                        break;
                    } else {
                        // summing w * delta
                        summedRightWeightDelta = summedRightWeightDelta.add(getWeight(leftCol,
                                leftRow, rightCol, rightRow).multiply(deltas[rightCol][rightRow]));
                    }
                }
                deltas[leftCol][leftRow] = delta.multiply(summedRightWeightDelta);
                if (verbose) {
                    System.out.println("leftCol: " + leftCol
                            + ", leftRow: " + leftRow + ", lo*(1-lo): "
                            + delta);
                    System.out.println("leftCol: " + leftCol
                            + ", leftRow: " + leftRow + ", srwd: "
                            + summedRightWeightDelta);
                    System.out.println("leftCol: " + leftCol
                            + ", leftRow: " + leftRow + ", delta: "
                            + deltas[leftCol][leftRow]);
                }
            }
        }
        return deltas;
    }

    /**
     * Computes the error in the network, assuming that the proper inputs have
     * been set before this method is called.
     *
     * @param desired
     *            the desired output.
     * @return error using equation (output*(1-output)*(desired-output))
     */
    protected BigDecimal getOutputError(BigDecimal desired) {
        getOutput();
        // since we're using alpha = 3 in the neurons
        // 3 * last * ( 1 - last) * (desired - last)
        BigDecimal last = this.output.getLastOutput();
        BigDecimal oneMinusLast = BigDecimal.ONE.subtract(last);
        BigDecimal desiredMinusLast = desired.subtract(last);
        return BigDecimal.valueOf(3).multiply(last).multiply(oneMinusLast).multiply(desiredMinusLast);
    }

    private static class TestStats {
        public BigDecimal testScore;
        public BigDecimal lastScore;
        public BigDecimal bestCheck;
        public BigDecimal bestTestCheck;

        public TestStats() {
            this.testScore = BigDecimal.ZERO;
            this.lastScore = BigDecimal.valueOf(INFINITY);
            this.bestCheck = BigDecimal.valueOf(INFINITY);
            this.bestTestCheck = BigDecimal.valueOf(INFINITY);
        }
    }

    private static class TrainingStats {
        public long startTime;
        public TrainingStopReason stopReason;
        public boolean brokeAtLocalMax;
        public BigDecimal maxScore;
        public int displayRounds;

        public TrainingStats(long start) {
            this.startTime = start;
            this.brokeAtLocalMax = false;
            this.stopReason = TrainingStopReason.HIT_TRAINING_LIMIT;
            this.maxScore = BigDecimal.valueOf(NEGATIVE_INFINITY);
            this.displayRounds = 1000;
        }
    }

    public static RectNetFixed trainFile(String fileName,
            boolean verbose,
            String saveFile,
            boolean testing,
            long trainingTimeLimitMillis,
            ScaleFunctionType sfType) throws InterruptedException {
        return trainFile(fileName, verbose, saveFile, testing, trainingTimeLimitMillis, sfType, DEFAULT_RETRIES);
    }

    private void checkInterrupted() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException("Job detected interrupt flag");
        }
    }

    /**
     * Train a neural network from a .augtrain training file
     *
     * @param fileName
     *            File path to .augtrain training file
     * @param verbose
     *            Flag to display debugging text or not
     * @return The trained neural network
     * @throws InterruptedException
     */
    public static RectNetFixed trainFile(String fileName,
                                         boolean verbose,
                                         String saveFile,
                                         boolean testing,
                                         long trainingTimeLimitMillis,
                                         ScaleFunctionType sfType,
                                         int triesRemaining) throws InterruptedException {
        if (trainingTimeLimitMillis <= 0) {
            System.out.println("Training timeout was " + trainingTimeLimitMillis +
                    ", which is <= 0, so jobs will not time out.");
        }
        if (triesRemaining == 0) {
            System.err.println("Unable to train file " + fileName + "!");
            throw new IllegalStateException("Unable to train file " + fileName + "!");
        }
        System.out.println("Parsing file " + fileName + " for training.");
        NetTrainSpecification netSpec = parseFile(fileName, sfType, verbose);
        RectNetFixed net = new RectNetFixed(netSpec.getDepth(), netSpec.getSide());
        net.setData(netSpec.getNetData());
        net.setTimingInfo(TimingInfo.withDuration(trainingTimeLimitMillis));
        // Actually do the training part
        System.out.println("Net for " + fileName + " parsed, starting training.");
        TrainingStats trainingStats = new TrainingStats(System.currentTimeMillis());
        TestStats testStats = new TestStats();

        int fileIteration = 0;
        BigDecimal score = null;

        List<InputsAndTarget> inputsAndTargets = netSpec.getNetData().getTrainData();
        for (fileIteration = 0; fileIteration < netSpec.getNumberFileIterations(); fileIteration++) {

            // train all data rows for numberRowIterations times.
            for (int lcv = 0; lcv < inputsAndTargets.size() && !net.hasTimeExpired(); lcv++) {
                InputsAndTarget inputsAndTarget = inputsAndTargets.get(lcv);
                net.train(inputsAndTarget.getInputs(),
                          inputsAndTarget.getTarget(),
                          netSpec.getNumberRowIterations(),
                          netSpec.getLearningConstant());
            }

            if (net.hasTimeExpired()) {
                trainingStats.stopReason = TrainingStopReason.OUT_OF_TIME;
                break;
            }

            // compute total score
            score = BigDecimal.ZERO;
            for (int lcv = 0; lcv < inputsAndTargets.size(); lcv++) {
                InputsAndTarget inputsAndTarget = inputsAndTargets.get(lcv);
                net.setInputs(inputsAndTarget.getInputs());
                // Math.pow((targets.get(lcv) - r.getOutput()), 2)
                BigDecimal difference = inputsAndTarget.getTarget().subtract(net.getOutput());
                score = score.add(difference.multiply(difference));
            }
            score = score.multiply(BigDecimal.valueOf(-1.0));
            score = score.divide(BigDecimal.valueOf(inputsAndTargets.size()), BigDecimals.MATH_CONTEXT);

            // if (score > -1 * cutoff)
            //   ==> if (score - (-1 * cutoff) > 0)
            //     ==> if (min(score - (-1 * cutoff), 0) == 0)
            if (BigDecimal.ZERO.min(
                    score.subtract(BigDecimal.valueOf(-1.0).multiply(netSpec.getPerformanceCutoff())))
                        .equals(BigDecimal.ZERO)) {
                trainingStats.stopReason = TrainingStopReason.HIT_PERFORMANCE_CUTOFF;
                break;
            }
            // if (score > maxScore)
            //   ==> if (score - maxScore > 0)
            //     ==> if (min(score - maxScore, 0) == 0)
            if (BigDecimal.ZERO.min(score.subtract(trainingStats.maxScore)).equals(BigDecimal.ZERO)) {
                trainingStats.maxScore = score;
            } else if (fileIteration < netSpec.getMinTrainingRounds()) {
                continue;
            } else {
                trainingStats.brokeAtLocalMax = true;
                break;
            }

            // FIXME: wtf is this doing?
            if (testing && fileIteration % trainingStats.displayRounds == 0) {
                updateAndLogDisplayRound(verbose, saveFile, testing, netSpec, net,
                        trainingStats, fileIteration,
                        score, testStats, inputsAndTargets);
            }
        }
        logAfterTraining(verbose, net, trainingStats, fileIteration, score, inputsAndTargets);
        if (trainingStats.brokeAtLocalMax) {
            long timeExpired = System.currentTimeMillis() - net.timingInfo.getStartTime();
            long timeRemaining = trainingTimeLimitMillis - timeExpired;
            System.out.println("Retraining net from file " + fileName + " with " +
                    TimeUtils.formatSeconds((int)timeRemaining/1000) + " remaining.");
            net = RectNetFixed.trainFile(fileName, verbose, saveFile, testing, timeRemaining, sfType, triesRemaining--);
        }
        int timeExpired = (int)((System.currentTimeMillis() - net.timingInfo.getStartTime())/1000);
        double rmsError = computeRmsError(net, inputsAndTargets);
        net.trainingSummary = new TrainingSummary(trainingStats.stopReason, timeExpired, fileIteration, rmsError);
        return net;
    }

    private static double computeRmsError(RectNetFixed net, List<InputsAndTarget> inputsAndTargets) {
        double totalRmsError = 0;
        for (int lcv = 0; lcv < inputsAndTargets.size(); lcv++) {
            InputsAndTarget inputsAndTarget = inputsAndTargets.get(lcv);
            net.setInputs(inputsAndTarget.getInputs());
            double target = inputsAndTarget.getTarget().doubleValue();
            double actual = net.getOutput().doubleValue();
            double square = Math.pow(target - actual, 2);
            totalRmsError += square;
        }
        if (totalRmsError == 0) {
            return 0;
        } else {
            return Math.sqrt(totalRmsError / (1.0 * inputsAndTargets.size()));
        }
    }

    // FIXME: wtf is this method doing?
    private static void updateAndLogDisplayRound(boolean verbose,
            String saveFile, boolean testing, NetTrainSpecification netSpec,
            RectNetFixed net, TrainingStats trainingStats, int fileIteration, BigDecimal score,
            TestStats testStats, List<InputsAndTarget> inputsAndTargets) {
        int diffCounter = 0;
        int diffCounter2 = 0;
        BigDecimal diffCutoff = BigDecimal.valueOf(.1);
        BigDecimal diffCutoff2 = BigDecimal.valueOf(.05);
        // if (bestCheck > -1 * score)
        //   ==> if (bestCheck - (-1 * score) > 0)
        //     ==> if ( min(bestCheck - (-1 * score), 0) == 0)
        if (BigDecimal.ZERO.min(testStats.bestCheck.subtract(BigDecimal.valueOf(-1).multiply(score))).equals(BigDecimal.ZERO)) {
            RectNetFixed.saveNet(saveFile, net);
            testStats.bestCheck = BigDecimal.valueOf(-1.0).multiply(score);
        }
        if (testing) {
            int idx = saveFile.replaceAll("\\\\", "/").lastIndexOf(
                    "/");
            int idx2 = saveFile.lastIndexOf(
                            ".");
            testStats.testScore = RectNetFixed.testNet(
                    saveFile.substring(0, idx + 1)
                            + "OneThird.augtrain", net, verbose);
            // if (testScore < bestTestCheck)
            //   ==> if (testScore - bestTestCheck < 0)
            //     ==> if ( max(testScore - bestTestCheck, 0) != 0
            if (!BigDecimal.ZERO.max(testStats.testScore.subtract(testStats.bestTestCheck)).equals(BigDecimal.ZERO)) {
                RectNetFixed.saveNet(saveFile.substring(0, idx2)
                        + "Test.augsave", net);
                testStats.bestTestCheck = testStats.testScore;
            }
        }
        for (int lcv = 0; lcv < inputsAndTargets.size(); lcv++) {
            InputsAndTarget inputsAndTarget = inputsAndTargets.get(lcv);
            net.setInputs(inputsAndTarget.getInputs());
            // if (Math.abs(targets.get(lcv) - r.getOutput()) > diffCutoff)
            //   ==> if (Math.abs(targets.get(lcv) - r.getOutput()) - diffCutoff > 0)
            //     ==> if (min(Math.abs(targets.get(lcv) - r.getOutput()) - diffCutoff, 0) == 0)
            if (BigDecimal.ZERO.min(inputsAndTarget.getTarget().subtract(net.getOutput()).abs().subtract(diffCutoff)).equals(BigDecimal.ZERO)) {
                diffCounter++;
            }
            // if (Math.abs(targets.get(lcv) - r.getOutput()) > diffCutoff2)
            //   ==> if (Math.abs(targets.get(lcv) - r.getOutput()) - diffCutoff2 > 0)
            //     ==> if (min(Math.abs(targets.get(lcv) - r.getOutput()) - diffCutoff2, 0) == 0)
            if (BigDecimal.ZERO.min(inputsAndTarget.getTarget().subtract(net.getOutput()).abs().subtract(diffCutoff2)).equals(BigDecimal.ZERO)) {
                diffCounter2++;
            }
        }
        System.out.println(fileIteration + " rounds trained.");
        System.out.println("Current score: " + -1.0 * score.doubleValue());
        System.out.println("Min Score=" + -1.0 * trainingStats.maxScore.doubleValue());
        if (testing) {
            System.out.println("Current Test Score=" + testStats.testScore);
            System.out.println("Min Test Score=" + testStats.bestTestCheck);
        }
        System.out.println("Score change per round=" + (testStats.lastScore.doubleValue() + score.doubleValue())/trainingStats.displayRounds);
        System.out.println("Inputs Over " + diffCutoff + "="
                + diffCounter + " of " + inputsAndTargets.size());
        System.out.println("Inputs Over " + diffCutoff2 + "="
                + diffCounter2 + " of " + inputsAndTargets.size());
        BigDecimal diff = BigDecimal.ZERO;
        for (int lcv = 0; lcv < inputsAndTargets.size(); lcv++) {
            InputsAndTarget inputsAndTarget = inputsAndTargets.get(lcv);
            net.setInputs(inputsAndTarget.getInputs());
            diff = diff.add(net.getOutput().subtract(inputsAndTarget.getTarget()));
        }
        System.out.println("AvgDiff=" + diff.doubleValue() / (1.0 * inputsAndTargets.size()));
        System.out.println("Current learning constant: " + netSpec.getLearningConstant());
        System.out.println("Time elapsed (s): " + (System.currentTimeMillis() - trainingStats.startTime) / 1000.0);
        System.out.println("");
        testStats.lastScore = BigDecimal.valueOf(-1.0).multiply(score);
    }

    private static void logAfterTraining(boolean verbose, RectNetFixed net, TrainingStats trainingStats,
            int fileIteration, BigDecimal score, List<InputsAndTarget> inputsAndTargets) {
        if (verbose) {
            // Information about performance and training.
            if (trainingStats.brokeAtLocalMax) {
                System.out.println("Local max hit.");
            } else if (trainingStats.stopReason == TrainingStopReason.HIT_PERFORMANCE_CUTOFF) {
                System.out.println("Performance cutoff hit.");
            } else if (trainingStats.stopReason == TrainingStopReason.HIT_TRAINING_LIMIT) {
                System.out.println("Training round limit reached.");
            } else if (trainingStats.stopReason == TrainingStopReason.OUT_OF_TIME) {
                System.out.println("Training stopped after running out of time.");
            }
            System.out.println("Rounds trained: " + fileIteration);
            System.out.println("Final score of " + -1.0 * score.doubleValue()
                    / (1.0 * inputsAndTargets.size()));
            System.out.println("Time elapsed (ms): "
                    + ((System.currentTimeMillis() - trainingStats.startTime)));
            // Results
            System.out.println("-------------------------");
            System.out.println("Test Results: ");
            for (int lcv = 0; lcv < inputsAndTargets.size(); lcv++) {
                InputsAndTarget inputsAndTarget = inputsAndTargets.get(lcv);
                net.setInputs(inputsAndTarget.getInputs());
                StringBuilder sb = new StringBuilder();
                sb.append("Input " + lcv).append(" ");
                sb.append("Target: " + inputsAndTarget.getTarget()).append(" ");
                sb.append("Actual: " + net.getOutput().doubleValue());
                System.out.println(sb.toString());
            }
            System.out.println("-------------------------");
        }
    }

    public static NetTrainSpecification parseFile(String fileName, ScaleFunctionType sfType, boolean verbose) {
        if (!Net.validateAUGt(fileName)) {
            System.err.println("File not valid format.");
            throw new IllegalArgumentException("File not valid");
        }
        Path file = Paths.get(fileName);
        NetTrainSpecification.Builder netTrainingSpecBuilder = new Builder();
        netTrainingSpecBuilder.scaleFunctionType(sfType);
        try {
            List<String> fileLines = FileUtils.readLines(file.toFile());
            Validate.isTrue(fileLines.size() >= 4, "Cannot parse file with no data");

            Iterator<String> fileLineIterator = fileLines.iterator();
            parseSizeLine(netTrainingSpecBuilder, fileLineIterator);
            parseTrainingInfoLine(netTrainingSpecBuilder, fileLineIterator);
            // skip the titles line
            fileLineIterator.next();
            while (fileLineIterator.hasNext()) {
                parseDataLine(netTrainingSpecBuilder, fileLineIterator);
            }
        } catch (Throwable t) {
            System.err.println("Unable to parse file " + file);
            t.printStackTrace();
            throw Throwables.propagate(t);
        }
        NetTrainSpecification netTrainingSpec = netTrainingSpecBuilder.build();
        logTrainingFileInfo(fileName, verbose, netTrainingSpec);
        return netTrainingSpec;
    }

    private static void logTrainingFileInfo(String fileName, boolean verbose,
            NetTrainSpecification netTrainingSpec) {
        if (verbose) {
            System.out.println("-------------------------");
            System.out.println("File path: " + fileName);
            System.out.println("Number Inputs: " + netTrainingSpec.getSide());
            System.out.println("Net depth: " + netTrainingSpec.getDepth());
            System.out.println("Number training sets: " + netTrainingSpec.getNetData().getTrainData().size());
            System.out.println("Row iterations: " + netTrainingSpec.getNumberRowIterations());
            System.out.println("File iterations: " + netTrainingSpec.getNumberFileIterations());
            System.out.println("Learning constant: " + netTrainingSpec.getLearningConstant());
            System.out.println("Minimum training rounds: " + netTrainingSpec.getMinTrainingRounds());
            System.out.println("Performance cutoff: " + netTrainingSpec.getPerformanceCutoff());
            System.out.println("-------------------------");
        }
    }

    private static void parseDataLine(
            NetTrainSpecification.Builder netTrainingSpec,
            Iterator<String> fileLineIterator) {
        String dataLine = fileLineIterator.next();
        String[] dataLineSplit = dataLine.split(" ");
        String date = dataLineSplit[0];
        String target = dataLineSplit[1];
        // inputs
        BigDecimal[] input = new BigDecimal[netTrainingSpec.getSide()];
        dataLineSplit = dataLineSplit[2].split(",");
        for (int i = 0; i < netTrainingSpec.getSide(); i++) {
            input[i] = BigDecimal.valueOf(Double.valueOf(dataLineSplit[i]));
        }

        if (target.equalsIgnoreCase("NULL")) {
            netTrainingSpec.addPredictionRow(input, date);
        } else {
            BigDecimal targetVal = BigDecimal.valueOf(Double.valueOf(target));
            netTrainingSpec.addInputAndTarget(input, targetVal, date);
        }
    }

    private static void parseTrainingInfoLine(
            NetTrainSpecification.Builder netTrainingSpec,
            Iterator<String> fileLineIterator) {
        String trainingInfoLine = fileLineIterator.next();
        String[] trainingInfoLineSplit = trainingInfoLine.split(" ");
        trainingInfoLineSplit = trainingInfoLineSplit[1].split(",");
        int rowIter = Integer.valueOf(trainingInfoLineSplit[0]);
        int fileIter = Integer.valueOf(trainingInfoLineSplit[1]);
        BigDecimal learningConstant = BigDecimal.valueOf(Double.valueOf(trainingInfoLineSplit[2]));
        int minTrainingRounds = Integer.valueOf(trainingInfoLineSplit[3]);
        BigDecimal cutoff = BigDecimal.valueOf(Double.valueOf(trainingInfoLineSplit[4]));

        netTrainingSpec.minTrainingRounds(minTrainingRounds);
        netTrainingSpec.learningConstant(learningConstant);
        netTrainingSpec.rowIterations(rowIter);
        netTrainingSpec.fileIterations(fileIter);
        netTrainingSpec.performanceCutoff(cutoff);
    }

    private static void parseSizeLine(
            NetTrainSpecification.Builder netTrainingSpec,
            Iterator<String> fileLineIterator) {
        String sizeLine = fileLineIterator.next();
        String[] sizeLineSplit = sizeLine.split(" ");
        sizeLineSplit = sizeLineSplit[1].split(",");
        netTrainingSpec.side(Integer.valueOf(sizeLineSplit[0]));
        netTrainingSpec.depth(Integer.valueOf(sizeLineSplit[1]));
    }

    /**
     * Input a filename and a neural network to save the neural network as a
     * .augsave file
     *
     * @author TheConnMan
     * @param fileName
     *            Filepath ending in .augsave where the network will be saved
     * @param net
     *            Neural net to be saved
     */
    public static void saveNet(String fileName, RectNetFixed net) {
        try {
            if (!(fileName.toLowerCase().endsWith(".augsave"))) {
                System.err.println("Output file name to save to should end in .augsave");
                return;
            }
            PrintWriter out = new PrintWriter(new FileWriter(fileName));
            out.println("net " + Integer.toString(net.getX()) + ","
                    + Integer.toString(net.getY()));
            String line = "O ";
            for (int j = 0; j < net.getY(); j++) {
                line += net.getOutputNeuronWeight(j) + ",";
            }
            out.println(line.substring(0, line.length() - 1));
            for (int leftCol = 0; leftCol < net.getX() - 1; leftCol++) {
                int rightCol = leftCol + 1;
                for (int rightRow = 0; rightRow < net.getY(); rightRow++) {
                    line = rightCol + " ";
                    for (int leftRow = 0; leftRow < net.getY(); leftRow++) {
                        line += net.neurons[rightCol][rightRow].getWeight(leftRow).doubleValue() + ",";
                    }
                    out.println(line.substring(0, line.length() - 1));
                }
            }
            out.close();
        } catch (IOException e) {
            System.err.println("Error occured opening file to saveNet");
            throw new IllegalArgumentException("Could not open file");
        }
    }

    /**
     * Load a neural network from a .augsave file
     *
     * @author TheConnMan
     * @param fileName
     *            File path to an .augsave file containing a neural network
     * @return Neural network from the .augsave file
     */
    public static RectNetFixed loadNet(String fileName) {
        boolean valid = Net.validateAUGs(fileName);
        if (!valid) {
            System.err.println("File not valid format.");
            throw new RuntimeException("File not valid format");
        }
        // Now we need to pull information out of the augsave file.
        Charset charset = Charset.forName("US-ASCII");
        Path file = Paths.get(fileName);
        String line = null;
        int lineNumber = 1;
        String[] lineSplit;
        String[] edges;
        int side = 0;
        int depth = 0;
        int curCol = 0;
        int curRow = 0;
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            line = reader.readLine();
            try {
                lineSplit = line.split(" ");
                String[] size = lineSplit[1].split(",");
                side = Integer.valueOf(size[1]);
                depth = Integer.valueOf(size[0]);
            } catch (Exception e) {
                System.err.println("Loading failed at line: " + lineNumber);
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            throw new RuntimeException("Failed to load file");
        }
        RectNetFixed net = new RectNetFixed(depth, side);
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            while ((line = reader.readLine()) != null) {
                try {
                    lineSplit = line.split(" ");
                    switch (lineNumber) {
                    case 1:
                        break;
                    case 2:
                        String outputs[] = lineSplit[1].split(",");
                        for (int edgeNum = 0; edgeNum < outputs.length; edgeNum++) {
                            net.output.setWeight(edgeNum,
                                    BigDecimal.valueOf(Double.parseDouble(outputs[edgeNum])));
                        }
                        break;
                    default:
                        curCol = Integer.valueOf(lineSplit[0]);
                        curRow = (lineNumber - 3) % side;
                        edges = lineSplit[1].split(",");
                        for (int edgeNum = 0; edgeNum < edges.length; edgeNum++) {
                            net.neurons[curCol][curRow].setWeight(edgeNum,
                                    BigDecimal.valueOf(Double.parseDouble(edges[edgeNum])));
                        }
                        break;
                    }
                    lineNumber++;
                } catch (Exception e) {
                    System.err.println("Loading failed at line: " + lineNumber);
                }
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            throw new RuntimeException("Failed to load file");
        }
        return net;
    }

    /**
     * @param fileName
     * @param r
     */
    public static BigDecimal testNet(String fileName, RectNetFixed r,
            boolean verbose) {
        boolean valid = Net.validateAUGTest(fileName, r.y);
        if (!valid) {
            System.err.println("File not valid format.");
            throw new RuntimeException("File not valid format");
        }
        // Now we need to pull information out of the augtrain file.
        Charset charset = Charset.forName("US-ASCII");
        Path file = Paths.get(fileName);
        String line = null;
        int lineNumber = 1;
        String[] lineSplit;
        int side = r.y;
        String[] size;
        ArrayList<BigDecimal[]> inputSets = new ArrayList<BigDecimal[]>();
        ArrayList<BigDecimal> targets = new ArrayList<BigDecimal>();
        BigDecimal[] maxMinNums = new BigDecimal[4];
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            while ((line = reader.readLine()) != null) {
                try {
                    lineSplit = line.split(" ");
                    switch (lineNumber) {
                    case 1:
                        String[] temp = lineSplit[1].split(",");
                        for (int j = 0; j < 4; j++) {
                            maxMinNums[j] = BigDecimal.valueOf(Double.valueOf(temp[j + 2]));
                        }
                        break;
                    case 2:
                        size = lineSplit[1].split(",");
                        break;
                    case 3:
                        // Titles
                        break;
                    default:
                        // expected
                        BigDecimal target = BigDecimal.valueOf(Double.valueOf(lineSplit[0]));
                        targets.add(target);
                        // inputs
                        BigDecimal[] input = new BigDecimal[side];
                        size = lineSplit[1].split(",");
                        for (int i = 0; i < side; i++) {
                            input[i] = BigDecimal.valueOf(Double.valueOf(size[i]));
                        }
                        inputSets.add(input);
                        break;
                    }
                    lineNumber++;
                } catch (Exception e) {
                    System.err
                            .println("Training failed at line: " + lineNumber);
                }
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            System.exit(1);
        }
        BigDecimal score = BigDecimal.ZERO;
        for (int lcv = 0; lcv < inputSets.size(); lcv++) {
            r.setInputs(inputSets.get(lcv));
            // score += Math.pow((targets.get(lcv) - r.getOutput()), 2);
            BigDecimal diff = targets.get(lcv).multiply(r.getOutput());
            score = score.add(diff.multiply(diff));
        }
        if (verbose) {
            System.out.println("Final score of " + score.doubleValue()
                    / (1.0 * inputSets.size()));
            // Results

            System.out.println("-------------------------");
            System.out.println("Test Results: ");
            System.out.println("Actual, Prediction");
            score = BigDecimal.ZERO;
            BigDecimal score2 = BigDecimal.ZERO;
            for (int lcv = 0; lcv < inputSets.size(); lcv++) {
                r.setInputs(inputSets.get(lcv));

                BigDecimal tempTarget = (targets.get(lcv).subtract(maxMinNums[3])).multiply(
                        (maxMinNums[0].subtract(maxMinNums[1]))).divide(
                        (maxMinNums[2].subtract(maxMinNums[3])), BigDecimals.MATH_CONTEXT).add(maxMinNums[1]);
                BigDecimal tempOutput = (r.getOutput().subtract(maxMinNums[3])).multiply(
                        (maxMinNums[0].subtract(maxMinNums[1]))).divide(
                        (maxMinNums[2].subtract(maxMinNums[3])), BigDecimals.MATH_CONTEXT).add(maxMinNums[1]);
                System.out.println(tempTarget + "," + tempOutput);
                score = score.add(tempTarget.subtract(tempOutput).abs());
                BigDecimal diff = tempTarget.subtract(tempOutput);
                score2 = score2.add(diff.multiply(diff));
            }
            score = score.divide(BigDecimal.valueOf(inputSets.size()), BigDecimals.MATH_CONTEXT);
            score2 = score2.divide(BigDecimal.valueOf(inputSets.size()), BigDecimals.MATH_CONTEXT);
            System.out.println("-------------------------");
            System.out.println("Average error=" + score);
            System.out.println("Average squared error=" + score2);
        }
        return score.divide(BigDecimal.valueOf(inputSets.size()), BigDecimals.MATH_CONTEXT);
    }

    /**
     *
     * @param trainingFile
     * @param predFile
     * @param verbose
     * @return
     */
    public static BigDecimal predictTomorrow(RectNetFixed r, String trainingFile, String predFile,
            boolean verbose, String saveFile) {
        /*
         * boolean valid = Net.validateAUGPred(predFile, r.y); if (!valid) {
         * System.err.println("File not valid format."); System.exit(1); }
         */
        // Now we need to pull information out of the augtrain file.
        Charset charset = Charset.forName("US-ASCII");
        Path file = Paths.get(predFile);
        String line = null;
        int lineNumber = 1;
        String[] lineSplit;
        BigDecimal maxNum = BigDecimal.ONE, minNum = BigDecimal.ONE, mx = BigDecimal.ONE, mn = BigDecimal.ONE, today = BigDecimal.ZERO;
        ArrayList<BigDecimal[]> inputSets = new ArrayList<BigDecimal[]>();
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            while ((line = reader.readLine()) != null) {
                try {
                    lineSplit = line.split(",");
                    switch (lineNumber) {
                    case 1:
                        mx = BigDecimal.valueOf(Double.valueOf(lineSplit[0]));
                        mn = BigDecimal.valueOf(Double.valueOf(lineSplit[1]));
                        maxNum = BigDecimal.valueOf(Double.valueOf(lineSplit[2]));
                        minNum = BigDecimal.valueOf(Double.valueOf(lineSplit[3]));
                        today = BigDecimal.valueOf(Double.valueOf(lineSplit[4]));
                        break;
                    case 3:
                        boolean valid = Net.validateAUGPred(predFile,
                                lineSplit.length);
                        if (!valid) {
                            System.err.println("File not valid format.");
                            System.exit(1);
                        }
                        BigDecimal[] input = new BigDecimal[lineSplit.length];
                        for (int i = 0; i < lineSplit.length; i++) {
                            input[i] = BigDecimal.valueOf(Double.valueOf(lineSplit[i]));
                        }
                        inputSets.add(input);
                        break;
                    default:
                        break;
                    }
                    lineNumber++;
                } catch (Exception e) {
                    System.err
                            .println("Training failed at line: " + lineNumber);
                }
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            System.exit(1);
        }
        r.setInputs(inputSets.get(0));
        BigDecimal first = r.getOutput().subtract(minNum);
        BigDecimal second = maxNum.subtract(minNum);
        BigDecimal third = mx.subtract(mn);
        BigDecimal scaledValue = first.divide(second, BigDecimals.MATH_CONTEXT).multiply(third).add(mn);
        System.out.println("Today's price is $" + today);
        System.out.println("Tomorrow's price/change predicted to be $" + scaledValue);
        return scaledValue;
    }

    public static void main(String[] args) throws InterruptedException {
        //What the net was trained for - prediction or twoThirds/oneThird
        boolean predict=false;
        //Which file system - root or local
        boolean root=false;
        //Where the net comes from - training of loading
        boolean train=true;

        String prefix, trainingFile, trainingFile2, predFile, testFile, savedFile;
        if (root) {
            prefix = "/root/Core/java/nets/test_files/";
        } else {
            prefix = "D:\\Users\\TheConnMan\\git\\Core\\java\\nets\\test_files\\";
        }
        trainingFile = prefix + "Train_1_Day.augtrain";
        predFile = prefix + "Pred_1_Day.augpred";
        savedFile = prefix + "TwoThirdsTrained.augsave";
        trainingFile2 = prefix + "TwoThirds.augtrain";
        testFile = prefix + "OneThird.augtrain";
        RectNetFixed r;
        if (train && predict) {
            r = RectNetFixed.trainFile(trainingFile, false, savedFile, false, 1000000L, ScaleFunctionType.LINEAR);
        } else if (train && !predict) {
            r = RectNetFixed.trainFile(trainingFile2, false, savedFile, true, 1000000L, ScaleFunctionType.LINEAR);
        } else {
            r = RectNetFixed.loadNet(savedFile);
        }
        if (predict) {
            // Predict
            RectNetFixed.predictTomorrow(r, trainingFile, predFile, true, savedFile);
        } else {
            //Test
            RectNetFixed.testNet(testFile, r, true);
        }
        System.exit(0);
    }

    @Override
    public String toString() {
        return "RectNetFixed [x=" + x + ", y=" + y + ", verbose=" + verbose
                + "]";
    }

}
