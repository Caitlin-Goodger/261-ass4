import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.*;
import javax.swing.JFileChooser;

/**
 * The parser and interpreter. The top level parse function, a main method for
 * testing, and several utility methods are provided. You need to implement
 * parseProgram and all the rest of the parser.
 */
public class Parser {
	static List<VariableNode> variables = new ArrayList<>(); //List of variables in the file
	/**
	 * Top level parse method, called by the World
	 */
	static RobotProgramNode parseFile(File code) {
		Scanner scan = null;
		try {
			scan = new Scanner(code);

			// the only time tokens can be next to each other is
			// when one of them is one of (){},;
			scan.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");

			RobotProgramNode n = parseProgram(scan); // You need to implement this!!!

			scan.close();
			return n;
		} catch (FileNotFoundException e) {
			System.out.println("Robot program source file not found");
		} catch (ParserFailureException e) {
			System.out.println("Parser error:");
			System.out.println(e.getMessage());
			scan.close();
		}
		return null;
	}

	/** For testing the parser without requiring the world */

	public static void main(String[] args) {
		if (args.length > 0) {
			for (String arg : args) {
				File f = new File(arg);
				if (f.exists()) {
					System.out.println("Parsing '" + f + "'");
					variables = new ArrayList<>();
					RobotProgramNode prog = parseFile(f);
					System.out.println("Parsing completed ");
					if (prog != null) {
						System.out.println("================\nProgram:");
						System.out.println(prog);
					}
					System.out.println("=================");
				} else {
					System.out.println("Can't find file '" + f + "'");
				}
			}
		} else {
			while (true) {
				JFileChooser chooser = new JFileChooser(".");// System.getProperty("user.dir"));
				int res = chooser.showOpenDialog(null);
				if (res != JFileChooser.APPROVE_OPTION) {
					break;
				}
				RobotProgramNode prog = parseFile(chooser.getSelectedFile());
				System.out.println("Parsing completed");
				if (prog != null) {
					System.out.println("Program: \n" + prog);
				}
				System.out.println("=================");
			}
		}
		System.out.println("Done");
	}

	// Useful Patterns

	static Pattern NUMPAT = Pattern.compile("-?\\d+"); // ("-?(0|[1-9][0-9]*)");
	static Pattern OPENPAREN = Pattern.compile("\\(");
	static Pattern CLOSEPAREN = Pattern.compile("\\)");
	static Pattern OPENBRACE = Pattern.compile("\\{");
	static Pattern CLOSEBRACE = Pattern.compile("\\}");

	/**
	 * PROG ::= STMT+
	 */
	static RobotProgramNode parseProgram(Scanner s) {
		List<RobotProgramNode> commands = new ArrayList<>();
		//Read all the commands and put them into a blocknode
		while(s.hasNext()) {
				commands.add(parseCommand(s));
		}
		return new BlockNode(commands);
	}

	/**
	 * Read a token from the scan and depending on what it is, call the corresponding method
	 * @param s
	 * @return
	 */
	public static RobotProgramNode parseCommand(Scanner s) {
		String token = s.next();
		if(token.equals("loop")) {
			List<RobotProgramNode> block = new ArrayList<>();
			if (!checkFor(OPENBRACE, s)) { //Check there is an open bracket
				fail("Expected open bracket", s);
			}
			while(!checkFor(CLOSEBRACE,s)) {
				if(!s.hasNext()) {
					fail("Expected closed bracket",s); //Check there is a closing bracket
				}
				block.add(parseCommand(s));
			}
			if(block.size() ==0) { //Loop needs at least one statement in it
				fail("Requires at least 1 statement",s);
			}
			return new LoopNode(new BlockNode(block));
		} else if(token.equals("if")) {
			return parseIf(s,null,true);
		} else if(token.equals("while")) {
			List<RobotProgramNode> block = new ArrayList<>();
			if (!checkFor(OPENPAREN, s)) { //Check there is open bracket
				fail("Expected open bracket", s);
			}
			ConditionNode condition = parseCondition(s); //Read the condition for the loop
			if (!checkFor(CLOSEPAREN, s)) {
				fail("Expected closed bracket", s);
			}
			if (!checkFor(OPENBRACE, s)) {
				fail("Expected open bracket", s);
			}
			while (!checkFor(CLOSEBRACE, s)) {
				block.add(parseCommand(s)); //Read commands for the loop in
			}
			if(block.size() ==0) { //Check there is at least one statement
				fail("Requires at least 1 statement",s);
			}
			if (condition.v1 != null) {
				if (condition.v1.name.equals(condition.v2.name)) { //Check that it isn't going to be an infinite loop
					fail("Infinite loop", s);
				}
			}
			return new WhileNode(new BlockNode(block), condition);
		} else { // Else it is an action
			return parseAction(token,s);
		}
	}

