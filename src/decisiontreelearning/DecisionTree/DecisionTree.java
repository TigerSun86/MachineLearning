package decisiontreelearning.DecisionTree;

/**
 * FileName: DecisionTree.java
 * @Description: A tree structure for decision tree learning.
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Feb 25, 2014
 */
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import debug.Dbg;

public class DecisionTree {
    // Root is an attribute or a class.
    private String root;
    private DecisionTree parent;
    private String valueOfParent;

    // The first is a key, "a value of one attribute".
    // The second is a subtree.
    private HashMap<String, DecisionTree> branches;

    public DecisionTree(final String root2) {
        this.root = root2;
        this.parent = null; // Default parent is null. Call setParent() if
                            // needed.
        this.valueOfParent = null;
        this.branches = new HashMap<String, DecisionTree>();
    }

    public final String getRoot () {
        return root;
    }

    public final void setRoot (final String root2) {
        this.root = root2;
    }

    public final DecisionTree getParent () {
        return parent;
    }

    public final boolean hasParent () {
        return parent != null;
    }

    public final void setParent (final DecisionTree parent2) {
        this.parent = parent2;
    }

    public String getValueOfParent () {
        return valueOfParent;
    }

    public void setValueOfParent (final String valueOfParent2) {
        this.valueOfParent = valueOfParent2;
    }

    public final boolean isLeaf () {
        return branches.isEmpty();
    }

    public final Set<Entry<String, DecisionTree>> branchSet () {
        return branches.entrySet();
    }

    /**
     * Here "value" means "the value of the attribute", but not the HashMap
     * value."
     */
    public final Set<String> valueSet () {
        return branches.keySet();
    }

    public final Collection<DecisionTree> subTreeSet () {
        return branches.values();
    }

    /**
     * Here "value" means "the value of the attribute", but not the HashMap
     * value."
     */
    public final DecisionTree getSubTree (final String value) {
        return branches.get(value);
    }

    /**
     * Here "value" means "the value of the attribute", but not the HashMap
     * value."
     */
    public final void
            addBranch (final String value, final DecisionTree subTree) {
        branches.put(value, subTree);
    }

    @Override
    public final String toString () {
        return print("", true);
    }

    private String print (String prefix, boolean isTail) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix + (isTail ? "+-- " : "+-- ") + root + Dbg.NEW_LINE);
        final Collection<Entry<String, DecisionTree>> branches = branchSet();
        int counter = 0;
        for (Entry<String, DecisionTree> childBranch : branches) {
            sb.append(prefix + (isTail ? "    " : "|   ") + "|"
                    + childBranch.getKey() + Dbg.NEW_LINE);
            counter++;
            if (counter < branches.size()) {
                sb.append(childBranch.getValue().print(
                        prefix + (isTail ? "    " : "|   "), false));
            } else { // Last child.
                sb.append(childBranch.getValue().print(
                        prefix + (isTail ? "    " : "|   "), true));
            }
        }
        return sb.toString();
    }
}
