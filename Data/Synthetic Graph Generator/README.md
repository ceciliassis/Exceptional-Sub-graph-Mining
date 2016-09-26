The synthectic graph generator produces synthetic attributed graphs. We used this program in order to perform quantitative experiments. This generator produces graphs in which some subgraphs are exceptional.

The folder "Release" contains the runnable JAR file that can be directly executed. The folder "Code" contains the source code of the program.

## Inputs of the synthetic graph generator
This program requires a file that specifies the generation parameters. This file must be named "inputParameters.txt". It also must be in the same repository as the runnable JAR. The listing below shows an example.
```
numberOfVertices=1000
numberOfEdges=500
numberOfAttributes=10
minAttValue=50
maxAttValue=150
numberOfPatterns=2
patternVerticesSize=4
patternAttributesHalfSize=1
patternContrastRate=0.4
```
Based on the example above, the program will generate a graph that contains 1000 vertices, and 500 edges. Each vertex is described by 10 attributes. The attribute values are between 50 and 150. 2 patterns will be injected in this graph. Each of these patterns concern 4 vertices, and 1 positively contrasted attribute, and 1 negatively contrasted attribute. The contrast of each attribute will be 40% more (or less) than its normal value.


## How to launch the synthetic graph generator
In order to execute the program, the files provided in "Release" can be used. Before launching the runnable JAR, please make sure the parameters file is in the same folder as the runnable JAR. Then, the following command line can be used in this folder:
```
java -jar SyntheticGraphGenerator.jar
```


## The result files
When the program finishes the execution, it produces two files.

**1 - The generated graph metadata**: This file presents information about the generated graph. The listing below shows an example.
```json
{
	numberOfVertices : 1000,
	numberOfEdges : 500,
	numberOfAttributes : 10,
	numberOfPatterns : 2,
	patternVerticesSize : 4,
	patternAttributesHalfSize : 1,
	minAttValue : 50.0,
	maxAttValue : 150.0,
	generatedPatterns : [
		{
			vertices : ["V(0,1)","V(1,1)","V(1,2)","V(1,0)"],
			positiveContrastedAttributes : ["att0"],
			negativeContrastedAttributes : ["att1"]
		},
		{
			vertices : ["V(0,4)","V(0,5)","V(2,5)","V(1,4)"],
			positiveContrastedAttributes : ["att2"],
			negativeContrastedAttributes : ["att3"]
		}
	]
}
```
The array "generatedPatterns" specifies for each pattern its vertices and its attributes.

**2 - The generated graph**: This contains the generated graph. It is a JSON file that defines the structure of the graph and the vertex descriptions. The listing below is a simple example of a generated graph file.
```json
{
	descriptorsMetaData : [
		{
			descriptorName: "nominalDescriptor",
			attributesType: "nominal",
			attributesName: ["att0","att1","att2","att3","att4"]
		}
	],
	vertices : [
		{
			vertexId : "V(0,0)",
			descriptorsValues :[
				[152,86,64,136,146]
			]
		},
		{
			vertexId : "V(0,1)",
			descriptorsValues :[
				[158,71,113,65,101]
			]
		},
		{
			vertexId : "V(1,0)",
			descriptorsValues :[
				[141,79,125,131,128]
			]
		}
	],
	edges : [
		{
			vertexId :"V(0,0)",
			connected_vertices : ["V(0,1)","V(1,0)"]
		},
		{
			vertexId :"V(0,1)",
			connected_vertices : ["V(0,0)"]
		}
	]
}
```