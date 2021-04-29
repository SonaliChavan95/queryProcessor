/**
 * 
 */
package qp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

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

	public void readFile(String filename) {
		try {
			File myObj = new File(filename);

			if (myObj.exists()) {
				Scanner myReader = new Scanner(myObj);;

				String input;
				while (myReader.hasNextLine()) {
					input = myReader.nextLine();
					 System.out.println(input);

					switch(input) {
					case "SELECT ATTRIBUTE(S):":
						if(myReader.hasNextLine()) {
							input = myReader.nextLine();
							String arr[] = input.split("[,]"); 
							for(String i : arr)
								this.S.add(i.trim());
							// System.out.println(this.S.get(1));
						}
						break;
					case "NUMBER OF GROUPING VARIABLES(n):":
						break;
					case "GROUPING ATTRIBUTES(V):":
						break;
					case "F-VECT([F]):":
						break;
					case "SELECT CONDITION-VECT([σ]):":
						break;
					case "HAVING_CONDITION(G):":
						break;
					}
				}


				myReader.close();
			} else {
				System.out.println("The file does not exist.");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
}
