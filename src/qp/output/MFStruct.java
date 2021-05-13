
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
	int max_2_quant; 
	int count_2_quant; 
	int max_3_quant; 

	MfStruct(String cust, String prod) {
		this.cust = cust;
		this.prod = prod;
		this.min_2_quant = Integer.MAX_VALUE;
	}

	public String toString() { 
		 return (
			this.cust + "\t"+ this.prod + "\t"+ this.sum_1_quant + "\t"+ this.min_2_quant + "\t"+ this.max_2_quant + "\t"+ this.count_2_quant + "\t"+ this.sum_3_quant + "\t"+ this.max_3_quant
		);
	}
}
