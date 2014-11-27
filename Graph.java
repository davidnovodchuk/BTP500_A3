/*
 * BTP500 - Assignment 3
 * Dimitri Novodchuk
 */
import java.util.*;

public class Graph {

	/* 
	 * Explanation of the data structure I chose:
	 * 
	 * vertices is a TreeMap in which:
	 * 
	 * 	- keys: strings that represent vertices that connect with directed
	 * 		edges to other vertices. I chose to represent these vertices as
	 * 		keys of TreeMap because the graph is directed, and the "from"
	 * 		vertex (vertex 1) can be represented uniquely (as you know,
	 * 		TreeMap keys are unique, and not repeated). using TreeMap allows
	 * 		me find vertices faster that user arrays or ArraLists.
	 * 
	 *  - values: each value of "vertices" TreeMap consist of ArrayList
	 *  	of Strings. Each String represents a "to" vertex (vertex 2). I
	 *  	chose to use here ArrayList because searches are not performed
	 *   	on this data structure. insert item is more efficient in ArrayList
	 *   	than in TreeSet (TreeSet is sorted tree). I cannot use array
	 *   	because I don't know the number of item that will be stored there.
	 */
	private TreeMap<String,ArrayList<String>> vertices;

	// constructor - initializing empty graph
	public Graph() {

		this.vertices = new TreeMap<String,ArrayList<String>>();
	}

	// adding edge from v1 to v2
	public void addEdge(String v1, String v2) {

		// check if v1 already exists in "from" vertices
		if( this.vertices.containsKey(v1) ) {

			// checking that v2 is not a "to" vertex of v1
			if( ! this.vertices.get(v1).contains(v2) )
				
				// adding v2 to the "to" vertices of v1
				this.vertices.get(v1).add(v2);
		} else {

			// if v1 not in graph, adding it and then adding v2 to its
			// "to" vertices
			ArrayList<String> v2ArrayList = new ArrayList<String>();
			v2ArrayList.add(v2);
			this.vertices.put(v1, v2ArrayList);
		}
	}

	// size returns array with 2 values: index 0 - number of vertices in the graph
	// 									 index 1 - number of edges in the graph
	public int[] size() {

		// Number of vertices, initialized to number of "from" vertices
		int sizeV = this.vertices.size();
		
		// Number of edges
		int sizeE = 0;

		// Iterating through all the "from" vertices ( keys of this.vertices TreeMap )
		for ( String v1 : this.vertices.keySet() ) {

			// getting ArrayList of all "to" vertices of current "from" vertex (v1)
			ArrayList<String> v2ArrayList = this.vertices.get(v1);

			// number of "to" vertices in the current "from" vertex
			int numOfV = v2ArrayList.size();
			
			// number of vertices equals to number of edges because every "to" 
			// vertex connected by 1 edge from "from" vertex
			sizeV += numOfV;
			sizeE += numOfV;
			
		}

		// sizes contains size of vertices and size of edges
		int sizes[] = new int[]{sizeV, sizeE};
		return sizes;
	}

	public void printMatchingEdges(String pattern1, String pattern2) {

		String output = "";

		// Iterating through all the "from" vertices ( keys of this.vertices TreeMap )
		for ( String v1 : this.vertices.keySet() ) {

			// check if v1 matches pattern1
			if( v1.contains( pattern1 ) ) {

				// iterating through all "to" vertices of v1
				// v2 - changing "to" vertex (of "from" vertex v1)
				for ( String v2 : this.vertices.get(v1) ) {

					// check if v2 matches pattern2
					if( v2.contains( pattern2 ) )
						output += v1 + " ==> " + v2 + "\n" ;
				}
			}
		}
		// printing results
		if(output == "")
			System.out.println("\n--- no matches ---");
		else
			System.out.println( output );
	}

