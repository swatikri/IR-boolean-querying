- Main function is named “CSE535Assignment.java”. It should take the index file as the first parameter when starting. It should be able to execute the functions mentioned above and write corresponding results into a log file with the required format. The log file name should be the second parameter to your main function. The third parameter is an integer that will be used in the getTopK. The last parameter is a file contains query terms.
 
- In this file, each line contains a set of query terms that are separated by blank spaces. An example query term file is given in the following:

- Query terms are of the form in the query_file.txt file.
query_term1 query_term2 
query_term3 query_term4 query_term5
 
-Each set of query terms will trigger an execution of the getPostings, termAtATimeQueryAnd, termAtATimeQueryOr, docAtATimeQueryAnd and docAtATimeQueryOr once. For an example, if we have the aforementioned two-line example query file, you should record the outputs in the following order.
 
-So, the command line construct is: java CSE535Assignment term.idx output.log 10 query_file.txt 
