
import java.io.*;
import java.util.*;

/**
 * @author Vinayak Tare
 *
 */
public class CheckTrueFalse {

	/**
	 * @param args
	 */
	public static int monster[][] = new int[5][5];
	public static int pit[][] = new int[5][5];
	public static int stench[][] = new int[5][5];
	public static int breeze[][] = new int[5][5];
	

	
	public static boolean negStmtTrue = false;
	public static ArrayList<String> symbols = new ArrayList<String>();
	public static ArrayList<String> symbols2;
	public static HashMap<String, Boolean> model = new HashMap<String, Boolean>();
	public static String finalResultFile = "result.txt";
	
	public static void main(String[] args) {

		if (args.length != 3) {
			// takes three arguments
			System.out.println("Usage: " + args[0] + " [wumpus-rules-file] [additional-knowledge-file] [input_file]\n");
			exit_function(0);
		}
		
		initializeDefaultSymbols();

		// create some buffered IO streams
		String buffer;
		BufferedReader inputStream;
		BufferedWriter outputStream;

		// create the knowledge base and the statement
		LogicalExpression knowledge_base = new LogicalExpression();
		LogicalExpression statement = new LogicalExpression();

		// open the wumpus_rules.txt
		try {
			inputStream = new BufferedReader(new FileReader(args[0]));

			// load the wumpus rules
			System.out.println("loading the wumpus rules...");
			knowledge_base.setConnective("and");

			while ((buffer = inputStream.readLine()) != null) {
				if (!(buffer.startsWith("#") || (buffer.equals("")))) {
					// the line is not a comment
					initSymbolVal(buffer);
					LogicalExpression subExpression = readExpression(buffer);
					knowledge_base.setSubexpression(subExpression);
				} else {
					// the line is a comment. do nothing and read the next line
				}
			}

			// close the input file
			inputStream.close();

		} catch (Exception e) {
			System.out.println("failed to open " + args[0]);
			e.printStackTrace();
			exit_function(0);
		}
		// end reading wumpus rules

		// read the additional knowledge file
		try {
			inputStream = new BufferedReader(new FileReader(args[1]));

			// load the additional knowledge
			System.out.println("loading the additional knowledge...");

			// the connective for knowledge_base is already set. no need to set
			// it again.
			// i might want the LogicalExpression.setConnective() method to
			// check for that
			// knowledge_base.setConnective("and");

			while ((buffer = inputStream.readLine()) != null) {
				if (!(buffer.startsWith("#") || (buffer.equals("")))) {
					initSymbolVal(buffer);
					LogicalExpression subExpression = readExpression(buffer);
					knowledge_base.setSubexpression(subExpression);
				} else {
					// the line is a comment. do nothing and read the next line
				}
			}

			// close the input file
			inputStream.close();

		} catch (Exception e) {
			System.out.println("failed to open " + args[1]);
			e.printStackTrace();
			exit_function(0);
		}
		// end reading additional knowledge

		// check for a valid knowledge_base
		if (!valid_expression(knowledge_base)) {
			System.out.println("invalid knowledge base");
			exit_function(0);
		}

		// print the knowledge_base
		knowledge_base.print_expression("\n");
		initializeSymbols();
		// read the statement file
		try {
			inputStream = new BufferedReader(new FileReader(args[2]));

			System.out.println("\n\nLoading the statement file...");
			// buffer = inputStream.readLine();

			// actually read the statement file
			// assuming that the statement file is only one line long
			while ((buffer = inputStream.readLine()) != null) {
				if (!buffer.startsWith("#")) {
					// the line is not a comment
					statement = readExpression(buffer);
					break;
				} else {
					// the line is a commend. no nothing and read the next line
				}
			}

			// close the input file
			inputStream.close();

		} catch (Exception e) {
			System.out.println("failed to open " + args[2]);
			e.printStackTrace();
			exit_function(0);
		}
		// end reading the statement file

		// check for a valid statement
		if (!valid_expression(statement)) {
			System.out.println("invalid statement");
			exit_function(0);
		}

		// print the statement
		statement.print_expression("");
		// print a new line
		System.out.println("\n");
		
		symbols2 = new ArrayList<String>(symbols);
		
		System.out.println("Computing result....");
		
		boolean ttEntailsAlpha = TT_Entails(knowledge_base, statement, symbols, model);
		System.out.println("TT entails alpha  "+ttEntailsAlpha);
		negStmtTrue = true;
		// testing
		boolean ttEntailsNegAlpha = TT_Entails(knowledge_base, statement, symbols2, model);
		negStmtTrue = false;
		finalResult(ttEntailsAlpha,ttEntailsNegAlpha);
		//System.out.println("I don't know if the statement is definitely true or definitely false.");

	} // end of main

