
// MFStruct created using Grouping Attributes and F vectors 
package qp.output;

class MfStruct { 
	String cust; 
	String prod; 
	int count_1_quant; 
	int min_2_quant; 
	int sum_2_quant; 
	int max_2_quant; 
	int count_3_quant; 
	int avg_3_quant; 
	int sum_3_quant; 
	int max_3_quant; 
	int sum_1_quant; 
	int count_2_quant; 
	int avg_1_quant; 

	MfStruct(String cust, String prod) {
		this.cust = cust;
		this.prod = prod;
		this.min_2_quant = Integer.MAX_VALUE;
	}
}
