
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Vinayak Tare
 *
 */
public class Compute_a_posteriori {

	/**
	 * @param args
	 * 
	 */
	private static double h1 = 0.1;
	private static double h2 = 0.2;
	private static double h3 = 0.4;
	private static double h4 = 0.2;
	private static double h5 = 0.1;
	public static String resultFile = "result.txt";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Scanner sc = new Scanner(System.in);
		String observations = args[0];
		char obs[] = observations.toCharArray();
		int cherryCount = 0, limeCount = 0;
		for (int i = 0; i < obs.length; i++) {
			if (obs[i] == 'C' || obs[i] == 'c') {
				cherryCount++;
			} else if (obs[i] == 'L' || obs[i] == 'l') {
				limeCount++;
			} else {
				System.out.println("Wrong input\n Exiting Program");
				System.exit(0);
			}
		}
		double probablities[] = new double[6];
		probablities[1] = Math.pow(0, limeCount) * Math.pow(1, cherryCount) * h1;
		probablities[2] = Math.pow(0.25, limeCount) * Math.pow(0.75, cherryCount) * h2;
		probablities[3] = Math.pow(0.5, limeCount) * Math.pow(0.5, cherryCount) * h3;
		probablities[4] = Math.pow(0.75, limeCount) * Math.pow(0.25, cherryCount) * h4;
		probablities[5] = Math.pow(1, limeCount) * Math.pow(0, cherryCount) * h5;

		double alpha = 0;
		for (int i = 1; i < 6; i++) {
			alpha = alpha + probablities[i];

		}
		alpha = 1 / alpha;
		for (int i = 1; i < 6; i++) {
			probablities[i] = probablities[i] * alpha;
			probablities[i] = Math.round(probablities[i] * 100000);
			probablities[i] = probablities[i] / 100000;

		}
		double candypercent[] = { 0, 0.25, 0.5, 0.75, 1 };
		double nextLimeProbability = 0, nextCherryProbability = 0;
		for (int i = 1; i < 6; i++) {
			nextLimeProbability = nextLimeProbability + (candypercent[i - 1] * probablities[i]);
			nextCherryProbability = nextCherryProbability + ((1 - candypercent[i - 1]) * probablities[i]);
		}

		nextLimeProbability = Math.round(nextLimeProbability * 100000);
		nextLimeProbability = nextLimeProbability / 100000;
		nextCherryProbability = Math.round(nextCherryProbability * 100000);
		nextCherryProbability = nextCherryProbability / 100000;

		/************************************************/

		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(resultFile));
			output.write("Observation sequence Q : " + observations);
			output.write("\r\nLength of Q : " + observations.length());

			for (int j = 0; j < 5; j++) {
				output.write("\r\nP(h" + (j + 1) + "|Q) = " + probablities[j + 1]);
			}
			output.write("\r\nProbability that the next candy we pick will be C, given Q : " + nextCherryProbability);
			output.write("\r\nProbability that the next candy we pick will be L, given Q : " + nextLimeProbability);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}