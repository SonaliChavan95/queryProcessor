
// MFStruct created using Grouping Attributes and F vectors 
package qp.output;

class MfStruct { 
	String cust;
	String prod;
	int sum_2_quant;
	int count_1_quant;
	int sum_0_quant;
	int max_0_quant;
	double avg_0_quant;
	int count_0_quant;
	int sum_1_quant;
	double avg_1_quant;

	MfStruct(String cust, String prod) {
		this.cust = cust;
		this.prod = prod;
	}
}
