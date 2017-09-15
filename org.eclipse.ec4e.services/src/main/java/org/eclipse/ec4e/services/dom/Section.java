package org.eclipse.ec4e.services.dom;

import java.util.ArrayList;
import java.util.List;

public class Section {

	private final List<String> patterns;
	private final List<Option> options;

	public Section() {
		this.patterns = new ArrayList<>();
		this.options = new ArrayList<>();
	}

	public void addOption(Option option) {
		options.add(option);
	}

	public List<Option> getOptions() {
		return options;
	}

	public void addPattern(String pattern) {
		patterns.add(pattern);
	}

	public List<String> getPatterns() {
		return patterns;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		// patterns
		List<String> patterns = this.getPatterns();
		if (!patterns.isEmpty()) {
			s.append("[");
			if (patterns.size() == 1) {
				s.append(patterns.get(0));
			} else {
				int i = 0;
				for (String pattern : patterns) {
					if (i > 0) {
						s.append(",");
					}
					s.append(pattern);
					i++;
				}
			}
			s.append("]\n");
		}
		// options
		int i = 0;
		for (Option option : this.getOptions()) {
			if (i > 0) {
				s.append("\n");
			}
			s.append(option.toString());
			i++;
		}
		return s.toString();
	}

}
