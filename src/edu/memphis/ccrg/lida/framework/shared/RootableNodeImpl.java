package edu.memphis.ccrg.lida.framework.shared;


/**
 * Default implementation of {@link RootableNode}.
 * This feature is not fully implemented in this framework version.
 * @author Ryan J. McCall
 */
public class RootableNodeImpl extends NodeImpl implements RootableNode {

	private double desirability;
	private NodeType nodeType;

	@Override
	public double getDesirability() {
		return desirability;
	}

	@Override
	public void setDesirability(double d) {
		if(d <= 0.0){
			desirability = 0.0;
		}else if (d >= 1.0){
			desirability = 1.0;
		}else{
			desirability = d;
		}
	}

	@Override
	public double getNetDesirability() {
		double net = desirability - getActivation();
		if (net<0.0){
			net=0.0;
		}
		return net;
	}
	
	@Override
	public NodeType getNodeType() {
		return nodeType;
	}

	@Override
	public void setNodeType(NodeType t) {
		nodeType = t;
	}
	
	@Override
	public void updateNodeValues(Node n){
		if(n instanceof RootableNode){
			desirability = ((RootableNode) n).getDesirability();
		}
	}
	
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RootableNodeImpl) {
            return getId() == ((RootableNodeImpl) obj).getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId();
    }

	@Override
	public double getTotalDesirability() {
		return getDesirability();
	}
}