	/**
	 * Read the Condition and check that it is valid
	 * Used for if and while loops
	 * @param s
	 * @return
	 */
	public static ConditionNode parseCondition(Scanner s) {
		String operator = s.next();
		if (!checkFor(OPENPAREN, s)) {
			fail("Expected open bracket", s);
		}
		//Different operators take different types of parameters
		if(operator.equals("not")) {
			ConditionNode c = new ConditionNode(operator,parseCondition(s),null);
			if (!checkFor(CLOSEPAREN, s)) {
				fail("Expected closed bracket", s);
			}
			return c;
		} else if(!(operator.equals("lt") || operator.equals("eq") || operator.equals("gt"))) {
			ConditionNode c1 = parseCondition(s);
			if(!(checkFor(",",s))) { //Comma expected between the conditions
				fail("Expected comma",s);
			}
			ConditionNode c2 = parseCondition(s);
			if (!checkFor(CLOSEPAREN, s)) {
				fail("Expected closed bracket", s);
			}
			return new ConditionNode(operator,c1,c2);
		}
		VariableNode v1 = parseVariable(s.next(),s);
		if(!(checkFor(",",s))) {
			fail("Expected comma",s);
		}
		VariableNode v2 = parseVariable(s.next(),s);
		if (!checkFor(CLOSEPAREN, s)) {
			fail("Expected closed bracket", s);
		}
		if (v1.robotVariable && v2.robotVariable) {
			fail("Can't compare", s);
		}
		if (v1.toString().equals(v2.toString())) { //Check that it isn't going to be an infinite loop
			fail("Infinte Loop", s);
		}
		return new ConditionNode(operator,v1,v2);
	}