	/*
	 * this method reads logical expressions if the next string is a: - '(' =>
	 * then the next 'symbol' is a subexpression - else => it must be a
	 * unique_symbol
	 * 
	 * it returns a logical expression
	 * 
	 * notes: i'm not sure that I need the counter
	 * 
	 */
	public static LogicalExpression readExpression(String input_string) {
		LogicalExpression result = new LogicalExpression();

		// testing
		// System.out.println("readExpression() beginning -"+ input_string
		// +"-");
		// testing
		// System.out.println("\nread_exp");

		// trim the whitespace off
		input_string = input_string.trim();

		if (input_string.startsWith("(")) {
			// its a subexpression

			String symbolString = "";

			// remove the '(' from the input string
			symbolString = input_string.substring(1);
			// symbolString.trim();

			// testing
			// System.out.println("readExpression() without opening paren -"+
			// symbolString + "-");

			if (!symbolString.endsWith(")")) {
				// missing the closing paren - invalid expression
				System.out.println("missing ')' !!! - invalid expression! - readExpression():-" + symbolString);
				exit_function(0);

			} else {
				// remove the last ')'
				// it should be at the end
				symbolString = symbolString.substring(0, (symbolString.length() - 1));
				symbolString.trim();

				// testing
				// System.out.println("readExpression() without closing paren
				// -"+ symbolString + "-");

				// read the connective into the result LogicalExpression object
				symbolString = result.setConnective(symbolString);

				// testing
				// System.out.println("added connective:-" +
				// result.getConnective() + "-: here is the string that is left
				// -" + symbolString + "-:");
				// System.out.println("added connective:->" +
				// result.getConnective() + "<-");
			}

			// read the subexpressions into a vector and call setSubExpressions(
			// Vector );
			result.setSubexpressions(read_subexpressions(symbolString));

		} else {
			// the next symbol must be a unique symbol
			// if the unique symbol is not valid, the setUniqueSymbol will tell
			// us.
			result.setUniqueSymbol(input_string);

			// testing
			// System.out.println(" added:-" + input_string + "-:as a unique
			// symbol: readExpression()" );
		}

		return result;
	}

	/*
	 * this method reads in all of the unique symbols of a subexpression the
	 * only place it is called is by read_expression(String, long)(( the only
	 * read_expression that actually does something ));
	 * 
	 * each string is EITHER: - a unique Symbol - a subexpression - Delineated
	 * by spaces, and paren pairs
	 * 
	 * it returns a vector of logicalExpressions
	 * 
	 * 
	 */

