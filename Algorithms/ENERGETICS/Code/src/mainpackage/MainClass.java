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
import model.Graph;

public class MainClass {
	public static final String PARAMETERS_FILE_NAME="inputParameters.txt";
	public static String inputFilePath="FoursquareGraph.json";
	public static String indicatorsFilePath="resultIndicatorFile.txt";
	public static void main(String[] args) {
		DesignPoint designPoint=new DesignPoint();
		readParametersFromFile(designPoint);
		System.out.println("input file path : "+inputFilePath);
		Graph graph = new GraphBuilder(inputFilePath).build();
		System.out.println(graph.getVertices().length);
		long startTime = System.currentTimeMillis();
		PatternComputer computer = new PatternComputer(new MeasureComputer(graph));
		computer.computePatterns(designPoint);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println(elapsedTime);
		writeResultIndicators(computer,elapsedTime);
		computer.writeResultInFile();
		
	}
	private static void writeResultIndicators(PatternComputer computer, long elapsedTime) {
		try {
			BufferedWriter resultIndicatorFile = new BufferedWriter(new FileWriter(indicatorsFilePath));
			resultIndicatorFile.write("verified characteristics number : "+computer.nbVerifiedCharacteristics+"\n");
			resultIndicatorFile.write("explored characteristics number : "+computer.nbExploredCharacteristics+"\n");
			resultIndicatorFile.write("explored subgraphes number : "+computer.nbExploredHyperzones+"\n");
			resultIndicatorFile.write("found patterns total number : "+computer.nbFoundPatterns+"\n");
			resultIndicatorFile.write("execution time (ms) : "+elapsedTime);
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
					inputFilePath= elements[1];
					break;
				case "delta":
					// threshold
					designPoint.setThreshold(Double.parseDouble(elements[1]));
					break;
				case "mincov":
					designPoint.setMinCovering(Double.parseDouble(elements[1]));
					break;
				case "sigma":
					// minSizeSubGraph					
					designPoint.setMinSizeSubgraph(Integer.parseInt(elements[1]));
					break;
				case "activateUB1":
					designPoint.setActivateUB1(Boolean.parseBoolean(elements[1]));
					break;
				case "activateVariateHyperzoneNaively":
					designPoint.setActivateVariateHyperzoneNaively(Boolean.parseBoolean(elements[1]));
					break;
				case "activateCharacFailFirstPrinciple":
					designPoint.setActivateCharacFailFirstPrinciple(Boolean.parseBoolean(elements[1]));
					break;
				case "activateUB2":
					designPoint.setActivateUB2(Boolean.parseBoolean(elements[1]));
					break;
				case "activateUB3":
					designPoint.setActivateUB3(Boolean.parseBoolean(elements[1]));
					break;
				case "activateSiblingBasedUB":
					designPoint.setActivateSiblingBasedUB(Boolean.parseBoolean(elements[1]));
					break;
				case "activateCoveringPruning":
					designPoint.setActivateCoveringPruning(Boolean.parseBoolean(elements[1]));
					break;
				case "activateSMinus":
					designPoint.setActivateSMinus(Boolean.parseBoolean(elements[1]));
					break;
				case "activateSPlus":
					designPoint.setActivateSPlus(Boolean.parseBoolean(elements[1]));
					break;
				case "activateUniversalClosure":
					designPoint.setActivateUniversalClosure(Boolean.parseBoolean(elements[1]));
					break;
				case "pruneWithConnection":
					designPoint.setPruneWithConnection(Boolean.parseBoolean(elements[1]));
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
