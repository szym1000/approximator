import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Regression {

	private double[] p;
	private double[][] points, pointsVal;
	private double[] cpoints, cpointsVal;
	private List<Double> Q, QVal;
	private int order;
	private double[] Qder;
	
	public Regression(double[][] points, double[] cpoints, double[][] pointsVal, double[] cpointsVal, int order) {
		Q = new ArrayList<Double>();
		QVal = new ArrayList<Double>();
		this.points = points;
		this.cpoints = cpoints;
		this.pointsVal = pointsVal;
		this.cpointsVal = cpointsVal;
		this.order = order;
		p = new double[order+1];
		Qder = new double[order+1];
		
		initp();
	}
	
	private void initp() {
		for(int i=0; i<p.length; i++) {
			p[i] = Math.random();
		}
	}
	
	/**
	 * Method that triggers the regression calculation.
	 */
	public void doRegression() {

		for(int i=0; i<1000; i++) {
			Q.add(calculateQ());
			QVal.add(calculateQVal());
			
			recalculateParameters();
		} 
		
	}
	
	/**
	 * Method calculating the value of Q.
	 * @return Value of Q
	 */
	private double calculateQ() {
		double sum = 0.0;
		
		for(int i=0; i<points.length; i++) {
			
			double temp = 0.0;
			
			for(int j=0; j<=order; j++) {
				temp += Math.pow(cpoints[i], j) * p[j];
			}
			temp -= points[i][1];
			
			sum += Math.pow(temp, 2);
			
		}
		
		return sum / (2*points.length);
	}
	
	private double calculateQVal() {
		double sum = 0.0;
		
		for(int i=0; i<pointsVal.length; i++) {
			
			double temp = 0.0;
			
			for(int j=0; j<=order; j++) {
				temp += Math.pow(cpointsVal[i], j) * p[j];
			}
			temp -= pointsVal[i][1];
			
			sum += Math.pow(temp, 2);
			
		}
		
		return sum / (2*pointsVal.length);
	}	
	
	/**
	 * Method calculating the value of the derivative of Q with respect to the given parameter.
	 * @param Index of the parameter.
	 * @return Value of the derivative.
	 */
	private double calculateQder(int which) {
		double sum = 0.0;
		
		for(int i=0; i<points.length; i++) {
			
			sum += (p[0] - points[i][1]) * Math.pow(cpoints[i], which);
			
			for(int j=1; j<=order; j++) {
				sum += Math.pow(cpoints[i], j) * p[j] * Math.pow(cpoints[i], which);
			}
			
		}
		
		Qder[which] = sum / cpoints.length;
		
		return Qder[which];
	}

	private void recalculateParameters() {
		for(int i=0; i < p.length; i++) {
			p[i] -= Data.alpha * calculateQder(i);
		}
	}
	
	public double[] getp() {
		return p;
	}
	
	public double[][] getQ() {
		double[][] qpoints = new double[Q.size()][2];
		
		for(int i=0; i<qpoints.length; i++) {
			qpoints[i][0] = i;
			qpoints[i][1] = Q.get(i);
		}
		
		return qpoints;
	}
	
	public double[][] getQVal() {
		double[][] qpointsVal = new double[QVal.size()][2];
		
		for(int i=0; i<qpointsVal.length; i++) {
			qpointsVal[i][0] = i;
			qpointsVal[i][1] = QVal.get(i);
		}
		
		return qpointsVal;
	}	
	
	public void saveAvgQToFile(int order) {
		FileWriter out;
		try {
			out = new FileWriter("res//q" + Data.taskNo + ".dat", true);

			double avgQ = 0.0;
			double sum = 0.0;
			
			for(int i=0; i<Q.size(); i++) {			
				sum += Q.get(i);
			}
			avgQ = sum / Q.size();
			
			out.append(order + " " + avgQ + "\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public void saveAvgQValToFile(int order) {
		FileWriter out;
		try {
			out = new FileWriter("res//qVal" + Data.taskNo + ".dat", true);

			double avgQVal = 0.0;
			double sum = 0.0;
			
			for(int i=0; i<QVal.size(); i++) {			
				sum += QVal.get(i);
			}
			avgQVal = sum / QVal.size();
			
			if(avgQVal < Data.minAvgQVal[1]) {
				Data.minAvgQVal[0] = order;
				Data.minAvgQVal[1] = avgQVal;
				
				System.out.println("MinAvgQVal: " + avgQVal + " | order: " + order);
			} 
			
			out.append(order + " " + avgQVal + "\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}	
	
}
