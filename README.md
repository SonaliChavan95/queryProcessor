# queryProcessor

## About
ESQL -> JAVA CODE -> Query Result

#### ESQL
```sql
SELECT cust, prod, count(1.quant) as count_1_quant, min(2.quant) as min_2_quant, 
sum(2.quant) as sum_2_quant, max(2.quant) as max_2_quant
FROM sales
GROUP BY cust, prod; 1, 2
SUCH THAT 1.cust = cust and 1.state="NY" and 1.quant<1000,
          2.cust = cust and 2.state="NJ"
HAVING sum_1_quant > 2 * min_2_quant
```
#### EQUIVALENT INPUT QUERY
```
SELECT ATTRIBUTE(S):
cust, prod, count_1_quant, min_2_quant, sum_2_quant ,max_2_quant
NUMBER OF GROUPING VARIABLES(n):
2
GROUPING ATTRIBUTES(V):
cust,prod
F-VECT([F]):
count_1_quant, min_2_quant, sum_2_quant ,max_2_quant
SELECT CONDITION-VECT([σ]):
1.state="NY" and 1.quant<100 
2.state="NJ"
HAVING_CONDITION(G):
sum_1_quant > 2 * min_2_quant
```

#### QUERY INFORMATION
```
Selected: [cust, prod, count_1_quant, min_2_quant, sum_2_quant ,max_2_quant] -> ArrayList
Number of grouping variables: 2 -> int
Grouping Attributes: [cust, prod] -> ArrayList
Function Vector: [count_1_quant, min_2_quant, sum_2_quant ,max_2_quant] -> HashSet
Condition Vector: [1.state="NY" and 1.quant<100, 2.state="NJ"] -> ArrayList
Having String: sum_1_quant > 2 * min_2_quant
```

#### CODE GENERATION
parts of generated code:
  1. database setup: ConnectDB.java
  2. mf-table: MfStruct.java - aggregate functions, grouping attributes
  2. main class: query.java - main code for generating the output

#### LIMITATION:
* Where clause is not supported
* If records are not present for any group, default Integer.MIN and Integer.MAX value is displayed in the output table for min, max conditions.
* Simple sql query without group by is not supported
* No Syntax checking in query file and presence of column in table

#### PREREQUISITES:
* Condition vectors should be specified in numeric order
* Grouping variables needs to numbers from 1 to n 
* Condition vector shouldn’t have space between operator and operand e.g 1.quant > 30 is not allowed. It should be 1.quant>30 in the input query file

#### PROJECT SETUP IN ECLIPSE:
* Unzip folder
* Import Existing Project into Eclipse
* Right Click on "queryProcessor" root folder
* Goto "Build Path" -> Select configure Build Path -> Select "Libraries"
* "Select Classpth" -> click on "Add External Jars"
* Select "postgresql.jar" file from unzipped folder
* Click on "Apply and Close"
* Change username password and database name at two places before running the project
          1. qp.ConnectDB.java (at line number 18, 19, 20) 
          2. qp.CodeGenerator.java (at line number 41, 42, 43)
* Select "Project.java" and Run the application.

## SEE sample_queries/* files for syntax
