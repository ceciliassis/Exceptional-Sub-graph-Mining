package controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import model.DesignPoint;
import model.Graph;
import model.Pattern;
import model.Vertex;

public class GraphGenerator {
	private DesignPoint designPoint;
	private String graphFilePath = "generatedGraph.json";
	private String graphMetaDataPath = "GeneratedGraphMetaData.json";
	private Random random = new Random();
	private ArrayList<Pattern> listOfPatterns = new ArrayList<>();
	private Graph graph;

	public GraphGenerator() {

	}
	
	public DesignPoint getDesignPoint() {
		return designPoint;
	}

	public void setDesignPoint(DesignPoint designPoint) {
		this.designPoint = designPoint;
	}



	public GraphGenerator setGraphFilePath(String graphFilePath) {
		this.graphFilePath = graphFilePath;
		return this;
	}

	public GraphGenerator setGraphMetaDataPath(String graphMetaDataPath) {
		this.graphMetaDataPath = graphMetaDataPath;
		return this;
	}

	public void generate(DesignPoint designPoint) {
		this.designPoint=designPoint;
		int generatedEdges = 0;
		int widthOfGraph = (int) Math.floor(Math.sqrt(designPoint.getNumberOfVertices()));
		int heightOfGraph = (int) Math.ceil((double) designPoint.getNumberOfVertices() / (double) widthOfGraph);
		designPoint.setNumberOfVertices(widthOfGraph*heightOfGraph);
		if (designPoint.getNumberOfEdges()>(designPoint.getNumberOfVertices()*(designPoint.getNumberOfVertices()-1)/2)){
			designPoint.setNumberOfEdges(designPoint.getNumberOfVertices()*(designPoint.getNumberOfVertices()-1)/2);
		}
		graph = new Graph(designPoint.getNumberOfAttributes(), heightOfGraph, widthOfGraph);
		double factor = 1;
		for (int i = 0; i < heightOfGraph; i++) {
			for (int j = 0; j < widthOfGraph; j++) {
				graph.getTableOfVertices()[i][j] = new Vertex(designPoint.getNumberOfAttributes(), "V(" + i + "," + j + ")");
				for (int k = 0; k < designPoint.getNumberOfAttributes(); k++) {
					double value = randDouble(designPoint.getMinAttValue() * factor, designPoint.getMaxAttValue() * factor);
					graph.getTableOfVertices()[i][j].getDescriptorValues()[k] = value;
				}
			}
		}

		int widthOfPattern = (int) Math.floor(Math.sqrt(designPoint.getPatternVerticesSize()));
		int heightOfPattern = (int) Math.ceil((double) designPoint.getPatternVerticesSize() / (double) widthOfPattern);
		int minColumnIndex = 0;
		int minRowIndex = 0;
		int currentUsedAttribute = 0;
		for (int patternIndex = 0; patternIndex < designPoint.getNumberOfPatterns(); patternIndex++) {
			if (patternIndex > 0) {
				if (minColumnIndex + widthOfPattern * 2 < widthOfGraph) {
					minColumnIndex += widthOfPattern + 1;
				} else if (minRowIndex + heightOfPattern * 2 < heightOfGraph) {
					minColumnIndex = 0;
					minRowIndex += heightOfPattern + 1;
				} else {
					new RuntimeException("there is no sufficient vertices to place all patterns separately");
				}
			}
			int numberOfPositiveAttributes = designPoint.getPatternAttributesHalfSize();
			int numberOfNegativeAttributes = designPoint.getPatternAttributesHalfSize();
			HashSet<Integer> positiveAttributes = new HashSet<>();
			HashSet<Integer> negativeAttributes = new HashSet<>();
			if (currentUsedAttribute + numberOfPositiveAttributes + numberOfNegativeAttributes > designPoint.getNumberOfAttributes()) {
				currentUsedAttribute = 0;
				System.out.println("return to 0 for attributes index");
			}
			for (int i = currentUsedAttribute; i < numberOfPositiveAttributes + currentUsedAttribute; i++) {
				positiveAttributes.add(i);
			}
			currentUsedAttribute += numberOfPositiveAttributes;

			for (int i = currentUsedAttribute; i < currentUsedAttribute + numberOfNegativeAttributes; i++) {
				negativeAttributes.add(i);
			}
			currentUsedAttribute += numberOfNegativeAttributes;
			if (currentUsedAttribute >= designPoint.getNumberOfAttributes()) {
				new RuntimeException("there is no sufficient attributes to place all patterns separately");
			}
			Pattern pattern = new Pattern(positiveAttributes, negativeAttributes, designPoint.getPatternVerticesSize(), minColumnIndex,
					minColumnIndex + widthOfPattern - 1, minRowIndex, minRowIndex + heightOfPattern - 1,
					designPoint.getPatternContrastRate());
			listOfPatterns.add(pattern);
			generatedEdges += pattern.addPatternToGraph(graph);
		}
		// generate the rest of edges :
		while (generatedEdges < designPoint.getNumberOfEdges()) {
			int rowOfFirst = randInt(0, heightOfGraph - 1);
			int columnOfFirst = randInt(0, widthOfGraph - 1);
			int rowOfSecond = randInt(0, heightOfGraph - 1);
			int columnOfSecond = randInt(0, widthOfGraph - 1);
			if (rowOfFirst != rowOfSecond || columnOfFirst != columnOfSecond) {
				if (!graph.getTableOfVertices()[rowOfFirst][columnOfFirst].getNeighbors()
						.contains(graph.getTableOfVertices()[rowOfSecond][columnOfSecond].getVertexId())) {
					graph.getTableOfVertices()[rowOfFirst][columnOfFirst].getNeighbors()
							.add(graph.getTableOfVertices()[rowOfSecond][columnOfSecond].getVertexId());
					graph.getTableOfVertices()[rowOfSecond][columnOfSecond].getNeighbors()
					.add(graph.getTableOfVertices()[rowOfFirst][columnOfFirst].getVertexId());
					generatedEdges++;
				}
			}
		}
		designPoint.setNumberOfEdges(generatedEdges);

		writeResult(widthOfGraph, heightOfGraph);

	}

