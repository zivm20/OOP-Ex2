

import java.util.Iterator;

import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.EdgeData;
import api.NodeData;
import api_implementation.Algorithems;
import api_implementation.Graph;
import processing.core.*;

/**
 * This class is the main class for Ex2 - your implementation will be tested using this class.
 */
public class Ex2 extends PApplet{
	public static void main(String[] args)  {
		/*
		GeoLocation p1 = new Point(10,0,0);
		GeoLocation p2 = new Point(0,10,0);
		GeoLocation p3 = new Point(-10,0,0);
		GeoLocation p4 = new Point(0,-10,0);
		GeoLocation p5 = new Point(7,5,0);
		GeoLocation p6 = new Point(-1,7,0);
		GeoLocation p7 = new Point(-4,-3,0);
		GeoLocation p8 = new Point(5,2,0);
		NodeData n1 = new Node(0,p1);
		NodeData n2 = new Node(1,p2);
		NodeData n3 = new Node(2,p3);
		NodeData n4 = new Node(4,p4);
		NodeData n5 = new Node(3,p5);
		NodeData n6 = new Node(5,p6);
		NodeData n7 = new Node(6,p7);
		NodeData n8 = new Node(7,p8);
		EdgeData e1 = new Edge(0,1,p1.distance(p2));
	 	EdgeData e2 = new Edge(1,2,p2.distance(p3));
	 	EdgeData e3 = new Edge(2,4,p3.distance(p4));
	 	EdgeData e4 = new Edge(4,0,p4.distance(p1));
	 	EdgeData e5 = new Edge(3,5,p5.distance(p6));
	 	EdgeData e6 = new Edge(5,6,p6.distance(p7));
	 	EdgeData e7 = new Edge(6,0,p6.distance(p1));
	 	EdgeData e8 = new Edge(3,0,p5.distance(p1));
	 	EdgeData e9 = new Edge(2,3,p3.distance(p5));
	 	EdgeData e10 = new Edge(2,5,1);
	 	EdgeData e11 = new Edge(5,4,1);
		LinkedList<NodeData> nodes = new LinkedList<NodeData>();
		LinkedList<EdgeData> edges = new LinkedList<EdgeData>();
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);
		nodes.add(n4);
		nodes.add(n5);
		nodes.add(n6);
		nodes.add(n7);
		nodes.add(n8);
		
		edges.add(e1);
		edges.add(e2);
		edges.add(e3);
		edges.add(e4);
		edges.add(e5);
		edges.add(e6);
		edges.add(e7);
		edges.add(e8);
		edges.add(e9);
		edges.add(e10);
		edges.add(e11);
		
		*/
		/*
		LinkedList<NodeData> nodes = new LinkedList<NodeData>();
		LinkedList<EdgeData> edges = new LinkedList<EdgeData>();
		for(int i = 0; i<10000; i++) {
			double x = Math.random()*(900);
			double y = Math.random()*(900);
			double z = Math.random()*(900);
			GeoLocation p = new Point(x,y,z);
			nodes.add(new Node(i,p));
		}
		for(int n = 0; n<10000; n++) {
			for(int ed = 0; ed<15; ed++) {
				if(Math.random() > 0.5) {
					edges.add(new Edge(n,(int) Math.floor(Math.random()*nodes.size()), Math.random()*200));
				}
			}
		}
		System.out.println("made params");
		DirectedWeightedGraph g = new Graph(nodes,edges);
		System.out.println("init graph");
		DirectedWeightedGraphAlgorithms alg = new Algorithems(g);
		System.out.println("init Algorithm");
		
		System.out.println(alg.save("data/"+name+".json"));
		System.out.println(alg.isConnected());
		
		*/
		String name = "2-5-4faster";
		
		
		
		if(args.length == 1) {
			runGUI("data/"+args[0]);
		}
		else {
			runGUI("data/"+name+".json");
		}
		
		
	}
    /**
     * This static function will be used to test your implementation
     * @param json_file - a json file (e.g., G1.json - G3.gson)
     * @return
     */
    public static DirectedWeightedGraph getGrapg(String json_file) {
        DirectedWeightedGraph ans;
        // ****** Add your code here ******
        
        ans = new Graph("data/"+json_file);
        
        
        // ********************************
        return ans;
    }
    /**
     * This static function will be used to test your implementation
     * @param json_file - a json file (e.g., G1.json - G3.gson)
     * @return
     */
    public static DirectedWeightedGraphAlgorithms getGrapgAlgo(String json_file) {
        DirectedWeightedGraphAlgorithms ans = new Algorithems(json_file);
        // ****** Add your code here ******
        //
        // ********************************
        return ans;
    }
    /**
     * This static function will run your GUI using the json file.
     * @param json_file - a json file (e.g., G1.json - G3.gson)
     *
     */
    public static void runGUI(String json_file) {
        
        PApplet.main("Ex2",new String[] {json_file});
        
    }
    
    
    //GUI
    DirectedWeightedGraphAlgorithms alg;
    double scaleX;
    double scaleY;
    double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
    //float scaleZ;
    
    @Override
    public void settings() {
    	size(900,900);
    }
    
