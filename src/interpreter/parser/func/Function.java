/**
 * Function.java is a part of Lispreter. 
 */
package interpreter.parser.func;

import interpreter.exception.FuncDefException;
import interpreter.exception.NodeInitException;
import interpreter.parser.Node;
import interpreter.parser.SExpression;
import interpreter.parser.util.Pat;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * @author Anand
 *
 */
public abstract class Function {

	protected List<String> params;
	protected Node body;

	public Function(Node params, Node body) {
		if (!(params.isList() || params.toString().equals("NIL"))) {
			throw new FuncDefException("Invalid function parameters");
		}
		if (!(body.isList() || body.toString().equals("NIL"))) {
			throw new FuncDefException("Invalid function body");
		}
		this.params = convertParams(params.toString());
		this.body = body;
	}

	/**
	 * Evaluates a function by invoking its body with passed arguments.
	 * 
	 * @param args
	 *            the list of actuals.
	 * @return the resultant Node after evaluation.
	 */
	public Node eval(Node args) {
		return body.eval(true, bind(args));
	}

	/**
	 * Create a list of parameters from a string containing the formals.
	 * Additionally validifies parameters.
	 * 
	 * @param formals
	 *            the String of parameters.
	 * @return a List of parameter names.
	 * @throws FuncDefException
	 *             if parameters are not distinct or not well-formed.
	 */
	private List<String> convertParams(String formals) {
		String[] words = formals.substring(1, formals.length() - 1)
				.split("\\s");
		List<String> result = new ArrayList<>();
		for (String word : words) {
			if (Pat.VALID_FUNC.matches(word)) {
				if (result.contains(word)) {
					throw new FuncDefException(
							"Formal param names cannot be duplicates.");
				}
				result.add(word);
			} else {
				throw new FuncDefException(
						"Parameter names must be alphanumeric literals : "
								+ word);
			}
		}
		return result;
	}

	/**
	 * Constructs a binding table of formals -> actuals.
	 * 
	 * @param actuals
	 *            the Node of actual parameters.
	 * @return a binding table.
	 */
	protected abstract Hashtable<String, Node> bind(Node actuals);
}