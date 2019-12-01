package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.AttributesType;
import model.DescriptorMetaData;
import model.Graph;
import model.Vertex;

public class GraphBuilder {
	public static final String JSON_VERTICES_KEY = "vertices";
	public static final String JSON_EDGES_KEY = "edges";
	public static final String JSON_DESCRIPTORS_META_DATA_KEY = "descriptorsMetaData";
	public static final String JSON_ID_VERTICES_KEY = "vertexId";
	public static final String JSON_CONNECTED_EDGES_KEY = "connected_vertices";
	public static final String JSON_DESCRIPTOR_NAME_KEY = "descriptorName";
	public static final String JSON_ATTRIBUTES_TYPE_KEY = "attributesType";
	public static final String JSON_ATTRIBUTES_NAME_KEY = "attributesName";
	public static final String JSON_DESCRIPTOR_VALUES_KEY = "descriptorsValues";

	private String graphFilePath;

	public GraphBuilder(String graphFilePath) {
		this.graphFilePath = graphFilePath;
	}

	public Graph build() {
		String fileAsString = "";
		Graph builtGraph = null;
		System.out.println("load file");
		try {
			BufferedReader graphFile = new BufferedReader(new FileReader(new File(graphFilePath)));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = graphFile.readLine()) != null) {
				sb.append(line);
		        sb.append("\n");
			}
			fileAsString=sb.toString();
			JSONObject graphAsJson = new JSONObject(fileAsString);
			JSONArray jsonVerticesArray = graphAsJson.getJSONArray(JSON_VERTICES_KEY);
			JSONArray jsonEdgesArray = graphAsJson.getJSONArray(JSON_EDGES_KEY);
			JSONArray jsonDescriptorsMetaDataArray = graphAsJson.getJSONArray(JSON_DESCRIPTORS_META_DATA_KEY);
			builtGraph = new Graph(jsonDescriptorsMetaDataArray.length(), jsonVerticesArray.length());
			// load descriptors metadata
			System.out.println("load descriptors metadata");
			for (int i = 0; i < jsonDescriptorsMetaDataArray.length(); i++) {
				JSONArray attributesNamesArray = ((JSONObject) jsonDescriptorsMetaDataArray.get(i))
						.getJSONArray(JSON_ATTRIBUTES_NAME_KEY);
				String[] attributesNames = new String[attributesNamesArray.length()];
				for (int j = 0; j < attributesNamesArray.length(); j++) {
					attributesNames[j] = (String) attributesNamesArray.get(j);
				}
				AttributesType type = AttributesType.getFromString(
						((JSONObject) jsonDescriptorsMetaDataArray.get(i)).getString(JSON_ATTRIBUTES_TYPE_KEY));

				DescriptorMetaData descriptor = null;
				descriptor = new DescriptorMetaData(
						((JSONObject) jsonDescriptorsMetaDataArray.get(i)).getString(JSON_DESCRIPTOR_NAME_KEY), type,
						attributesNames);
				builtGraph.getDescriptorsMetaData()[i] = descriptor;
			}
			HashSet<String> setOfIds = new HashSet<>();
			// load vertices
			System.out.println("load vertices");
			for (int i = 0; i < jsonVerticesArray.length(); i++) {
				JSONObject vertexAsJSon = (JSONObject) jsonVerticesArray.get(i);
				String vertexId = vertexAsJSon.getString(JSON_ID_VERTICES_KEY);
				JSONArray descriptorsValuesJSon = vertexAsJSon.getJSONArray(JSON_DESCRIPTOR_VALUES_KEY);
				double[][] descriptorsValues = new double[descriptorsValuesJSon.length()][];
				for (int j = 0; j < descriptorsValuesJSon.length(); j++) {
					JSONArray currentvaluesArray = (JSONArray) descriptorsValuesJSon.get(j);
					if (currentvaluesArray
							.length() != builtGraph.getDescriptorsMetaData()[j].getAttributesName().length) {
						new RuntimeException("number of values is not correct");
					}
					descriptorsValues[j] = new double[currentvaluesArray.length()];
					for (int k = 0; k < currentvaluesArray.length(); k++) {
						descriptorsValues[j][k] = currentvaluesArray.getDouble(k);
					}
				}
				setOfIds.add(vertexId);
				builtGraph.getVertices()[i] = new Vertex(vertexId, descriptorsValues);
			}
			builtGraph.sortVertices();
			System.out.println("load edges");
			// load edges
			for (int i = 0; i < jsonEdgesArray.length(); i++) {
				String currentVerticeId = ((JSONObject) jsonEdgesArray.get(i)).getString(JSON_ID_VERTICES_KEY);
				int currentVertexIndex = builtGraph.getIndicesOfVertices().get(currentVerticeId);
				JSONArray connectedEdgesArray = ((JSONObject) jsonEdgesArray.get(i))
						.getJSONArray(JSON_CONNECTED_EDGES_KEY);
				for (int j = 0; j < connectedEdgesArray.length(); j++) {
					int otherVertexIndex = builtGraph.getIndicesOfVertices().get((String) connectedEdgesArray.get(j));
					builtGraph.getVertices()[currentVertexIndex].getSetOfNeighborsId().add(otherVertexIndex);
					builtGraph.getVertices()[otherVertexIndex].getSetOfNeighborsId().add(currentVertexIndex);
				}
			}
			for (Vertex v : builtGraph.getVertices()) {
				v.setupNeighborsIds(builtGraph.getVertices().length);
			}
			graphFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return builtGraph;
	}
}
