/**
 * Stephen Freiberg
 * AugurWorks Confidential
 * C implementation of Neural Networks
 **/

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <assert.h>
#include <math.h>
#include <time.h>
#include <float.h>
#include <stdbool.h>


/** 
 * Put the desired parameters here for now.
 **/
#define NUMINPUTS (2)
#define WIDTH (2)
#define VERBOSE (true)
#define DEBUGMODE (false)
#define ALPHA (-3.0)
#define ITERATIONS (1000000)
#define LEARNINGCONSTANT (1.0)
#define PERFCUTOFF (0.0000001)
#define MINTRAININGROUNDS (100)
#define LITTLE_ENDIAN 1

/** 
 * Fuzzy exponentials.
 **/
static union {
	double d;
	struct {
		#ifdef LITTLE_ENDIAN
		int j,i;
		#else 
		int i,j;
		#endif
	} n;
} _eco;
#define EXP_A (1048576/0.69314718055994530942)
#define EXP_C 60801
#define EXP(y) (_eco.n.i = EXP_A*(y) + (1072693248 - EXP_C), _eco.d)

struct neuron {
	double weights[NUMINPUTS];
	int i;
	double lastOutput;
};

/**
 * Define the global variables
 * neurons[ colNum * NUMINPUTS + rowNum ]
 **/
struct neuron neurons[NUMINPUTS * WIDTH];
struct neuron outputNeuron;
double inputs[NUMINPUTS];

void simpleInputs() {
	for (int j = 0; j < NUMINPUTS; j++) {
		inputs[j] = j;
	}
}

double sigmoid(double d) {
	return (1.0) / (1.0 + EXP(ALPHA * d));
}

int init() {
	// Initialize RANDOM
	srand(time(NULL));
	for (int i = 0; i < NUMINPUTS * WIDTH; i++) {
		neurons[i].i = i;
		// Initialize the random weights
		for (int weightID = 0; weightID < NUMINPUTS; weightID++) {
			neurons[i].weights[weightID] = rand() / (1.0*RAND_MAX);
		}
	}
	// By convention, output will be (-1)
	outputNeuron.i = -1;
	for (int weightID = 0; weightID < NUMINPUTS; weightID++) {
		outputNeuron.weights[weightID] = rand() / (1.0*RAND_MAX);
	}
	return 0;
}

double getOutput() {
	double output = 0;
	// Outs from column i will live here
	double outs[NUMINPUTS];
	// Inputs to column i will live here
	double ins[NUMINPUTS];
	// Fill ins with inputs (for column -1)
	for (int j = 0; j < NUMINPUTS; j++) {
		ins[j] = inputs[j];
	}
	// Fill outs with info from column 0
	for (int column = 0; column < WIDTH; column++) {
		for (int row = 0; row < NUMINPUTS; row++) {
			outs[row] = 0;
			for (int rowLeft = 0; rowLeft < NUMINPUTS; rowLeft++) {
				// convolve the thing
				outs[row] += neurons[column * NUMINPUTS + row].weights[rowLeft] * ins[rowLeft];
			}
			outs[row] = sigmoid(outs[row]);
			neurons[column * NUMINPUTS + row].lastOutput = outs[row];
		}
		for (int row = 0; row < NUMINPUTS; row++) {
			ins[row] = outs[row];
			outs[row] = 0;
		}
	}
	for (int rowLeft = 0; rowLeft < NUMINPUTS; rowLeft++) {
		// convolve the thing
		output += outputNeuron.weights[rowLeft] * ins[rowLeft];
	}
	output = sigmoid(output);
	outputNeuron.lastOutput = output;
	return output;
}

void setInputs(double inpts[]) {
	for (int j = 0; j < NUMINPUTS; j++) {
		inputs[j] = inpts[j];
	}
}

double outputError(double desired) {
	getOutput();
	return outputNeuron.lastOutput * (1.0 - outputNeuron.lastOutput) * (desired - outputNeuron.lastOutput);
}