	private void writeResult(int widthOfGraph, int heightOfGraph) {
		try {
			BufferedWriter graphFile = new BufferedWriter(new FileWriter(graphFilePath));
			writeInJSONFile(graphFile, widthOfGraph, heightOfGraph);
			graphFile.close();

			BufferedWriter graphMetaDataFile = new BufferedWriter(new FileWriter(graphMetaDataPath));
			graphMetaDataFile.write(getMetaDataAsJSonString(widthOfGraph, heightOfGraph));
			graphMetaDataFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void writeInJSONFile(BufferedWriter jsonFile, int widthOfGraph, int heightOfGraph) throws IOException {
		jsonFile.write("{\n");
		jsonFile.write("\tdescriptorsMetaData : [\n");
		jsonFile.write("\t\t{\n");
		jsonFile.write("\t\t\tdescriptorName: \"nominalDescriptor\",\n");
		jsonFile.write("\t\t\tattributesType: \"nominal\",\n");
		jsonFile.write("\t\t\tattributesName: [");
		for (int i = 0; i < designPoint.getNumberOfAttributes(); i++) {
			if (i > 0) {
				jsonFile.write(",");
			}
			jsonFile.write("\"att" + i + "\"");
		}
		jsonFile.write("]\n");
		jsonFile.write("\t\t}\n");
		jsonFile.write("\t],\n");
		jsonFile.write("\tvertices : [\n");
		for (int i = 0; i < heightOfGraph; i++) {
			for (int j = 0; j < widthOfGraph; j++) {
				if (i > 0 || j > 0) {
					jsonFile.write(",\n");
				}
				jsonFile.write(graph.getTableOfVertices()[i][j].toJsonWithoutReturnLign("\t\t"));
			}
		}
		jsonFile.write("\n\t],\n");
		jsonFile.write("\tedges : [\n");
		for (int i = 0; i < heightOfGraph; i++) {
			for (int j = 0; j < widthOfGraph; j++) {
				if (i > 0 || j > 0) {
					jsonFile.write(",\n");
				}
				jsonFile.write("\t\t{\n");
				jsonFile.write("\t\t\tvertexId : \"" + graph.getTableOfVertices()[i][j].getVertexId() + "\",\n");
				jsonFile.write("\t\t\tconnected_vertices : [");
				boolean firstInsert = true;
				for (String neighborId : graph.getTableOfVertices()[i][j].getNeighbors()) {
					if (firstInsert) {
						firstInsert = false;
					} else {
						jsonFile.write(",");
					}
					jsonFile.write("\"" + neighborId + "\"");
				}
				jsonFile.write("]\n");
				jsonFile.write("\t\t}");

			}
		}
		jsonFile.write("\n\t]\n}");
	}

	private double randDouble(double minimum, double maximum) {
		return minimum + random.nextDouble() * (maximum - minimum);
	}

	private int randInt(int minimum, int maximum) {
		return random.nextInt((maximum - minimum) + 1) + minimum;
	}

	private String getMetaDataAsJSonString(int widthOfGraph, int heightOfGraph) {
		String metaData = "{\n";
		metaData += "\tnumberOfVertices : " + ((int) (widthOfGraph * heightOfGraph)) + ",\n";
		metaData += "\tnumberOfEdges : " + designPoint.getNumberOfEdges() + ",\n";
		metaData += "\tnumberOfAttributes : " + designPoint.getNumberOfAttributes() + ",\n";
		metaData += "\tnumberOfPatterns : " + designPoint.getNumberOfPatterns() + ",\n";
		metaData += "\tpatternVerticesSize : " + designPoint.getPatternVerticesSize() + ",\n";
		metaData += "\tpatternAttributesHalfSize : " + designPoint.getPatternAttributesHalfSize() + ",\n";
		metaData += "\tminAttValue : " + designPoint.getMinAttValue() + ",\n";
		metaData += "\tmaxAttValue : " + designPoint.getMaxAttValue() + ",\n";
		metaData += "\tgeneratedPatterns : [\n";
		boolean firstInsert = true;
		for (Pattern pattern : listOfPatterns) {
			if (firstInsert) {
				firstInsert = false;
			} else {
				metaData += ",\n";
			}
			metaData += pattern.toJSon("\t\t");
		}
		metaData += "\n\t]\n}";
		return metaData;
	}
}
