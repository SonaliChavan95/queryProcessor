/**
 *
 */
package qp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class InputQuery {

  // Select attributes
  public ArrayList<String> S;
  // Number of grouping variables
  public int n;
  // Grouping Attributes
  public ArrayList<String> V;
  // F Vector
  public HashSet<String> F;
  // Select condition Vector
  public ArrayList<String> CV;
  // Having Condition
  public String G;

  /**
   * Constructor for this class
   * */
  InputQuery() {
    this.S = new ArrayList<String>();
    this.V = new ArrayList<String>();
    this.F = new HashSet<String>();
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

          switch(input) {
          case "SELECT ATTRIBUTE(S):":
            if(myReader.hasNextLine()) {
              input = myReader.nextLine();
              String arr[] = input.split("[,]");
              for(String i : arr)
                this.S.add(i.trim());
            }
            break;
          case "NUMBER OF GROUPING VARIABLES(n):":
            if(myReader.hasNextLine()) {
              input = myReader.nextLine();
              this.n = Integer.parseInt(input);
            }
            break;
          case "GROUPING ATTRIBUTES(V):":
            if(myReader.hasNextLine()) {
              input = myReader.nextLine();
              String arr[] = input.split("[,]");
              for(String i : arr)
                this.V.add(i.trim());
            }
            break;
          case "F-VECT([F]):":
            if(myReader.hasNextLine()) {
              input = myReader.nextLine();
              String arr[] = input.split("[,]");
              for(String i : arr) {
                if(i.contains("avg")) {
                  this.F.add(i.trim().replace("avg", "sum"));
                  this.F.add(i.trim().replace("avg", "count"));
                }
                this.F.add(i.trim());
              }
            }
            break;
          case "SELECT CONDITION-VECT([??]):":
            while(myReader.hasNextLine()) {
              input = myReader.nextLine();
              if(input.equals("HAVING_CONDITION(G):"))
                break;
              this.CV.add(input.trim());
            }

            break;
          case "HAVING_CONDITION(G):":
            if(myReader.hasNextLine()) {
              input = myReader.nextLine();
              this.G = input.trim();
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
