package btree;

import java.util.ArrayList;
import java.util.List;
/**
 * Data entry is one element inside the leaf node,
 * and it contains a key and a list of record ids. 
 * its format: <key, [<pid,tid>, <pid,tid>, ...]> 
 *
 */
public class DataEntry implements Comparable<DataEntry>{
	int key;
	ArrayList<Rid> rids;
/*
 * constructor;	
 */
	public DataEntry(int key, List<Rid> rids){
		this.key = key;
		this.rids = new ArrayList<Rid>(rids);
	}
	
	/**
	 * Compares to another DataEntry.
	 */
	@Override
	public int compareTo(DataEntry that) {
		if (this.key > that.key) return 1;
		else if (this.key < that.key) return -1; 
		else return 0;
	}
	
	/**
	 * Returns the string representation of the data entry.
	 */
	@Override 
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<[" + key +":");
		for (Rid rid : rids) {
			sb.append(rid.toString());
		}
		sb.append("]>");
		return sb.toString();
	}
	
	
}
