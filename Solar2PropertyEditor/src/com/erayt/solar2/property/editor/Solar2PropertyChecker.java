package com.erayt.solar2.property.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;

import com.erayt.solar2.property.editor.Criteria.Info;

public class Solar2PropertyChecker {
	private Map<String, CheckedInfo> checkedKeys = new HashMap<String, CheckedInfo>();

	public class CheckedInfo {
		public int count;
		public Position position;

		public CheckedInfo(int count, Position position) {
			this.count = count;
			this.position = position;
		}
	}

	public void check(IDocument document, ITypedRegion key, ITypedRegion value,
			ProblemCollector collector) {
		try {
			String keyValue = document.get(key.getOffset(), key.getLength());
			String valueValue = document.get(value.getOffset(),
					value.getLength());
			char[] charArray = keyValue.toCharArray();
			int start = 0;

			start = getStart(key, charArray, start);
			int end = getEnd(charArray);
			int keyStart = key.getOffset() + start;
			int keyLength = end - start + 1;

			charArray = valueValue.toCharArray();
			int old = 0;
			for (; old < charArray.length; old++) {
				char c = charArray[old];
				if (c == '=' || c == ':') {
					old++;
					break;
				}
			}

			start = getStart(value, charArray, old);
			if (start >= charArray.length) {
				start = old;
				end = charArray.length - 1;
			} else {
				end = getEnd(charArray);
			}
			int valueStart = value.getOffset() + start;
			int valueLength = end - start + 1;

			keyValue = document.get(keyStart, keyLength);
			valueValue = document.get(valueStart, valueLength);
			Criteria criteria = Criteria.getInstance();
			Info info = criteria.check(keyValue, valueValue);
			if (info == null) {
				return;
			} else if (!info.valid) {
				collector.accept(new Solar2PropertyProblem(valueStart,
						valueLength, info.message));
			} else {
				if (checkedKeys.containsKey(keyValue)) {
					checkedKeys.get(keyValue).count++;
				} else {
					checkedKeys.put(keyValue, new CheckedInfo(1, new Position(
							keyStart, keyLength)));
				}
			}

		} catch (BadLocationException e) {
		}

	}

	private int getEnd(char[] charArray) {
		int end = charArray.length - 1;
		for (; end > 0; end--) {
			if (!Character.isWhitespace(charArray[end])) {
				break;
			}
		}
		return end;
	}

	private int getStart(ITypedRegion key, char[] charArray, int start) {
		for (; start < charArray.length; start++) {
			char c = charArray[start];
			if (!Character.isWhitespace(c)) {
				break;
			}
		}
		return start;
	}

	public void finallyCheck() {

	}
}