	public static Vector<LogicalExpression> read_subexpressions(String input_string) {

		Vector<LogicalExpression> symbolList = new Vector<LogicalExpression>();
		LogicalExpression newExpression;// = new LogicalExpression();
		String newSymbol = new String();

		// testing
		// System.out.println("reading subexpressions! beginning-" +
		// input_string +"-:");
		// System.out.println("\nread_sub");

		input_string.trim();

		while (input_string.length() > 0) {

			newExpression = new LogicalExpression();

			// testing
			// System.out.println("read subexpression() entered while with
			// input_string.length ->" + input_string.length() +"<-");

			if (input_string.startsWith("(")) {
				// its a subexpression.
				// have readExpression parse it into a LogicalExpression object

				// testing
				// System.out.println("read_subexpression() entered if with: ->"
				// + input_string + "<-");

				// find the matching ')'
				int parenCounter = 1;
				int matchingIndex = 1;
				while ((parenCounter > 0) && (matchingIndex < input_string.length())) {
					if (input_string.charAt(matchingIndex) == '(') {
						parenCounter++;
					} else if (input_string.charAt(matchingIndex) == ')') {
						parenCounter--;
					}
					matchingIndex++;
				}

				// read untill the matching ')' into a new string
				newSymbol = input_string.substring(0, matchingIndex);

				// testing
				// System.out.println( "-----read_subExpression() - calling
				// readExpression with: ->" + newSymbol + "<- matchingIndex is
				// ->" + matchingIndex );

				// pass that string to readExpression,
				newExpression = readExpression(newSymbol);

				// add the LogicalExpression that it returns to the vector
				// symbolList
				symbolList.add(newExpression);

				// trim the logicalExpression from the input_string for further
				// processing
				input_string = input_string.substring(newSymbol.length(), input_string.length());

			} else {
				// its a unique symbol ( if its not, setUniqueSymbol() will tell
				// us )

				// I only want the first symbol, so, create a LogicalExpression
				// object and
				// add the object to the vector

				if (input_string.contains(" ")) {
					// remove the first string from the string
					newSymbol = input_string.substring(0, input_string.indexOf(" "));
					input_string = input_string.substring((newSymbol.length() + 1), input_string.length());

					// testing
					// System.out.println( "read_subExpression: i just read ->"
					// + newSymbol + "<- and i have left ->" + input_string
					// +"<-" );
				} else {
					newSymbol = input_string;
					input_string = "";
				}

				// testing
				// System.out.println( "readSubExpressions() - trying to add -"
				// + newSymbol + "- as a unique symbol with ->" + input_string +
				// "<- left" );

				newExpression.setUniqueSymbol(newSymbol);

				// testing
				// System.out.println("readSubexpression(): added:-" + newSymbol
				// + "-:as a unique symbol. adding it to the vector" );

				symbolList.add(newExpression);

				// testing
				// System.out.println("read_subexpression() - after adding: ->"
				// + newSymbol + "<- i have left ->"+ input_string + "<-");

			}

			// testing
			// System.out.println("read_subExpression() - left to parse ->" +
			// input_string + "<-beforeTrim end of while");

			input_string.trim();

			if (input_string.startsWith(" ")) {
				// remove the leading whitespace
				input_string = input_string.substring(1);
			}

			// testing
			// System.out.println("read_subExpression() - left to parse ->" +
			// input_string + "<-afterTrim with string length-" +
			// input_string.length() + "<- end of while");
		}
		return symbolList;
	}

	/*
	 * this method checks to see if a logical expression is valid or not a valid
	 * expression either: ( this is an XOR ) - is a unique_symbol - has: -- a
	 * connective -- a vector of logical expressions
	 * 
	 */
	public static boolean valid_expression(LogicalExpression expression) {

		// checks for an empty symbol
		// if symbol is not empty, check the symbol and
		// return the truthiness of the validity of that symbol

		if (!(expression.getUniqueSymbol() == null) && (expression.getConnective() == null)) {
			// we have a unique symbol, check to see if its valid
			return valid_symbol(expression.getUniqueSymbol());

			// testing
			// System.out.println("valid_expression method: symbol is not
			// empty!\n");
		}

		// symbol is empty, so
		// check to make sure the connective is valid

		// check for 'if / iff'
		if ((expression.getConnective().equalsIgnoreCase("if"))
				|| (expression.getConnective().equalsIgnoreCase("iff"))) {

			// the connective is either 'if' or 'iff' - so check the number of
			// connectives
			if (expression.getSubexpressions().size() != 2) {
				System.out.println("error: connective \"" + expression.getConnective() + "\" with "
						+ expression.getSubexpressions().size() + " arguments\n");
				return false;
			}
		}
		// end 'if / iff' check

		// check for 'not'
		else if (expression.getConnective().equalsIgnoreCase("not")) {
			// the connective is NOT - there can be only one symbol /
			// subexpression
			if (expression.getSubexpressions().size() != 1) {
				System.out.println("error: connective \"" + expression.getConnective() + "\" with "
						+ expression.getSubexpressions().size() + " arguments\n");
				return false;
			}
		}
		// end check for 'not'

		// check for 'and / or / xor'
		else if ((!expression.getConnective().equalsIgnoreCase("and"))
				&& (!expression.getConnective().equalsIgnoreCase("or"))
				&& (!expression.getConnective().equalsIgnoreCase("xor"))) {
			System.out.println("error: unknown connective " + expression.getConnective() + "\n");
			return false;
		}
		// end check for 'and / or / not'
		// end connective check

		// checks for validity of the logical_expression 'symbols' that go with
		// the connective
		for (Enumeration e = expression.getSubexpressions().elements(); e.hasMoreElements();) {
			LogicalExpression testExpression = (LogicalExpression) e.nextElement();

			// for each subExpression in expression,
			// check to see if the subexpression is valid
			if (!valid_expression(testExpression)) {
				return false;
			}
		}

		// testing
		// System.out.println("The expression is valid");

		// if the method made it here, the expression must be valid
		return true;
	}

