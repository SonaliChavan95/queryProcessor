
// MFStruct created using Grouping Attributes and F vectors 
package qp.output;

class MfStruct { 
	String cust; 
	String prod; 
	int sum_1_quant; 
	int avg_1_quant; 
	int min_2_quant; 
	int sum_3_quant; 
	int avg_3_quant; 

	MfStruct(String cust, String prod) {
		this.cust = cust;
		this.prod = prod;
		this.min_2_quant = 2147483647;
	}

	public String toString() { 
		 return (
			this.cust + "\t"+ this.prod + "\t"+ this.sum_1_quant + "\t"+ this.min_2_quant + "\t"+ this.sum_3_quant
		);
	}
}
