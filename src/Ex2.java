

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
    	scale( min((float) ((width*0.8)/scaleX), (float)((height*0.8)/scaleY)),min((float) ((width*0.8)/scaleX), (float)((height*0.8)/scaleY)) );
    	
    	Iterator<NodeData> nodeIter =  alg.getGraph().nodeIter();
    	strokeWeight(max((float)(1/((width*0.8)/scaleX)),(float)(1/((height*0.8)/scaleY))));
    	while(nodeIter.hasNext()) {
    		fill(0);
    		stroke(0);
    		NodeData node = nodeIter.next();
    		pushMatrix();
    		translate((float)(node.getLocation().x()-minX),(float)(node.getLocation().y()-minY));
    		float size = 20 * max((float)(1/((width*0.8)/scaleX)),(float)(1/((height*0.8)/scaleY)));
    		ellipse((float)0,(float)0, size,size);
    		popMatrix();
    		
    	}
    	
    	Iterator<EdgeData> edgeIter =  alg.getGraph().edgeIter();
    	while(edgeIter.hasNext()) {
    		fill(0);
    		//strokeWeight(max((float) (scaleX/10), (float)(scaleY/10)) );
    		EdgeData edge = edgeIter.next();
    		NodeData node1 = alg.getGraph().getNode(edge.getSrc());
    		NodeData node2 = alg.getGraph().getNode(edge.getDest());
    		float x1 = (float)(node1.getLocation().x()-minX);
    		float x2 = (float)(node2.getLocation().x()-minX);
    		float y1 = (float)(node1.getLocation().y()-minY);
    		float y2 = (float)(node2.getLocation().y()-minY);
    		line(x1,y1,x2,y2);
    		
    	}
    	edgeIter =  alg.getGraph().edgeIter();
    	while(edgeIter.hasNext()) {
    		fill(0);
    		//strokeWeight(max((float) (scaleX/10), (float)(scaleY/10)) );
    		EdgeData edge = edgeIter.next();
    		NodeData node1 = alg.getGraph().getNode(edge.getSrc());
    		NodeData node2 = alg.getGraph().getNode(edge.getDest());
    		float x1 = (float)(node1.getLocation().x()-minX);
    		float x2 = (float)(node2.getLocation().x()-minX);
    		float y1 = (float)(node1.getLocation().y()-minY);
    		float y2 = (float)(node2.getLocation().y()-minY);
    		
    		double distance = node1.getLocation().distance(node2.getLocation());
    		double angle = (x1-x2)/(y1-y2);
    		double[] intersection = getPoint(x2,y2,x1,y1,distance*0.05);
    		double newAngle = -1/angle;
    		double tempY1 = newAngle + intersection[1];
    		double tempY2 = -newAngle + intersection[1];
    		double[] point1= getPoint(intersection[0],intersection[1],intersection[0]+1,tempY1, 0.1*distance/Math.pow(3, 2) );
    		double[] point2= getPoint(intersection[0],intersection[1],intersection[0]-1,tempY2,  0.1*distance/Math.pow(3, 2) );
    		stroke(255,0,0);
    		fill(255,0,0);
    		//line(x2,y2,(float)point1[0],(float)point1[1]);
    		//line(x2,y2,(float)point2[0],(float)point2[1]);
    		
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
    		//System.out.println(node.toString());
    		maxX = Math.max(maxX, node.getLocation().x());
    		maxY = Math.max(maxY, node.getLocation().y());
    		minX = Math.min(minX, node.getLocation().x());
    		minY = Math.min(minY, node.getLocation().y());
    		
    	}
    	
    	scaleX = (maxX - minX);
    	scaleY = (maxY - minY);
    	nodeIter =  alg.getGraph().nodeIter();
    	while(nodeIter.hasNext()) {
    		NodeData node = nodeIter.next();
    	}
    	
    
    }
    
    
    
    
    
    
}