	/** this function checks to see if a unique symbol is valid */
	//////////////////// this function should be done and complete
	// originally returned a data type of long.
	// I think this needs to return true /false
	// public long valid_symbol( String symbol ) {
	public static boolean valid_symbol(String symbol) {
		if (symbol == null || (symbol.length() == 0)) {

			// testing
			// System.out.println("String: " + symbol + " is invalid! Symbol is
			// either Null or the length is zero!\n");

			return false;
		}

		for (int counter = 0; counter < symbol.length(); counter++) {
			if ((symbol.charAt(counter) != '_') && (!Character.isLetterOrDigit(symbol.charAt(counter)))) {

				System.out.println("String: " + symbol + " is invalid! Offending character:---" + symbol.charAt(counter)
						+ "---\n");

				return false;
			}
		}

		// the characters of the symbol string are either a letter or a digit or
		// an underscore,
		// return true
		return true;
	}

	/***
	 * 
	 * Initialize symbols to default value
	 * 
	 */
	
	private static void initializeDefaultSymbols() {

        for (int i = 1; i < 5; i++) {
            for (int j = 1; j < 5; j++) {
                monster[i][j] = -1;
                pit[i][j] = -1;
                stench[i][j] = -1;
                breeze[i][j] = -1;
            }
        }
        
    }
	
	/***
	 * 
	 * 
	 * This method assigns value to symbols
	 * 
	 * 
	 * 
	 * @param ruleLine
	 */

	private static void initSymbolVal(String ruleLine) {
		// TODO Auto-generated method stub
		int val = 1;
		String symbol = ruleLine;
		String symbol_initials = null;
		String[] symbol_literals = new String[3];

		if (!ruleLine.startsWith("(")) {
			val = 1;
		} else if ((ruleLine.startsWith("(not") || ruleLine.startsWith("(NOT"))
				&& !(ruleLine.startsWith("(not (") || ruleLine.startsWith("(NOT ("))) {

			val = 0;
			symbol = ruleLine.substring(ruleLine.indexOf(" ") + 1, ruleLine.indexOf(")"));
			System.out.println(symbol);
		} else {
			return;
		}

		symbol_literals = symbol.split("_");
		symbol_initials = symbol_literals[0];
		int x = Integer.parseInt(symbol_literals[1]);
		int y = Integer.parseInt(symbol_literals[2]);

		if (symbol_initials.equals("M")) {
			monster[x][y] = val;
		} else if (symbol_initials.equals("P")) {
			pit[x][y] = val;
		} else if (symbol_initials.equals("S")) {
			stench[x][y] = val;
		} else if (symbol_initials.equals("B")) {
			breeze[x][y] = val;
		} else {
			System.out.println("Invalid knowledge base format");
		}

	}
	
	/***
	 * 
	 * This method creates symbol list
	 */
	private static void initializeSymbols(){

        // TODO Auto-generated method stub

        for (int i = 1; i < 5; i++) {
            for (int j = 1; j < 5; j++) {
                if (monster[i][j] == -1) {
                    symbols.add("M_" + i + "_" + j);
                    model.put("M_" + i + "_" + j, false);
                }
                if (pit[i][j] == -1) {
                    symbols.add("P_" + i + "_" + j);
                    model.put("P_" + i + "_" + j, false);
                }
                if (stench[i][j] == -1) {
                    symbols.add("S_" + i + "_" + j);
                    model.put("S_" + i + "_" + j, false);
                }
                if (breeze[i][j] == -1) {
                    symbols.add("B_" + i + "_" + j);
                    model.put("B_" + i + "_" + j, false);
                }
            }
        }
       
		
	}
	
	
	  private static boolean TT_Entails(LogicalExpression knowledge_base, LogicalExpression alpha,
		        ArrayList<String> symbols_list, HashMap<String, Boolean> model) {
		    	//System.out.println("inside TT entails");
		        // TODO Auto-generated method stub
		        return TT_Check_All(knowledge_base, alpha, symbols_list, model);
		        
		    }
	  
