/**
 * 
 */
package qp;

import java.util.ArrayList;

public class MfStructure {

	// Select attributes
	public ArrayList<String> S;
	// Number of grouping variables
	public int n; 
	// Grouping Attributes
	public ArrayList<String> V;
	// F Vector
	public ArrayList<String> F;
	// Select condition Vector
	public ArrayList<String> CV;
	// Having Conditions
	public ArrayList<String> G;
	
	
	MfStructure() {
		this.S = new ArrayList<String>();
		this.V = new ArrayList<String>();
		this.F = new ArrayList<String>();
		this.CV = new ArrayList<String>();
		this.G = new ArrayList<String>();
		this.n = 0;
	}
	
	MfStructure(MfStructure mf_struct) {
		this.S = mf_struct.S;
		this.V = mf_struct.V;
		this.F = mf_struct.F;
		this.CV = mf_struct.CV;
		this.G = mf_struct.G;
		this.n = mf_struct.n;
	}
	
	
}
