package com.erayt.solar2.property.editor;

public class Solar2PropertyProblem {

	private int start;
	private ProblemType type;
	private String message;
	private int end;

	public Solar2PropertyProblem(int start, int end, String message) {
		this(start, end, ProblemType.ERROR, message);
	}

	public Solar2PropertyProblem(int start, int end, ProblemType type,
			String message) {
		this.start = start;
		this.end = end;
		this.type = type;
		this.message = message;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String getMessage() {
		return message;
	}

	public ProblemType getType() {
		return type;
	}

	public enum ProblemType {
		INFO, ERROR, WARNING;
	}
}