	/**
	 * Read the variables in.
	 * @param str
	 * @param s
	 * @return
	 */
	public static VariableNode parseVariable(String str, Scanner s) {
		if(str.equals("fuelLeft")) {
			return new VariableNode("fuelLeft",0,true);
		} else if(str.equals("oppLR")) {
			return new VariableNode("oppLR",0,true);
		} else if(str.equals("oppFB")) {
			return new VariableNode("oppFB",0,true);
		} else if(str.equals("numBarrels")) {
			return new VariableNode("numBarrels",0,true);
		} else if(str.equals("barrelLR")) {
			return new VariableNode("barrelLR",0,true);
		} else if(str.equals("barrelFB")) {
			return new VariableNode("barrelFB",0,true);
		} else if(str.equals("wallDist")) {
			return new VariableNode("wallDist",0,true);
		} else {
			//If it isn't a variable check if it is a number
			boolean isInt = true;
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (c < '0' || c > '9') {
					if(!str.equals("-1")) {
						isInt = false;
					}
				}
			}
			if(isInt) {
				return new VariableNode(str,Integer.parseInt(str),false);
			}
			if(str.equals("add") || str.equals("sub") || str.equals("mul") || str.equals("div")) {
				if (!checkFor(OPENPAREN, s)) {
					fail("Expected open bracket", s);
				}
				VariableNode v1 = parseVariable(s.next(),s);
				if(!(checkFor(",",s))) {
					fail("Expected comma",s);
				}
				VariableNode v2 = parseVariable(s.next(),s);
				VariableNode v = new VariableNode(str,v1,v2);
				if (!checkFor(CLOSEPAREN, s)) {
					fail("Expected closed bracket", s);
				}
				return v;
			}
			//Check if it is an variable that has been declared
			for(int i = 0; i<variables.size();i++) {
				if(variables.get(i).name.equals(str)) {
					return variables.get(i);
				}
			}
			if(str.charAt(0) == '$') {
//				variables.add(new VariableNode(str,0,false));
//				return new VariableNode(str,0,false);
				fail("Declare variable before use",s); //Must declare variable before use
			}
		}
		fail("Invalid variable", s);
		return null;
	}

	/**
	 * Read If in.
	 * Own method so that if there is an elif it can be recursive
	 * @param s
	 * @param parentIf
	 * @param hasCon
	 * @return
	 */
	public static IfNode parseIf(Scanner s, IfNode parentIf, boolean hasCon) {
		List<RobotProgramNode> block = new ArrayList<>();
		ConditionNode condition = null;
		if(hasCon) {
			if(!checkFor(OPENPAREN,s)) {
				fail("Expected open brackets",s);
			}
			condition = parseCondition(s);
			if(!checkFor(CLOSEPAREN,s)) {
				fail("Expected close brackets", s);
			}
		}
		if (!checkFor(OPENBRACE, s)) {
			fail("Expected open bracket", s);
		}
		while (!checkFor(CLOSEBRACE, s)) {
			block.add(parseCommand(s));
		}
		if (block.size() == 0) {
			fail("Requires at least 1 statement",s);
		}
		IfNode n = new IfNode(new BlockNode(block), condition);
		if(s.hasNext("else if")) { // Check if there is an elif following
			s.next();
			if(parentIf == null) { //Call method again, making sure to add it to the if node that it started at
				n.ifNodes.add(parseIf(s,n,true));
			} else {
				parentIf.ifNodes.add(parseIf(s,parentIf,true));
			}
		} else if(s.hasNext("else")) { //Check if there is an else
			List<RobotProgramNode> blockElse = new ArrayList<>();
			s.next();
			if (!checkFor(OPENBRACE, s)) {
				fail("Expected open bracket", s);
			}
			while (!checkFor(CLOSEBRACE, s)) {
				blockElse.add(parseCommand(s));
			}
			if (blockElse.size() == 0) {
				fail("Requires at least 1 statement", s);
			}
			n.ifNodes.add(new IfNode(new BlockNode(blockElse), null));
		}
		return n;
	}

	/**
	 * Read the actions in
	 * @param token
	 * @param s
	 * @return
	 */
	public static RobotProgramNode parseAction(String token, Scanner s) {
		RobotProgramNode n = null;
		if (token.equals("move")) {
			if (checkFor(OPENPAREN, s)) {
				int amount = (int) parseExpression(s).getValue();
				if (!checkFor(CLOSEPAREN, s)) {
					fail("Requires close bracket", s);
				}
				n = new MoveNode(amount);
			} else {
				n = new MoveNode(1);
			}
		} else if (token.equals("turnL")) {
			n = new TurnLeftNode();
		} else if (token.equals("turnR")) {
			n = new TurnRightNode();
		} else if (token.equals("turnAround")) {
			n = new TurnAroundNode();
		} else if (token.equals("shieldOn")) {
			n = new ShieldNode(true);
		} else if (token.equals("shieldOff")) {
			n = new ShieldNode(false);
		} else if (token.equals("takeFuel")) {
			n = new TakeFuelNode();
		} else if (token.equals("wait")) {
			if (checkFor(OPENPAREN, s)) {
				int amount = (int) parseExpression(s).getValue();
				n = new WaitNode(amount);
				if (!(checkFor(CLOSEPAREN, s))) {
					fail("Requires close bracket", s);
				}
			} else {
				n = new WaitNode(1);
			}
		}else if (token.charAt(0) == '$') { //Check if it is a variable
			s.next();
			VariableNode v = parseExpression(s); //Read in the content of the variable
			n = new VariableNode(token,v,false);
			variables.add(new VariableNode(token,v,false));
		}else {
			fail("Unknown Token",s);
		}
		require(";","No semicolon",s);
		return n;
	}

	/**
	 * Read in an expression
	 * @param s
	 * @return
	 */
	public static VariableNode parseExpression(Scanner s) {
		String token = s.next();
		if(!(token.equals("add")|| token.equals("sub") || token.equals("mul") || token.equals("div"))) {
			return parseVariable(token,s);
		}
		if (!checkFor(OPENPAREN, s)) {
			fail("Expected open bracket", s);
		}
		VariableNode v1 = parseVariable(s.next(),s);
		if(!(checkFor(",",s))) {
			fail("Expected comma",s);
		}
		VariableNode v2 = parseVariable(s.next(),s);
		if (!checkFor(CLOSEPAREN, s)) {
			fail("Expected closed bracket", s);
		}
		return new VariableNode(token,v1,v2);
	}

	// utility methods for the parser

	/**
	 * Report a failure in the parser.
	 */
	static void fail(String message, Scanner s) {
		String msg = message + "\n   @ ...";
		for (int i = 0; i < 5 && s.hasNext(); i++) {
			msg += " " + s.next();
		}
		throw new ParserFailureException(msg + "...");
	}

	/**
	 * Requires that the next token matches a pattern if it matches, it consumes
	 * and returns the token, if not, it throws an exception with an error
	 * message
	 */
	static String require(String p, String message, Scanner s) {
		if (s.hasNext(p)) {
			return s.next();
		}
		fail(message, s);
		return null;
	}

	static String require(Pattern p, String message, Scanner s) {
		if (s.hasNext(p)) {
			return s.next();
		}
		fail(message, s);
		return null;
	}

	/**
	 * Requires that the next token matches a pattern (which should only match a
	 * number) if it matches, it consumes and returns the token as an integer if
	 * not, it throws an exception with an error message
	 */
	static int requireInt(String p, String message, Scanner s) {
		if (s.hasNext(p) && s.hasNextInt()) {
			return s.nextInt();
		}
		fail(message, s);
		return -1;
	}

	static int requireInt(Pattern p, String message, Scanner s) {
		if (s.hasNext(p) && s.hasNextInt()) {
			return s.nextInt();
		}
		fail(message, s);
		return -1;
	}

	/**
	 * Checks whether the next token in the scanner matches the specified
	 * pattern, if so, consumes the token and return true. Otherwise returns
	 * false without consuming anything.
	 */
	static boolean checkFor(String p, Scanner s) {
		if (s.hasNext(p)) {
			s.next();
			return true;
		} else {
			return false;
		}
	}

	static boolean checkFor(Pattern p, Scanner s) {
		if (s.hasNext(p)) {
			s.next();
			return true;
		} else {
			return false;
		}
	}

}

// You could add the node classes here, as long as they are not declared public (or private)