    @Override
    public void setup() {
    	scaleX = 1;
    	scaleY = 1;
    	//scaleZ = 1;
    	alg = getGrapgAlgo(args[0]);
    	preProcessGraph();
    	
    }
    @Override
    public void draw() {
    	background(255);
    	
    	translate( (float)Math.max((width*0.9-height*0.9)/2,0),(float)Math.max((height*0.9-width*0.9)/2,0));
    	
    	pushMatrix();
    	translate((float)(width*0.1),(float)(height*0.1));
    	pushMatrix();
    	scale((float)scaleX,(float)scaleY);
    	
    	Iterator<NodeData> nodeIter =  alg.getGraph().nodeIter();
    	strokeWeight((float)(1/Math.max(scaleX,scaleY)));
    	while(nodeIter.hasNext()) {
    		fill(0);
    		stroke(0);
    		NodeData node = nodeIter.next();
    		pushMatrix();
    		textAlign(LEFT);
    		textSize((float)(20/Math.min(scaleX,scaleY)));
    		text(node.getKey(),(float)(((20/scaleX))+node.getLocation().x()-minX), (float)(-((20/scaleY))+node.getLocation().y()-minY));
    		
    		
    		translate((float)(node.getLocation().x()-minX),(float)(node.getLocation().y()-minY));
    		float size = 20;
    		ellipse((float)0,(float)0,(float)(size/scaleX),(float)(size/scaleY));
    		
    		popMatrix();
    		
    	}
    	
    	Iterator<EdgeData> edgeIter =  alg.getGraph().edgeIter();
    	while(edgeIter.hasNext()) {
    		fill(0);
    		
    		EdgeData edge = edgeIter.next();
    		NodeData node1 = alg.getGraph().getNode(edge.getSrc());
    		NodeData node2 = alg.getGraph().getNode(edge.getDest());
    		float x1 = (float)(node1.getLocation().x()-minX);
    		float x2 = (float)(node2.getLocation().x()-minX);
    		float y1 = (float)(node1.getLocation().y()-minY);
    		float y2 = (float)(node2.getLocation().y()-minY);
    		line(x1,y1,x2,y2);
    		
    	}
    	popMatrix();

    	edgeIter =  alg.getGraph().edgeIter();
    	while(edgeIter.hasNext()) {
    		fill(0);
    		//strokeWeight(max((float) (scaleX/10), (float)(scaleY/10)) );
    		EdgeData edge = edgeIter.next();
    		NodeData node1 = alg.getGraph().getNode(edge.getSrc());
    		NodeData node2 = alg.getGraph().getNode(edge.getDest());
    		float x1 = (float)((node1.getLocation().x()-minX)*scaleX);
    		float x2 = (float)((node2.getLocation().x()-minX)*scaleX);
    		float y1 = (float)((node1.getLocation().y()-minY)*scaleY);
    		float y2 = (float)((node2.getLocation().y()-minY)*scaleY);
    		
    		double distance = node1.getLocation().distance(node2.getLocation());
    		double angle = (y1-y2)/(x1-x2);
    		double[] intersection = getPoint(x2,y2,x1,y1,distance*0.1*Math.min(scaleX,scaleY));
    		double newAngle = -1/angle;
    		double tempY1 = newAngle + intersection[1];
    		double tempY2 = -newAngle + intersection[1];
    		double[] point1= getPoint(intersection[0],intersection[1],intersection[0]+1,tempY1, Math.max(scaleX,scaleY)*0.4*distance/Math.pow(3, 2) );
    		double[] point2= getPoint(intersection[0],intersection[1],intersection[0]-1,tempY2, Math.max(scaleX,scaleY)*0.4*distance/Math.pow(3, 2) );
    		stroke(255,0,0);
    		fill(255,0,0);
    		
    		
    		
    		triangle(x2,y2,(float)(point1[0]),(float)point1[1],(float)point2[0],(float)point2[1]);
    		
    		
    		
 
    		
    		
    	}
    	popMatrix();
    	
    }
    double[] getPoint(double x1,double y1,double x2,double y2,double distance) {
		double vectX = x2-x1;
		double vectY = y2-y1;
		double uX = vectX/Math.pow(vectX*vectX + vectY*vectY,0.5 );
		double uY = vectY/Math.pow(vectX*vectX + vectY*vectY,0.5 );
		double pX = x1+uX*distance;
		double pY = y1+uY*distance;
		return new double[] {pX,pY};
    }
    
    
    void preProcessGraph() {
    	Iterator<NodeData> nodeIter =  alg.getGraph().nodeIter();
    	double  maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE; 
    	while(nodeIter.hasNext()) {
    		NodeData node = nodeIter.next();
    		maxX = Math.max(maxX, node.getLocation().x());
    		maxY = Math.max(maxY, node.getLocation().y());
    		minX = Math.min(minX, node.getLocation().x());
    		minY = Math.min(minY, node.getLocation().y());
    		
    	}
    	
    	scaleX = (maxX - minX);
    	scaleY = (maxY - minY);
    	scaleX = ((width*0.8)/scaleX);
    	scaleY = ((height*0.8)/scaleY);
    	nodeIter =  alg.getGraph().nodeIter();
    	
    	
    
    }
    
    
    
    
    
    
}