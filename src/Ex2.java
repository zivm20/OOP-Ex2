

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.EdgeData;
import api.GeoLocation;
import api.NodeData;
import api_implementation.Algorithems;
import api_implementation.Graph;
import api_implementation.Point;
import processing.core.*;

/**
 * This class is the main class for Ex2 - your implementation will be tested using this class.
 */
public class Ex2 extends PApplet{
	public static void main(String[] args)  {
	
		
		
		
		if(args.length == 1) {
			runGUI("data/"+args[0]);
		}
		else {
			runGUI("data/G1.json");
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
    boolean multi_select = false;
    int choose_2 = 2;
    double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
    List<NodeData> marked_nodes;
    List<EdgeData> marked_edges;
    //float scaleZ;
    
    @Override
    public void settings() {
    	size(900,900);
    }
    
    @Override
    public void setup() {
    	scaleX = 1;
    	scaleY = 1;
    	marked_nodes = new  LinkedList<NodeData>();
    	marked_edges = new  LinkedList<EdgeData>();
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
    	noStroke();
    	while(nodeIter.hasNext()) {
    		fill(0);
    		stroke(0);
    		NodeData node = nodeIter.next();
    		pushMatrix();
    		if(marked_nodes.contains(node)) {
    			fill(0,255,0);
    		}
    		
    		translate((float)(node.getLocation().x()-minX),(float)(node.getLocation().y()-minY));
    		float size = 20;
    		
    		ellipse((float)0,(float)0,(float)(size/scaleX),(float)(size/scaleY));
    		popMatrix();
    	}
    	Iterator<EdgeData> edgeIter =  alg.getGraph().edgeIter();
    	strokeWeight((float)(5/Math.max(scaleX,scaleY)));
    	while(edgeIter.hasNext()) {
    		noFill();
    		stroke(0);
    		EdgeData edge = edgeIter.next();
    		NodeData node1 = alg.getGraph().getNode(edge.getSrc());
    		NodeData node2 = alg.getGraph().getNode(edge.getDest());
    		if(marked_edges.contains(edge)) {
    			fill(0,255,0);
    			stroke(0,255,0);
    		}
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
    		double[] point1= getPoint(intersection[0],intersection[1],intersection[0]+1,tempY1, ((scaleX+scaleY)/2)*0.4*distance/Math.pow(3, 2) );
    		double[] point2= getPoint(intersection[0],intersection[1],intersection[0]-1,tempY2, ((scaleX+scaleY)/2)*0.4*distance/Math.pow(3, 2) );
    		stroke(255,0,0);
    		fill(255,0,0);
    		triangle(x2,y2,(float)(point1[0]),(float)point1[1],(float)point2[0],(float)point2[1]);
    	}
    	popMatrix();
    	
    }
    public void keyReleased() {
    	if(key=='c') {
    		marked_nodes = new  LinkedList<NodeData>();
    		marked_edges = new  LinkedList<EdgeData>();
    		marked_nodes.add(alg.center());
    	}
    	if(key == 't') {
    		multi_select = !multi_select;
    		if(multi_select) {
    			marked_nodes = new  LinkedList<NodeData>();
        		marked_edges = new  LinkedList<EdgeData>();
    		}
    		else {
    			marked_nodes = alg.tsp(marked_nodes);
    			marked_edges = new  LinkedList<EdgeData>();
    			for(int i = 0; i<marked_nodes.size()-1; i++) {
    				marked_edges.add(alg.getGraph().getEdge(marked_nodes.get(i).getKey(), marked_nodes.get(i+1).getKey()));
    			}
    		}
    	}
    	if(key == 'p') {
    		marked_nodes = new  LinkedList<NodeData>();
    		marked_edges = new  LinkedList<EdgeData>();
    		choose_2 = 0;
    	}
    }
    public void mouseClicked() {
    	double dist = Double.MAX_VALUE;
    	NodeData n = null;
    	
    	double mX = (mouseX - (width*0.1)-(float)Math.max((width*0.9-height*0.9)/2,0))/scaleX ;
    	double mY = (mouseY - (height*0.1)-(float)Math.max((width*0.9-height*0.9)/2,0) )/scaleY;
    	
    	GeoLocation mouse = new Point(mX,mY,0);
    	Iterator<NodeData> nodeIter =  alg.getGraph().nodeIter();
    	while(nodeIter.hasNext()) {
    		NodeData node = nodeIter.next();
    		double nodeX = (node.getLocation().x()-minX);
    		double nodeY = (node.getLocation().y()-minY);
    		GeoLocation nodeLoc = new Point(nodeX,nodeY,0);
    		
    		if(dist > nodeLoc.distance(mouse)) {
    			n = node;
    			dist = nodeLoc.distance(mouse);
    		}
    	}
    	
    	if(multi_select) {
    		if(!marked_nodes.contains(n)) {
    			marked_nodes.add(n);
    		}
    	}
    	if(choose_2<2) {
    		if(!marked_nodes.contains(n)) {
    			marked_nodes.add(n);
    			choose_2++;
    		}
    		if(choose_2 == 2) {
    			marked_nodes = alg.shortestPath(marked_nodes.get(0).getKey(),marked_nodes.get(marked_nodes.size()-1).getKey());
    			marked_edges = new  LinkedList<EdgeData>();
    			for(int i = 0; i<marked_nodes.size()-1; i++) {
    				marked_edges.add(alg.getGraph().getEdge(marked_nodes.get(i).getKey(), marked_nodes.get(i+1).getKey()));
    			}
    		}
    	}
    	
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