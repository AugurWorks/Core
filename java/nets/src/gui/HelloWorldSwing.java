package gui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class HelloWorldSwing {
	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		BorderLayout experimentLayout = new BorderLayout(5, 5);
		JFrame frame = new JFrame("Alfred");
		frame.setLayout(experimentLayout);

		Font def = new Font("Verdana", Font.PLAIN, 12);
		Font fieldFont = new Font("Verdana", Font.PLAIN, 10);

		// LEFT
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		frame.add(left, BorderLayout.WEST);

		// RIGHT
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		frame.add(right, BorderLayout.EAST);

		// TRAIN A NET
		JLabel train = new JLabel("Train a net");
		train.setFont(new Font("Verdana", Font.BOLD, 14));
		frame.add(train, BorderLayout.PAGE_START);

		// ROW 1
		JLabel fileNameLabel = new JLabel("AUGTRAIN File Location: ");
		fileNameLabel.setFont(def);
		left.add(fileNameLabel);

		JTextField augtrainLocation = new JTextField(
				"absolute/file/path.augtrain");
		augtrainLocation.setFont(fieldFont);
		right.add(augtrainLocation);

		// ROW 2
		JLabel outputFileNameLabel = new JLabel("Output File Location: ");
		outputFileNameLabel.setFont(def);
		left.add(outputFileNameLabel);

		JTextField augsaveLocation = new JTextField(
				"absolute/file/path.augsave");
		augsaveLocation.setFont(fieldFont);
		right.add(augsaveLocation);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Display the window.
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}