package com.erayt.solar2.property.editor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.erayt.solar2.criteriaResolve.CriteriaResolver;

public class Criteria {
	private Map<String, Rule> rules;
	private static volatile boolean initialized = false;
	private static final Info DEFAULT_INFO = new Info(true);
	private static Lock lock = new ReentrantLock();
	private static Criteria INSTANCE;
	public static final String ERRORNULL = "can not be null";

	public static Criteria getInstance() {
		if (!initialized) {
			try {

				{
					lock.lock();
					INSTANCE = new Criteria();
					initialized = true;
				}
			} finally {
				lock.unlock();
			}
		}
		return INSTANCE;
	}

	private Criteria() {
		try {
			init();
		} catch (WrongPropertyExcetion e) {
			MessageDialog.openError(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), "Can not parse",
					e.getMessage());
		}
	}

	public Info check(String key, String value) {
		if (rules.containsKey(key)) {
			Rule rule = rules.get(key);
			if (value.isEmpty() && rule.require) {
				return new Info(false, ERRORNULL);
			}
			if (rule.rule.matcher(value).matches()) {
				return DEFAULT_INFO;
			} else {
				return new Info(false, rule.errorMsg);
			}
		}
		return null;
	}

	public String getInfoByKey(String key) {
		if (rules.containsKey(key)) {
			return rules.get(key).textInfo;
		}
		return null;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void init() throws WrongPropertyExcetion {
		rules = CriteriaResolver.getRules();
	}

	public String[] getKeyWords() {
		Set<String> keySet = getInstance().rules.keySet();
		return keySet.toArray(new String[keySet.size()]);
	}

	public static class Rule {
		private final String key;
		private final Pattern rule;
		private final String errorMsg;
		private final boolean require;
		private final String textInfo;
		private static String[] keys = new String[] { "key", "require",
				"errorMsg", "rule", "textInfo" };

		public Rule(String key, Pattern rule, String textInfo, String errorMsg,
				boolean require) {
			this.key = key;
			this.require = require;
			this.errorMsg = errorMsg;
			this.textInfo = textInfo;
			this.rule = rule;
		}

		public Rule(Map<String, Object> map) throws WrongPropertyExcetion {
			if (map.size() != 5) {
				throw new WrongPropertyExcetion();
			}
			for (String key : keys) {
				if (!map.containsKey(key)) {
					throw new WrongPropertyExcetion();
				}
			}
			this.key = (String) map.get("key");
			this.require = (Boolean) map.get("require");
			this.errorMsg = (String) map.get("errorMsg");
			this.textInfo = (String) map.get("textInfo");
			this.rule = (Pattern) map.get("rule");
		}
	}

	public static class Info {
		public boolean valid;
		public String message;

		public Info(boolean valid) {
			this.valid = valid;
		}

		public Info(boolean valid, String message) {
			this.valid = valid;
			this.message = message;
		}
	}

	public static class WrongPropertyExcetion extends Exception {
		public WrongPropertyExcetion() {
			super("proerty must be key ,require,errorMsg,textInfo,rule");
		}

		public WrongPropertyExcetion(Throwable e) {
			super(e);
		}
	}
}