	    private static boolean TT_Check_All(LogicalExpression knowledge_base, LogicalExpression alpha, ArrayList<String> symbols,
	            HashMap<String, Boolean> model) {
	            // TODO Auto-generated method stub
	        	//System.out.println("inside tt check all");
	            if (symbols.isEmpty()) {
	            	//System.out.println("TT check all model "+model);
	                if (PL_TRUE(knowledge_base, model, false)) {
	                	
	                    return PL_TRUE(alpha, model, negStmtTrue);
	                } else {
	                    return true;
	                }
	            } else {
	                String P = symbols.remove(0);
	                //System.out.println("L "+P);
	                ArrayList<String> rest = new ArrayList<String>(symbols);

	                return TT_Check_All(knowledge_base, alpha, rest, EXTENDS(P, true, model))
	                    && TT_Check_All(knowledge_base, alpha, rest, EXTENDS(P, false, model));
	                
	            }
	        }
	    
	    
	    private static HashMap<String, Boolean> EXTENDS(String P, boolean value, HashMap<String, Boolean> model) {
	        // TODO Auto-generated method stub
	        model.put(P, value);
	        return model;
	    }
	    
	    private static boolean PL_TRUE(LogicalExpression logical_statement, HashMap<String, Boolean> model,
	            boolean negStmtTrue) {
	            // TODO Auto-generated method stub

	            boolean result = logical_statement.solve_expressions(model);
	           // System.out.println("PL_True  "+result);
	            LogicalExpression.clearStack();

	            if (negStmtTrue) {
	                return !result;
	            } else {
	                return result;
	            }
	        }
	    
	    
	    private static void finalResult(boolean ttEntailsAlpha, boolean ttEntailsNegAlpha) {
	        // TODO Auto-generated method stub
	        String result = "I don't know if the statement is definitely true or definitely false.";
	        if (ttEntailsAlpha && !ttEntailsNegAlpha) {
	        	result = "definitely true";
	        } else if (!ttEntailsAlpha && ttEntailsNegAlpha) {
	        	result = "definitely false";
	        } else if (!ttEntailsAlpha && !ttEntailsNegAlpha) {
	        	result = "possibly true, possibly false";
	        } else if (ttEntailsAlpha && ttEntailsNegAlpha) {
	        	result = "both true and false";
	        }

	                
	        System.out.println("\n\n Result : "+result);
	        printResult(result, finalResultFile);

	    }
	    public static void printResult(String result, String outputFile) {
	        try {
	            BufferedWriter output = new BufferedWriter(new FileWriter(outputFile));

	            // write the current turn
	            output.write(result + "\r\n");
	            output.close();

	        } catch (IOException e) {
	            System.out.println("\nProblem writing to the result file!\n" + "Try again.");
	            e.printStackTrace();
	        }
	    }


	    public static boolean getValueFromArray(String symbol) {
	        // TODO Auto-generated method stub

	        String symbol_initials = null;
	        String[] symbol_literals = new String[3];

	        symbol_literals = symbol.split("_");
	        symbol_initials = symbol_literals[0];
	        int x = Integer.parseInt(symbol_literals[1]);
	        int y = Integer.parseInt(symbol_literals[2]);

	        if (symbol_initials.equals("M")) {
	            if (monster[x][y] == 1) {
	                return true;
	            } else {
	                return false;
	            }
	        } else if (symbol_initials.equals("P")) {
	            if (pit[x][y] == 1) {
	                return true;
	            } else {
	                return false;
	            }
	        } else if (symbol_initials.equals("S")) {
	            if (stench[x][y] == 1) {
	                return true;
	            } else {
	                return false;
	            }
	        } else if (symbol_initials.equals("B")) {
	            if (breeze[x][y] == 1) {
	                return true;
	            } else {
	                return false;
	            }
	        } else {
	            System.out.println("Invalid Symbol format!!");
	        }
	        return false;
	    }



	private static void exit_function(int value) {
		System.out.println("exiting from checkTrueFalse");
		System.exit(value);
	}
}