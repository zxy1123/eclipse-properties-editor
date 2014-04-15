package com.erayt.solar2.property.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.internal.ui.text.CombinedWordRule;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

@SuppressWarnings("restriction")
public class PropertyKeyScanner extends AbstractJavaScanner {
	private String[] NONE = new String[0];
	private IColorManager colorManager;
	private final IToken KEYWORD;

	public PropertyKeyScanner(IColorManager manager, IPreferenceStore store) {
		super(manager, store);
		this.colorManager = manager;
		KEYWORD = new Token(new TextAttribute(colorManager.getColor(new RGB(
				172, 0, 85)), null, SWT.BOLD));
		initialize();
	}

	@Override
	protected String[] getTokenProperties() {
		return NONE;
	}

	@Override
	protected List<IRule> createRules() {
		List<IRule> rules = new ArrayList<IRule>();
		CombinedWordRule wordRule = new CombinedWordRule(
				new Solar2WordDetector(), Token.UNDEFINED);
		CombinedWordRule.WordMatcher keyWordMatcher = new CombinedWordRule.WordMatcher();
		for (String key : Criteria.getInstance().getKeyWords()) {
			keyWordMatcher.addWord(key, KEYWORD);
		}
		wordRule.addWordMatcher(keyWordMatcher);
		rules.add(wordRule);
		return rules;
	}

	class Solar2WordDetector implements IWordDetector {

		@Override
		public boolean isWordStart(char c) {
			return Character.isJavaIdentifierStart(c);
		}

		@Override
		public boolean isWordPart(char c) {
			return !Character.isWhitespace(c);
		}

	}

}
