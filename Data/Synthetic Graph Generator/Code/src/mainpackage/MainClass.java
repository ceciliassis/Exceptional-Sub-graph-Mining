package mainpackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import controller.GraphGenerator;
import model.DesignPoint;

public class MainClass {
	public static final String PARAMETERS_FILE_NAME="inputParameters.txt";
	
	public static void main(String[] args) {
		DesignPoint designPoint=new DesignPoint();
		readParametersFromFile(designPoint);
		new GraphGenerator().generate(designPoint);
	}

	private static void readParametersFromFile(DesignPoint designPoint) {
		try {
			BufferedReader parametersFile = new BufferedReader(new FileReader(new File(PARAMETERS_FILE_NAME)));
			String line;
			while ((line = parametersFile.readLine()) != null) {
				String[] elements = line.split("=");
				switch (elements[0]) {
				case "numberOfVertices":
					designPoint.setNumberOfVertices(Integer.parseInt(elements[1]));
					break;
				case "numberOfEdges":
					designPoint.setNumberOfEdges(Integer.parseInt(elements[1]));
					break;
				case "numberOfAttributes":
					designPoint.setNumberOfAttributes(Integer.parseInt(elements[1]));
					break;
				case "numberOfPatterns":
					designPoint.setNumberOfPatterns(Integer.parseInt(elements[1]));
					break;
				case "patternVerticesSize":
					designPoint.setPatternVerticesSize(Integer.parseInt(elements[1]));
					break;
				case "patternAttributesHalfSize":
					designPoint.setPatternAttributesHalfSize(Integer.parseInt(elements[1]));
					break;
				case "minAttValue":
					designPoint.setMinAttValue(Double.parseDouble(elements[1]));
					break;
				case "maxAttValue":
					designPoint.setMaxAttValue(Double.parseDouble(elements[1]));
					break;
				case "patternContrastRate":
					designPoint.setPatternContrastRate(Double.parseDouble(elements[1]));
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
