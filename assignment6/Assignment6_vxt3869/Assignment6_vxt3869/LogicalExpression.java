

import java.util.*;

/**
 * @author Vinayak Tare
 *
 */
public class LogicalExpression {

	private String uniqueSymbol = null; // null if sentence is a more complex
										// expression
	private String connective = null; // null if sentence is a _UNIQUE_ symbol
	private Vector<LogicalExpression> subexpressions = null; // a vector of
																// LogicalExpressions
																// ( basically a
																// vector of
																// unique
																// symbols and
																// subexpressions
																// )

	public static Stack<String> symbolStack = new Stack<String>();
	private static boolean final_result;

	
	// constructor
	public LogicalExpression() {
		// these need to stay null if they're empty
		// this.uniqueSymbol = "0";
		// this.connective = "0";
		this.subexpressions = new Vector<LogicalExpression>();
	}

	// another constructor that will ( or is supposed to ) create
	// a new logical Expression, essentially making a copy
	public LogicalExpression(LogicalExpression oldExpression) {

		if (oldExpression.getUniqueSymbol() == null) {
			this.uniqueSymbol = oldExpression.getUniqueSymbol();
		} else {
			// create a new logical expression from the one passed to it
			this.connective = oldExpression.getConnective();

			// now get all of the subExpressions
			// hint, enumerate over the subexpression vector of newExpression
			for (Enumeration e = oldExpression.getSubexpressions().elements(); e.hasMoreElements();) {
				LogicalExpression nextExpression = (LogicalExpression) e.nextElement();

				this.subexpressions.add(nextExpression);
			}
		}

	}

	/*
	 * this method replaces _part_ of read_word() this method sets the symbol
	 * for the LogicalExpression it checks to make sure that it starts with one
	 * of the appropriate letters, then checks to make sure that the rest of the
	 * string is either digits or '_'
	 */
	public void setUniqueSymbol(String newSymbol) {
		boolean valid = true;

		// remove the leading whitespace
		newSymbol.trim();

		if (this.uniqueSymbol != null) {
			System.out.println("setUniqueSymbol(): - this LE already has a unique symbol!!!" + "\nswapping :->"
					+ this.uniqueSymbol + "<- for ->" + newSymbol + "<-\n");
		} else if (valid) {
			this.uniqueSymbol = newSymbol;
			// testing
			// System.out.println(" setUniqueSymbol() - added-" + newSymbol + "-
			// to the LogicalExpression! ");
		}
	}

	/*
	 * this method replaces _part_ of read_word() from the example code it sets
	 * the connective for this LogicalExpression
	 * 
	 * and returns the rest of the string to collect the symbols for it
	 */
	public String setConnective(String inputString) {

		String connect;

		// testing
		// System.out.println("setConnective() - beginning -" + inputString +
		// "-");

		// trim the whitespace at the beginning of the string if there is any
		// there shouldn't be
		inputString.trim();

		// if the first character of the inputString is a '('
		// - remove the ')' and the ')' and any whitespace after it.
		if (inputString.startsWith("(")) {
			inputString = inputString.substring(inputString.indexOf('('), inputString.length());

			// trim the whitespace
			inputString.trim();
		}

		// testing
		// System.out.println("here: setConnective1- inputString:" + inputString
		// + "--");

		if (inputString.contains(" ")) {
			// remove the connective out of the string
			connect = inputString.substring(0, inputString.indexOf(" "));
			inputString = inputString.substring((connect.length() + 1), inputString.length());
			// inputString.trim();

			// testing
			// System.out.println("I have the connective -" + connect + "- and
			// i've got symbols -" + inputString + "-");

		} else {
			// just set to get checked and empty the inputString
			// huh?
			connect = inputString;
			inputString = "";
		}

		// if connect is a proper connective
		if (connect.equalsIgnoreCase("if") || connect.equalsIgnoreCase("iff") || connect.equalsIgnoreCase("and")
				|| connect.equalsIgnoreCase("or") || connect.equalsIgnoreCase("xor")
				|| connect.equalsIgnoreCase("not")) {
			// ok, first word in the string is a valid connective

			// set the connective
			this.connective = connect;

			// testing
			// System.out.println( "setConnective(): I have just set the
			// connective\n->" + connect + "<-\nand i'm returning\n->" +
			// inputString + "<-" );

			return inputString;

		} else {
			System.out.println("unexpected character!!! : invalid connective!! - setConnective():-" + inputString);
			this.exit_function(0);
		}

		// invalid connective - no clue who it would get here
		System.out.println(" invalid connective! : setConnective:-" + inputString);
		return inputString;
	}

	public void setSubexpression(LogicalExpression newSub) {
		this.subexpressions.add(newSub);
	}

	public void setSubexpressions(Vector<LogicalExpression> symbols) {
		this.subexpressions = symbols;

	}

	public String getUniqueSymbol() {
		return this.uniqueSymbol;
	}

	public String getConnective() {
		return this.connective;
	}

