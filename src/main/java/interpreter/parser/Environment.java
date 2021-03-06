/**
 * Environment.java is a part of Lispreter. 
 */
package interpreter.parser;

import interpreter.exception.EnvironmentException;
import interpreter.exception.FuncDefException;
import interpreter.parser.func.ClosureState;
import interpreter.parser.func.Function;
import interpreter.parser.prim.PrimitiveHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Contains the working 'd-list' of the lisp program. Manages function/var
 * binding within the program context.
 * 
 * @author Anand
 *
 */
public class Environment {

    private Map<String, Function> functions = new HashMap<>();
    private Map<Node, Function> lambdas = new HashMap<>();
    private Map<String, Node> variables = new HashMap<>();

    private PrimitiveHandler handler;

    private static Environment instance;

    /**
     * Constructs an Environment with a primitve handler instance for invoking
     * defined functions.
     * 
     * @param handler
     *            a PrimtiveHandler object.
     */
    private Environment(PrimitiveHandler handler) {
        this.handler = handler;
    }

    /**
     * Grabs the instance of this environment. If the environment currently
     * doesn't exist, a new one is constructed with a new PrimitiveHandler.
     * 
     * @return an Environment.
     */
    public static Environment getInstance() {
        if (instance == null) {
            instance = new Environment(new PrimitiveHandler());
        }
        return instance;
    }

    /**
     * Executes a given function with the given variable arguments.
     * 
     * @param name
     *            the name of the function to lookup
     * @param args
     *            the Node parameter arguments
     * @return the Node evaluation
     * @throws EnvironmentException
     *             if the given function name has not been defined.
     */
    public Node execFunc(String name, Node args) {
        if (!isDefinedF(name)) {
            throw new EnvironmentException("The function " + name
                    + " is undefined.");
        }
        return functions.get(name).eval(args);
    }

    public Node execLamb(Node args, Node body) {
        return lambdas.get(args).eval(body);
    }

    /**
     * Registers a function in the 'd-list' table. If the given name is
     * 'lambda', an anonymous function is registered.
     * 
     * @param name
     *            the identifier tag of the function.
     * @param args
     *            the formal arguments of the function.
     * @param body
     *            the literal or sexp function body.
     */
    public void registerFunc(String name, Node args, Node body) {
        if (name.toLowerCase().matches("lambda|λ")) {
            throw new FuncDefException(
                    "Use the anonymous function registration to register a lambda expression.");
        }
        functions.put(name, new Function(name, args, body));
    }

    /**
     * Registers a function in the anonymous table.
     * 
     * @param args
     *            the formal arguments of the function.
     * @param body
     *            the literal or sexp function body.
     */
    public void registerAnon(Node args, Node body) {
        lambdas.put(args, new Function("lambda", args, body));
        ClosureState.getInstance().setNextNode(args);
    }

    /**
     * Merges in new variable bindings into the working environment.
     * 
     * @param table
     *            a hashtable of var bindings (String -> Node).
     */
    public Map<String, Node> substitute(Map<String, Node> table) {
        variables.putAll(table);
        return getVariables();
    }

    /**
     * Unbinds a given variable name, if it exists in the table.
     * 
     * @param name
     *            the variable name to unbind.
     * @throws EnvironmentException
     *             if the variable is undefined.
     */
    public void unbind(String name) {
        if (!isDefinedV(name)) {
            throw new EnvironmentException("The variable " + name
                    + " is undefined.");
        }
        variables.remove(name);
    }

    /**
     * Unbinds a set of variables, if they exist in the table. Used to remove
     * variables from the environment.
     * 
     * @param names
     *            the variable names to unbind.
     * @throws EnvironmentException
     *             if any variable is undefined.
     */
    public void unbindMulti(Set<String> names) {
        for (String name : names) {
            unbind(name);
        }
    }

    /**
     * Is the given function name defined?
     * 
     * @param name
     *            the name of the function.
     * @return true if the function name is in the hashtable, false otherwise.
     */
    public boolean isDefinedF(String name) {
        return functions.containsKey(name);
    }

    /**
     * Is the given variable name defined?
     * 
     * @param name
     *            the name of the variable.
     * @return true if the variable name is in the hashtable, false otherwise.
     */
    public boolean isDefinedV(String name) {
        return variables.containsKey(name);
    }

    /**
     * Grabs the value of a given variable name.
     * 
     * @param name
     *            the name of the variable.
     * @return the Node value, if the variable was found.
     * @throws EnvironmentException
     *             if the variable was not defined.
     * @see #isDefinedV(String)
     */
    public Node getVariableValue(String name) {
        if (!isDefinedV(name)) {
            throw new EnvironmentException("The variable " + name
                    + " is undefined.");
        }
        return variables.get(name);
    }

    /**
     * Grabs a copy table of the working variables, to avoid corruption
     * 
     * @return a table of String -> Node.
     */
    public Map<String, Node> getVariables() {
        return new HashMap<String, Node>(variables);
    }

    public Map<Node, Function> getLambdas() {
        return new HashMap<>(lambdas);
    }

    /**
     * Sets the variables to the given String -> Node Hashtable.
     * 
     * @param values
     *            a valid Hashtable (String -> Node).
     */
    public void setVariables(Map<String, Node> values) {
        variables = new HashMap<>(values);
    }

    /**
     * Invokes a primitive with a given alias and Node by calling the
     * PrimitiveHandler method.
     * 
     * @param name
     *            the alias of the function
     * @param formals
     *            the Node argument, null for a boolean function
     * @return the Node constructed by invoking the primitive function
     * @see PrimitiveHandler#callFunc(String, Object...)
     */
    public Node invokePrim(String name, Node formals) {
        return handler.callFunc(name, formals);
    }

    /**
     * Grabs the PrimitiveHandler.
     * 
     * @return a PrimitiveHandler object.
     */
    public PrimitiveHandler getHandler() {
        return handler;
    }
}
