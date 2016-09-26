package mainpackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import controller.GraphBuilder;
import controller.MeasureComputer;
import controller.PatternComputer;
import model.DesignPoint;
import model.GenerationType;
import model.Graph;

public class MainClass {
	public static final String PARAMETERS_FILE_NAME = "inputParameters.txt";
	public static String inputFilePath = "FoursquareGraph.json";
	public static String indicatorsFilePath = "resultIndicatorFile.txt";

	public static void main(String[] args) {
		DesignPoint designPoint = new DesignPoint();
		readParametersFromFile(designPoint);
		Graph graph = new GraphBuilder(inputFilePath).build();
		System.out.println(graph.getVertices().length);
		System.out.println("input file path : " + inputFilePath);
		long startTime = System.currentTimeMillis();
		PatternComputer computer = new PatternComputer(new MeasureComputer(graph), designPoint);
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				computer.computePatterns();
			}
		});
		thread.start();
		try {
			Thread.sleep(designPoint.getExecutionTimeInMS());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		PatternComputer.continuExecution = false;

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println(elapsedTime);
		computer.removeRedundancies();

		writeResultIndicators(computer, elapsedTime);
		computer.writeResultInFile("retrievedPatternsFile.json");

	}

	private static void writeResultIndicators(PatternComputer computer, long elapsedTime) {
		try {
			BufferedWriter resultIndicatorFile = new BufferedWriter(new FileWriter(indicatorsFilePath));
			resultIndicatorFile.write("explored subgraphes number : " + computer.nbExploredHyperzones + "\n");
			resultIndicatorFile.write("found patterns total number : " + computer.nbFoundPatterns + "\n");
			resultIndicatorFile.write("execution time (ms) : " + elapsedTime);
			resultIndicatorFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void readParametersFromFile(DesignPoint designPoint) {
		try {
			BufferedReader parametersFile = new BufferedReader(new FileReader(new File(PARAMETERS_FILE_NAME)));
			String line;
			while ((line = parametersFile.readLine()) != null) {
				String[] elements = line.split("=");
				switch (elements[0]) {
				case "inputFilePath":
					inputFilePath = elements[1];
					break;
				case "delta":
					// threshold
					designPoint.setThreshold(Double.parseDouble(elements[1]));
					break;
				case "sigma":
					// minSizeSubGraph
					designPoint.setMinSizeSubgraph(Integer.parseInt(elements[1]));
					break;
				case "generationType":
					switch (elements[1]) {
					case "WeighedGeneration":
						designPoint.setGenerationType(GenerationType.WeighedGeneration);
						break;
					case "FrequencyGeneration":
						designPoint.setGenerationType(GenerationType.FreqGeneration);
						break;
					case "UniformGeneration":
						designPoint.setGenerationType(GenerationType.UniformGeneration);
						break;
					}
					break;
				case "activateSMinus":
					designPoint.setActivateSMinus(Boolean.parseBoolean(elements[1]));
					break;
				case "removeRepetition":
					designPoint.setRemoveRepetition(Boolean.parseBoolean(elements[1]));
					break;
				case "executionTimeInMS":
					designPoint.setExecutionTimeInMS(Long.parseLong(elements[1]));
					break;
				default:
					throw new RuntimeException("this parameter is unknown : " + elements[0]);
				}
			}
			parametersFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
