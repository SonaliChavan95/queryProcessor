
// MFStruct created using Grouping Attributes and F vectors 
package qp.output;

class MfStruct { 
	String cust;
	String prod;
	int count_1_quant;
	int sum_2_quant;
	int count_3_quant;
	double avg_3_quant;
	int sum_3_quant;
	int max_3_quant;
	int sum_1_quant;
	int count_2_quant;
	double avg_1_quant;

	MfStruct(String cust, String prod) {
		this.cust = cust;
		this.prod = prod;
	}
}
