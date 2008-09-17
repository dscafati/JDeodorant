package gr.uom.java.ast.decomposition.cfg;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BasicBlockCFG {
	private List<BasicBlock> basicBlocks;
	
	public BasicBlockCFG(CFG cfg) {
		this.basicBlocks = new ArrayList<BasicBlock>();
		for(GraphNode node : cfg.nodes) {
			CFGNode cfgNode = (CFGNode)node;
			if(cfgNode.isLeader()) {
				BasicBlock basicBlock = new BasicBlock(cfgNode);
				if(!basicBlocks.isEmpty()) {
					BasicBlock previousBlock = basicBlocks.get(basicBlocks.size()-1);
					previousBlock.setNextBasicBlock(basicBlock);
					basicBlock.setPreviousBasicBlock(previousBlock);
				}
				basicBlocks.add(basicBlock);
			}
			else {
				BasicBlock basicBlock = basicBlocks.get(basicBlocks.size()-1);
				basicBlock.add(cfgNode);
			}
		}
		BasicBlock.resetBlockNum();
	}

	public List<BasicBlock> getBasicBlocks() {
		return basicBlocks;
	}

	public Set<BasicBlock> forwardReachableBlocks(BasicBlock basicBlock) {
		Set<BasicBlock> reachableBlocks = new LinkedHashSet<BasicBlock>();
		reachableBlocks.add(basicBlock);
		CFGNode lastNode = basicBlock.getLastNode();
		for(GraphEdge edge : lastNode.outgoingEdges) {
			Flow flow = (Flow)edge;
			if(!flow.isLoopbackFlow()) {
				CFGNode dstNode = (CFGNode)flow.dst;
				BasicBlock dstBasicBlock = dstNode.getBasicBlock();
				reachableBlocks.add(dstBasicBlock);
				reachableBlocks.addAll(forwardReachableBlocks(dstBasicBlock));
			}
		}
		return reachableBlocks;
	}
}