	public LogicalExpression getNextSubexpression() {
		return this.subexpressions.lastElement();
	}

	public Vector getSubexpressions() {
		return this.subexpressions;
	}

	/************************* end getters and setters *************/

	public void print_expression(String separator) {

		if (!(this.uniqueSymbol == null)) {
			System.out.print(this.uniqueSymbol.toUpperCase());
		} else {
			// else the symbol is a nested logical expression not a unique
			// symbol
			LogicalExpression nextExpression;

			// print the connective
			System.out.print("(" + this.connective.toUpperCase());

			// enumerate over the 'symbols' ( LogicalExpression objects ) and
			// print them
			for (Enumeration e = this.subexpressions.elements(); e.hasMoreElements();) {
				nextExpression = (LogicalExpression) e.nextElement();

				System.out.print(" ");
				nextExpression.print_expression("");
				System.out.print(separator);
			}

			System.out.print(")");
		}
	}
	
	public boolean solve_expressions(HashMap<String, Boolean> model) {
				if (this.getUniqueSymbol() != null) {
			symbolStack.push(this.getUniqueSymbol());
		} else {
			LogicalExpression nextExpression;

			symbolStack.push(this.getConnective());
			for (Enumeration e = this.subexpressions.elements(); e.hasMoreElements();) {
				nextExpression = (LogicalExpression) e.nextElement();
				nextExpression.solve_expressions(model);
			}

			final_result = getResult(model);
		}
		return final_result;
	}
	private boolean getResult(HashMap<String, Boolean> model) {
		// TODO Auto-generated method stub
		String symbol, connective;
		boolean result = false;
		ArrayList<String> uniqSymbols = new ArrayList<String>();
		

		do {
			symbol = symbolStack.pop();
			uniqSymbols.add(symbol);
		} while (!isConnective(symbol));

		uniqSymbols.remove(symbol);
		connective = symbol;

		if (connective.equalsIgnoreCase("or")) { // can have more than two
													// unique symbols
			result = false;
			while (!uniqSymbols.isEmpty() && !result) {
				result = result || getValue(uniqSymbols.remove(0), model);
			}
		} else if (connective.equalsIgnoreCase("and")) { // can have more than
															// two unique
															// symbols
			result = true;
			while (!uniqSymbols.isEmpty() && result) {
				result = result && getValue(uniqSymbols.remove(0), model);
			}
		} else if (connective.equalsIgnoreCase("not")) {
			result = true;
			result = !getValue(uniqSymbols.remove(0), model);
		} else if (connective.equalsIgnoreCase("xor")) { // result = a'b + ab'
															// can have more
															// than two unique
															// symbols
			result = false;
			int no_of_true_symbol = 0;
			while (!uniqSymbols.isEmpty()) {
				if (getValue(uniqSymbols.remove(0), model)) {
					no_of_true_symbol++;
				}
			}
			if (no_of_true_symbol == 1) {
				result = true;
			}
		} else if (connective.equalsIgnoreCase("if")) { // required exactly two
														// symbols
			result = true;
			if (uniqSymbols.size() == 2) {
				if (getValue(uniqSymbols.get(1), model) && !getValue(uniqSymbols.get(0), model)) {
					result = false;
				}
			}
		} else if (connective.equalsIgnoreCase("iff")) { // result = a'b' + ab
															// required exactly
															// two symbols
			result = false;
			if (uniqSymbols.size() == 2) {
				boolean symbol1 = getValue(uniqSymbols.get(1), model);
				boolean symbol2 = getValue(uniqSymbols.get(0), model);
				if ((!symbol1 && !symbol2) || (symbol1 && symbol2)) {
					result = true;
				}
			}
		} else {
			System.out.println("Invalid Connective");
		}

		if (result) { // push evaluated result again on top of stack for further
						// use
			symbolStack.push("T");
		} else {
			symbolStack.push("F");
		}

		return result;
	}
	
	private boolean isConnective(String symbol) {
		// TODO Auto-generated method stub
		return (symbol.equalsIgnoreCase("if") || symbol.equalsIgnoreCase("iff") || symbol.equalsIgnoreCase("and")
				|| symbol.equalsIgnoreCase("or") || symbol.equalsIgnoreCase("xor") || symbol.equalsIgnoreCase("not"));
	}

	private boolean getValue(String symbol, HashMap<String, Boolean> model) {
		// TODO Auto-generated method stub
		if (symbol.equalsIgnoreCase("T")) {
			return true;
		} else if (symbol.equalsIgnoreCase("F")) {
			return false;
		} else if (model.get(symbol) == null) {
			return CheckTrueFalse.getValueFromArray(symbol);
		} else {
			return model.get(symbol);
		}
	}

	public static void clearStack() {
		// TODO Auto-generated method stub
		if (!(symbolStack == null)) {
			symbolStack.clear();
		}
	}

	
	private static void exit_function(int value) {
		System.out.println("exiting from LogicalExpression");
		System.exit(value);
	}
}
