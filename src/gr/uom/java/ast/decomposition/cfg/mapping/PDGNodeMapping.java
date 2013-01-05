package gr.uom.java.ast.decomposition.cfg.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gr.uom.java.ast.decomposition.AbstractStatement;
import gr.uom.java.ast.decomposition.cfg.AbstractVariable;
import gr.uom.java.ast.decomposition.cfg.CompositeVariable;
import gr.uom.java.ast.decomposition.cfg.PDGNode;

public class PDGNodeMapping {
	private PDGNode nodeG1;
	private PDGNode nodeG2;
	private List<Replacement> replacements;
	private boolean validMatch;
	private volatile int hashCode = 0;
	
	public PDGNodeMapping(PDGNode nodeG1, PDGNode nodeG2) {
		this.nodeG1 = nodeG1;
		this.nodeG2 = nodeG2;
		this.replacements = new ArrayList<Replacement>();
		
		AbstractStatement s1 = nodeG1.getStatement();
		AbstractStatement s2 = nodeG2.getStatement();
		if(!s1.toString().equals(s2.toString())) {
			List<Replacement> replacements = s1.findReplacements(s2);
			Collections.sort(replacements);
			String tempS1 = s1.toString();
			int replacementLengthDiff = 0;
			for(Replacement replacement : replacements) {
				String start = tempS1.substring(0, replacement.getStartPosition1() + replacementLengthDiff);
				String middle = replacement.getValue2();
				String end = s1.toString().substring(replacement.getStartPosition1() + replacement.getLength1(), s1.toString().length());
				tempS1 = start + middle + end;
				replacementLengthDiff += replacement.getLength2() - replacement.getLength1();
			}
			if(tempS1.equals(s2.toString())) {
				this.replacements.addAll(replacements);
				validMatch = true;
			}
		}
		else {
			validMatch = true;
		}
	}
	
	public PDGNode getNodeG1() {
		return nodeG1;
	}

	public PDGNode getNodeG2() {
		return nodeG2;
	}

	public List<Replacement> getReplacements() {
		return replacements;
	}

	public boolean isValidMatch() {
		return validMatch;
	}

	public boolean matchingVariableReplacement(AbstractVariable variable1, AbstractVariable variable2) {
		if(variable1.getClass() == variable2.getClass()) {
			String rightPartVariable1 = null;
			String rightPartVariable2 = null;
			if(variable1 instanceof CompositeVariable) {
				CompositeVariable comp1 = (CompositeVariable)variable1;
				CompositeVariable comp2 = (CompositeVariable)variable2;
				rightPartVariable1 = comp1.getRightPart().toString();
				rightPartVariable2 = comp2.getRightPart().toString();
			}
			boolean equalRightPart = false;
			if(rightPartVariable1 != null && rightPartVariable2 != null) {
				equalRightPart = rightPartVariable1.equals(rightPartVariable2);
			}
			else {
				equalRightPart = true;
			}
			for(Replacement replacement : replacements) {
				if(replacement instanceof FieldInstructionReplacement) {
					if(variable1.isField() && variable2.isField() && equalRightPart &&
							replacement.getValue1().equals(variable1.getVariableName()) &&
							replacement.getValue2().equals(variable2.getVariableName())) {
						return true;
					}
				}
				else if(replacement instanceof VariableDeclarationReplacement || replacement instanceof VariableInstructionReplacement) {
					if(!variable1.isField() && !variable2.isField() && equalRightPart &&
							replacement.getValue1().equals(variable1.getVariableName()) &&
							replacement.getValue2().equals(variable2.getVariableName())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o instanceof PDGNodeMapping) {
			PDGNodeMapping mapping = (PDGNodeMapping)o;
			return this.nodeG1.equals(mapping.nodeG1) &&
					this.nodeG2.equals(mapping.nodeG2);
		}
		return false;
	}

	public int hashCode() {
		if(hashCode == 0) {
			int result = 17;
			result = 37*result + nodeG1.hashCode();
			result = 37*result + nodeG2.hashCode();
			hashCode = result;
		}
		return hashCode;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(nodeG1);
		sb.append(nodeG2);
		sb.append(replacements);
		return sb.toString();
	}
}
