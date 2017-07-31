package operators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import util.Helpers;
import util.SelState;
import util.Tuple;
import visitors.JoinExpVisitor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import nio.BinaryTupleReader;
import nio.BinaryTupleWriter;
import nio.FormatConverter;
import nio.TupleReader;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;

public class SortMergeJoinOperator extends JoinOperator {
	int partitionIndex = 0;  // the index of the first tuple in the current partition
	int curRightIndex = 0;  // the index of the current tuple of left table
	Expression exp;  // my expression
	JoinExpVisitor jv;
	
	Tuple leftTp;
	Tuple rightTp;
	
	TupleComp cp = null;
	
	List<Integer> leftOrders = null; // the order of attributes in left table
	List<Integer> rightOrders = null;// the order of attributes in right table
	/**
	 *get the next tuple which satisfied the join condition 
	 */
	@Override
	public Tuple getNextTuple()  {
		while(leftTp !=null && rightTp !=null){
			if (cp.compare(leftTp, rightTp) < 0) {
				leftTp = left.getNextTuple();
				continue;
			}
			
			if (cp.compare(leftTp, rightTp) > 0) {
				rightTp = right.getNextTuple();
				curRightIndex++;
				partitionIndex = curRightIndex;
				continue;
			}			
			Tuple rst = null;
			if(exp == null || 
					Helpers.getJoinRes(leftTp, rightTp, exp, jv)){
				rst = joinTp(leftTp,rightTp);
			}			
			rightTp = right.getNextTuple();
			curRightIndex++;
			
			if (rightTp == null || 
					cp.compare(leftTp, rightTp) != 0) {
				// System.out.println("cur: " + leftTp.toString());
				leftTp = left.getNextTuple();
				// System.out.println(leftTp);
				((SortOperator) right).reset(partitionIndex);
				curRightIndex = partitionIndex;
				rightTp = right.getNextTuple();
			}
			
			if (rst != null) return rst;
		}
		
		return null;
	}
	

	
	public SortMergeJoinOperator(Operator left, 
			Operator right, Expression exp,List<Integer> leftOrders,
			List<Integer> rightOrders) {
		super(left, right, exp);
		this.exp = exp;
		this.leftOrders = leftOrders;
		this.rightOrders = rightOrders;
		cp  = new TupleComp(leftOrders,rightOrders);
		
		
		this.jv = new JoinExpVisitor(left.schema(),right.schema());
		leftTp = left.getNextTuple();
		rightTp = right.getNextTuple();
		
		//System.out.println("I'm in sort merge join");
	}
	
	// comparator class to compare one tuple from the left table and another from the 
	// right table
	public class TupleComp implements Comparator<Tuple>{
		List<Integer> leftOrders = null; // the order of attributes in left table 
		List<Integer> rightOrders = null;// the order of attributes in right table 
		@Override
		public int compare(Tuple left, Tuple right) {
			for( int i = 0; i< leftOrders.size();i++){
				int leftVal = left.cols[leftOrders.get(i)];
				int rightVal = right.cols[rightOrders.get(i)];
				int cmp = Integer.compare(leftVal, rightVal);
				if(cmp != 0) return cmp;		
			}
            
			return 0;
		}
		
		public TupleComp(List<Integer> leftOrders, List<Integer> rightOrders){
				this.leftOrders = leftOrders;
				this.rightOrders = rightOrders;
		}
	}

	@Override
	protected void next() {
		throw new UnsupportedOperationException();
	}
	
    @Override
    public String print() {
        String expression = (exp != null) ? exp.toString() : "";
        return String.format("SMJ[" + expression + "]");
    }
}
