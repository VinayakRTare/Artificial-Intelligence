Name: Vinayak Ravindra Tare
UTA ID: 1001453869
Programming language: Java

How to run the Code - Compile using command: javac *.java
run using command
java CheckTrueFalse wumpus_rules.txt [additional_knowledge_file] [statement_file] 
e.g. : java CheckTrueFalse wumpus_rules.txt kb.txt statement.txt


wumpus_rules.txt : contains wumpus world rules
kb.txt : sample knowledge base
statement.txt : sample statement to be evaluated


CheckTrueFalse.java : Contains main method 

TT-entails() : implementation of entailment algorithm, returns true if Knowledge base entails alpha(Statement) else false

TT-CheckAll() : This method creates all possible 2^n models(row of truth table), and for each model checks weather Knowledge Base and alpha both satisfies or not.
return true if KB entails alpha else false


