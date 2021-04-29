/**
 *
 */
package qp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class InputQuery {

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
	// Having Condition
	public String G;


	InputQuery() {
		this.S = new ArrayList<String>();
		this.V = new ArrayList<String>();
		this.F = new ArrayList<String>();
		this.CV = new ArrayList<String>();
		this.G = new String();
		this.n = 0;
	}

	InputQuery(InputQuery mf_struct) {
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

				String input = "";
				while (myReader.hasNextLine()) {
					if(!input.equals("HAVING_CONDITION(G):"))
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
						if(myReader.hasNextLine()) {
							input = myReader.nextLine();
							this.n = Integer.parseInt(input);
							// System.out.println(this.S.get(1));
						}
						break;
					case "GROUPING ATTRIBUTES(V):":
						if(myReader.hasNextLine()) {
							input = myReader.nextLine();
							String arr[] = input.split("[,]");
							for(String i : arr)
								this.V.add(i.trim());
							// System.out.println(this.V.get(1));
						}
						break;
					case "F-VECT([F]):":
						if(myReader.hasNextLine()) {
							input = myReader.nextLine();
							String arr[] = input.split("[,]");
							for(String i : arr)
								this.F.add(i.trim());
							// System.out.println(this.F.get(1));
						}
						break;
					case "SELECT CONDITION-VECT([σ]):":
						while(myReader.hasNextLine()) {
							input = myReader.nextLine();
							if(input.equals("HAVING_CONDITION(G):"))
								break;

							System.out.println(input);
							this.CV.add(input.trim());
						}
						// System.out.println(this.CV.get(3));

						break;
					case "HAVING_CONDITION(G):":
						if(myReader.hasNextLine()) {
							input = myReader.nextLine();
							this.G = input.trim();
							System.out.println(this.G);
						}
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