void train(double inpts[], double desired, int iterations, double learningConstant) {
	assert(iterations > 0);
	for (int iteration = 0; iteration < iterations; iteration++) {
		// set the inputs
		setInputs(inpts);
		// compute the last node error
		double deltaF = outputError(desired);
		if (DEBUGMODE) {
			printf("DeltaF: %lf", deltaF);
		}
		// For each interior node, compute the weighted error
		// Deltas are of the form
		// deltas[ col * NUMINPUTS + row ]
		double deltas[NUMINPUTS * (WIDTH + 1)];
		// spoof the rightmost deltas
		for (int j = 0; j < NUMINPUTS; j++) {
			deltas[j + WIDTH * NUMINPUTS] = deltaF;
		}
		int leftCol = 0;
		int leftRow = 0;
		int rightCol = 0;
		int rightRow = 0;
		for (leftCol = WIDTH - 1; leftCol >= 0; leftCol--) {
			rightCol = leftCol + 1;
			for (leftRow = 0; leftRow < NUMINPUTS; leftRow++) {
				double lastOut = neurons[leftCol * NUMINPUTS + leftRow].lastOutput;
				double delta = lastOut * (1.0 - lastOut);
				double summedRightWeightDelta = 0;
				for (rightRow = 0; rightRow < NUMINPUTS; rightRow++) {
					if (rightCol == WIDTH) { 
						summedRightWeightDelta += outputNeuron.weights[leftRow] * deltaF;
					} else {
						summedRightWeightDelta += neurons[rightCol * NUMINPUTS + rightRow].weights[leftRow] * deltas[rightCol * NUMINPUTS + rightRow];
					}
				}
				deltas[leftCol * NUMINPUTS + leftRow] = delta * summedRightWeightDelta;
				if (DEBUGMODE) {
				}
			}
		}
		// now that we have the deltas, we can change the weights
		// special case the last neuron
		for (int j = 0; j < NUMINPUTS; j++) {
			double dw = learningConstant * neurons[(WIDTH-1)*NUMINPUTS + j].lastOutput * deltaF;
			outputNeuron.weights[j] += dw;
		}
		// and the internals
		for (leftCol = WIDTH - 2; leftCol >= 0; leftCol--) {
			rightCol = leftCol + 1;
			for (leftRow = 0; leftRow < NUMINPUTS; leftRow++) {
				for (rightRow = 0; rightRow < NUMINPUTS; rightRow++) {
					// w' = w + r * i * delta
					// r is learningConstant
					// i is the output from the leftward neuron
					double dw = learningConstant * neurons[leftCol * NUMINPUTS + leftRow].lastOutput * deltas[rightCol * NUMINPUTS + rightRow];
					neurons[rightCol * NUMINPUTS + rightRow].weights[leftRow] += dw;
				}
			}
		}
	}
}

int main() {
	init();
	double or00[NUMINPUTS] = {0.0,0.0};
	double or01[NUMINPUTS] = {0.0,1.0};
	double or10[NUMINPUTS] = {1.0,0.0};
	double or11[NUMINPUTS] = {1.0,1.0};
	double score = 0;
	double maxScore = -DBL_MAX;
	bool brokeAtLocalMax = false;
	bool brokeAtPerfCutoff = false;
	int i;
	clock_t start = clock(), diff;
	for (i = 0; i < ITERATIONS; i++) {
		train(or00, 0.0, 1, LEARNINGCONSTANT);
		train(or01, 1.0, 1, LEARNINGCONSTANT);
		train(or10, 1.0, 1, LEARNINGCONSTANT);
		train(or11, 1.0, 1, LEARNINGCONSTANT);

		score = 0;

		setInputs(or00);
		double d = getOutput();
		score += (0.0 - d) * (0.0 - d);
		setInputs(or01);
		d = getOutput();
		score += (1.0 - d) * (1.0 - d);
		setInputs(or10);
		d = getOutput();
		score += (1.0 - d) * (1.0 - d);
		setInputs(or11);
		d = getOutput();
		score += (1.0 - d) * (1.0 - d);
		score *= -1.0;
		if (score > -1.0 * PERFCUTOFF) {
			brokeAtPerfCutoff = true;
			break;
		}
		if (score > maxScore) {
			maxScore = score;
		} else if (i < MINTRAININGROUNDS) {
			continue;
		} else {
			brokeAtLocalMax = true;
			break;
		}
	}
	diff = clock() - start;
	if (VERBOSE) {
		if (brokeAtLocalMax) {
			printf("Local max hit.\n");
		} else if (brokeAtPerfCutoff) {
			printf("Performance cutoff hit.\n");
		} else {
			printf("Training round limit reached.\n");
		}
		printf("Rounds trained: %d\n",i);
		printf("Final score of %lf\n",-1 * score);
		int msec = diff * 1000 / CLOCKS_PER_SEC;
		printf("Time elapsed: %d seconds %d milliseconds.\n", msec/1000, msec%1000);
		// Results
		printf("-------------------------\n");
		printf("Test Results: \n");
		setInputs(or00);
		double d = getOutput();
		printf("inputs (0,0): %lf\n", d);
		setInputs(or01);
		d = getOutput();
		printf("inputs (0,1): %lf\n", d);
		setInputs(or10);
		d = getOutput();
		printf("inputs (1,0): %lf\n", d);
		setInputs(or11);
		d = getOutput();
		printf("inputs (1,1): %lf\n", d);
		printf("-------------------------\n");
	}
	return 0;
}












