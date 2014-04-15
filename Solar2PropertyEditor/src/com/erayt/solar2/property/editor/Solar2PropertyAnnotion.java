package com.erayt.solar2.property.editor;

import org.eclipse.jface.text.source.Annotation;

public class Solar2PropertyAnnotion extends Annotation {
	private Solar2PropertyProblem problem;

	public Solar2PropertyAnnotion(Solar2PropertyProblem problem) {
		this.problem = problem;
	}

	public String getType() {
		switch (problem.getType()) {
		case INFO:
			return "";
		case ERROR:
			return "com.erayt.solar2.property";
		case WARNING:
			return "";
		default:
			return "";
		}
	}
	
	public String getText(){
		if(this.problem!=null){
			return problem.getMessage();
		}else{
			return super.getText();
		}
	}
	
}