	public void printMatchingPath(int length, String pattern1, String pattern2) {

		// ArrayList of visited vertices for the depth first search recursion
		TreeSet<String> visitedVertices = new TreeSet<String>();	

		// path = array of strings that will contain all strings in the path
		String[] path = new String[length + 1];
		
		// pattern1MatchFound - specifies if v1 vertex that matches pattern1 found
		boolean pattern1MatchFound = false;

		// Iterating through all the "from" vertices ( keys of this.vertices TreeMap )
		for ( String v1 : this.vertices.keySet() ) {
			
			if( ! pattern1MatchFound ) {

				// check if current vertex matches pattern1. 
				// if yes, finding path will be conducted on this vertex 
				if( v1.contains( pattern1 ) ) {
					
					pattern1MatchFound = true;
					
					// adding v1 to visitedVertices ArrayList, ArrayList is used because many 
					// searches performed on visitedVertices
					visitedVertices.add(v1);
	
					// calling the depthFirstSearch recursove method
					if( depthFirstSearch( length, v1, pattern2, visitedVertices, path, v1 ) ){
	
						// printing results
						for(int i = path.length - 1 ; i >= 0 ; i-- ) {
	
							for(int j = i + 2 ; j < path.length ; j++)
								System.out.print("   ");
	
							if(i == path.length - 1)
								System.out.println( path[i] );
							else
								System.out.println("==> " + path[i]);
						}
					} else
						System.out.println( "--- no matching path ---" );
				}
			}
		}
	}

	// recursive method, update path array and returns true if matching path found, 
	// false otherwise 
	private boolean depthFirstSearch( int length, String v1, String pattern2, 
			TreeSet<String> visitedVertices, String[] path, String firstVertex ) {

		// simplest case
		if( length == 1 && this.vertices.containsKey(v1)) {

			// iterating through all "to" vertices of v1
			// v2 - changing "to" vertex (of "from" vertex v1)
			for( String v2 : this.vertices.get(v1) ) {

				// check if v2 matches pattern2
				if( v2.contains(pattern2) ) {
					// check that there are no repetitions in the path, except for
					// first item (it can be a circular path)
					if( !visitedVertices.contains(v2) || firstVertex.equals(v2) ) {

						// adding current v1 and v2 to path array, and return true
						path[length - 1] = v2;
						path[length] = v1;
						return true;
					}
				}
			}
			return false;

		} else if( this.vertices.containsKey(v1) ) {

			// create ascending iterator
			Iterator<String> iterator = this.vertices.get(v1).iterator();

			// going through the v1 vertices 
			while (iterator.hasNext()){

				// nextV1 - current "from" vertex that will be passed to the
				// depthFirstSearch recursion as v1 argument
				String nextV1 = iterator.next();

				// check that nextV1 is not in visitedVertices (prevent repetition of vertices)
				if( !visitedVertices.contains(nextV1) ) {

					// creating new VisitedVerrices treeMap for each path based on visitedVertices
					TreeSet<String> newVisitedVertices = new TreeSet<String>(visitedVertices);
					newVisitedVertices.add(nextV1);

					// calling depthFirstSearch to determine if there is matching path
					if ( depthFirstSearch( length - 1, nextV1, pattern2, newVisitedVertices, 
							path, firstVertex ) ) {

						// adding current v1 to path array, return true
						path[length] = v1;
						return true;
					}
				}
			}
			return false;

		} else
			return false;
	}

	public static void main(String args[]) {

		Graph g = new Graph();
		g.addEdge("111","555");
		g.addEdge("111","777");
		g.addEdge("111","666");
		g.addEdge("111","667");
		g.addEdge("444","888");
		g.addEdge("666","555");
		g.addEdge("555","111");
		g.addEdge("111","777");

		System.out.println("Number of vertices: " + g.size()[0]);
		System.out.println("Number of edges: " + g.size()[1]);

		System.out.print("\n");
		g.printMatchingEdges("1", "6");
		
		g.printMatchingPath(3, "1", "1");
	}
}
