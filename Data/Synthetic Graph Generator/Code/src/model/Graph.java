package model;

public class Graph {
	private Vertex[][] tableOfVertices;
	private int numberOfAttributes;
	private int heightOfGraph;
	private int widthOfGraph;

	public Graph(int numberOfAttributes, int heightOfGraph, int widthOfGraph) {
		this.numberOfAttributes = numberOfAttributes;
		this.heightOfGraph = heightOfGraph;
		this.widthOfGraph=widthOfGraph;
		this.tableOfVertices = new Vertex[heightOfGraph][widthOfGraph];
	}

	public Vertex[][] getTableOfVertices() {
		return tableOfVertices;
	}

	public int getNumberOfAttributes() {
		return numberOfAttributes;
	}

	public int getHeightOfGraph() {
		return heightOfGraph;
	}
	public int getWidthOfGraph() {
		return widthOfGraph;
	}
	
	public String toJson(){
		String jsonString="{\n";
		jsonString+="\tdescriptorsMetaData : [\n";
		jsonString+="\t\t{\n";
		jsonString+="\t\t\tdescriptorName: \"nominalDescriptor\",\n";
		jsonString+="\t\t\tattributesType: \"nominal\",\n";
		jsonString+="\t\t\tattributesName: [";
		for (int i=0;i<numberOfAttributes;i++){
			if (i>0){
				jsonString+=",";
			}
			jsonString+="\"att"+i+"\"";
		}
		jsonString+="]\n";
		jsonString+="\t\t}\n";
		jsonString+="\t],\n";
		jsonString+="\tvertices : [\n";
		for (int i=0;i<heightOfGraph;i++){
			for (int j=0;j<widthOfGraph;j++){
				if (i>0 || j>0){
					jsonString+=",\n";
				}
				jsonString+=tableOfVertices[i][j].toJsonWithoutReturnLign("\t\t");
			}
		}
		jsonString+="\n\t],\n";
		jsonString+="\tedges : [\n";
		for (int i=0;i<heightOfGraph;i++){
			for (int j=0;j<widthOfGraph;j++){
				if (i>0 || j>0){
					jsonString+=",\n";
				}
				jsonString+="\t\t{\n";
				jsonString+="\t\t\tvertexId : \""+tableOfVertices[i][j].getVertexId()+"\",\n";
				jsonString+="\t\t\tconnected_vertices : [";
				boolean firstInsert=true;
				if (i>0){
					jsonString+="\""+tableOfVertices[i-1][j].getVertexId()+"\"";
					firstInsert=false;
				}
				if (j>0){
					if (!firstInsert){
						jsonString+=",";
					}
					jsonString+="\""+tableOfVertices[i][j-1].getVertexId()+"\"";
					firstInsert=false;
				}
				if (i<heightOfGraph-1){
					if (!firstInsert){
						jsonString+=",";
					}
					jsonString+="\""+tableOfVertices[i+1][j].getVertexId()+"\"";
					firstInsert=false;
				}
				if (j<widthOfGraph-1){
					if (!firstInsert){
						jsonString+=",";
					}
					jsonString+="\""+tableOfVertices[i][j+1].getVertexId()+"\"";
					firstInsert=false;
				}
				jsonString+="],\n";
				jsonString+="\t\t}";
				
			}
		}
		jsonString+="\n\t]\n}";
		return jsonString;
		
	}

}
