package gr.uom.java.ast.decomposition.cfg;

import gr.uom.java.ast.MethodObject;

public class PDGMethodEntryNode extends PDGNode {
	private MethodObject method;
	
	public PDGMethodEntryNode(MethodObject method) {
		super();
		this.method = method;
		this.id = 0;
	}

	public BasicBlock getBasicBlock() {
		return null;
	}

	public boolean equals(Object o) {
		if(this == o)
    		return true;
    	
    	if(o instanceof PDGMethodEntryNode) {
    		PDGMethodEntryNode pdgNode = (PDGMethodEntryNode)o;
    		return this.method.equals(pdgNode.method);
    	}
    	return false;
	}

	public int hashCode() {
		return method.hashCode();
	}

	public String toString() {
		return id + "\t" + method.getName();
	}
}
