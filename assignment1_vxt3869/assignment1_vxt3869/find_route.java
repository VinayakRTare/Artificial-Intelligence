/**
*
* The find_route program implements an application that
* simply computes shortest distance between two cities.
*
* @author  Vinayak Tare
* @version 1.0
* @since   2017-01-23
*  
*/
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;


class Node {
	String name;
	public ArrayList<Edge> al;
	public double pathCost;
	public Node parent;

	public Node(String name) {
		super();
		this.name = name;
	}

	public Node(String name, double pathCost) {
		super();
		this.name = name;
		this.pathCost = pathCost;
	}

	public Node(Node child) {
		// TODO Auto-generated constructor stub
		name = child.name;
		pathCost = child.pathCost;
		for (Edge e : child.al) {
			al.add(e);
		}
		parent = child.parent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}

class Edge {
	Node target;
	double cost;

	public Edge(Node target, double cost) {
		super();
		this.target = target;
		this.cost = cost;
	}

}

public class find_route {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> rawFile = new ArrayList<String>();
		Set<Node> nodeSet = new LinkedHashSet<Node>();
		try {
			final String FILENAME = args[0];
			BufferedReader br = null;
			FileReader fr = null;
			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);
			String sCurrentLine;
						
			while ((sCurrentLine = br.readLine()) != null) { //read txt file and store it in arraylist rawFile in 
				if(!sCurrentLine.equalsIgnoreCase("end of input") && !sCurrentLine.equalsIgnoreCase("")){											 //the form of string for future use  
				rawFile.add(sCurrentLine);}
				
			}
			br.close();
		} catch (IOException e) {
			System.out.println("No such file exists");
			e.printStackTrace();
			System.exit(0);
		}
		
		/***create and add nodes to nodeSet***/
		for (String s : rawFile) {
			String x[] = s.split("\\s+");
			Node newNode = new Node(x[0]);
			Node newNode2 = new Node(x[1]);
			newNode.al = new ArrayList<Edge>();
			newNode2.al = new ArrayList<Edge>();
			nodeSet.add(newNode);
			nodeSet.add(newNode2);
		}
		
		/**add edges*/
		Node edgeNode;
		for (String s : rawFile) {
			String x[] = s.split("\\s+");
			edgeNode = findNode(nodeSet, x[1]);
			Edge e2 = new Edge(edgeNode, Double.parseDouble(x[2]));
			for (Node newNode : nodeSet) {
				if (newNode.name.equalsIgnoreCase(x[0])) {
					newNode.al.add(e2);
				}
			}
		}

		Node source = null;
		Node destination = null;
		for (Node n : nodeSet) {
			if (n.name.equalsIgnoreCase(args[1])) {
				source = n;
			}
			if (n.name.equalsIgnoreCase(args[2])) {
				destination = n;
			}
		}
		if (source == null || destination == null) {
			System.out.println("Such city does not exist in a map");
			System.exit(0);
		}
		if(source==destination){
			System.out.println("Distance : 0 km");
		}
		else{
		// if cities are valid compute path
		routeSearch(source, destination);
		//path from source to destination
		List<Node> path = getPath(destination);
		if (destination.pathCost == 0) {
			System.out.println("Distance : infinity");
		} else {
			System.out.println("Distance " + destination.pathCost + " km");
		}
		System.out.println("Route");
		if (destination.pathCost == 0) {
			System.out.println("none");
		} else {
			for (int i = 0; i < path.size(); i++) {
				Node first = path.get(i);
				Node second = null;
				if (path.size() > i + 1) {
					second = path.get(i + 1);
					System.out.println("" + first.name + "  to  " + second.name + " "
							+ (second.pathCost - first.pathCost) + " km");
				}

			}
		}}
	}

	
/***finds a node that matches with string name***/
	public static Node findNode(Set<Node> nodeSet, String str) {
		for (Node abc : nodeSet) {
			if (abc.name.equalsIgnoreCase(str)) {
				return abc;
			}
		}
		return null;
	}
/***Computes shortest path from source to destination***/
	public static void routeSearch(Node source, Node destination) {
		source.pathCost = 0;
		PriorityQueue<Node> fringe = new PriorityQueue<Node>(50, new Comparator<Node>() {
			// override compare method
			public int compare(Node i, Node j) {
				if (i.pathCost > j.pathCost) {
					return 1;
				}
				else if (i.pathCost < j.pathCost) {
					return -1;
				}
				else {
					return 0;
				}
			}
		}
		);
		fringe.add(source);
		Set<Node> visited = new HashSet<Node>();
		boolean found = false;
		do {
			Node current = fringe.poll();
			visited.add(current);
			//if destination is found 
			if (current.name.equalsIgnoreCase(destination.name)) {
				found = true;
			}

			for (Edge e : current.al) {
				Node child = e.target;
				double cost = e.cost;
				// add node to fringe if node has not been explored
				if (!fringe.contains(child) && !visited.contains(child)) {
					child.pathCost = current.pathCost + cost;
					child.parent = current;
					fringe.add(child);

				}

				// if current pathCost is smaller than previous path
				else if ((fringe.contains(child)) && (child.pathCost > (current.pathCost + cost))) {
					child.parent = current;
					child.pathCost = current.pathCost + cost;
					fringe.remove(child);
					fringe.add(child);

				}

			}

		} while (!fringe.isEmpty() && (found == false));

	}
/***Create path which stores all the cities from source to destination***/
	public static List<Node> getPath(Node destination) {
		List<Node> path = new ArrayList<Node>();
		for (Node node = destination; node != null; node = node.parent) {
			path.add(node);
		}
		Collections.reverse(path);
		return path;
	}
}
