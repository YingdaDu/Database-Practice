package operators;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The BinaryOperator abstract class represents operators in the query
 * plan which have left and right child.

 *
 */
public abstract class BinaryOperator extends Operator {
	public Operator left, right; // the binary operator has left and right child.
	
	/**
	 * Binary operator needs to reset both children.
	 */
	public void reset() {
		left.reset();
		right.reset();
	}
	
	/**
	 * Every binary operator generates a concatenated 
	 * new schema.
	 */
	public List<String> schema() {
		return schema;
	}
	
	/**
	 * Construct a binary operator.
	 * @param left left child
	 * @param right right child
	 */
	public BinaryOperator(Operator left, Operator right) {
		this.left = left; this.right = right;
		schema = new ArrayList<String>(left.schema());
		schema.addAll(right.schema());
	}
	
	@Override
	public void printTree(PrintStream ps, int lv) {
		printIndent(ps, lv);
		ps.println(print());
		left.printTree(ps, lv + 1);
		right.printTree(ps, lv + 1);
	}
}
