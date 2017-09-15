package org.eclipse.ec4e.services.dom;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ec4e.services.parser.EditorConfigParser;

public class EditorConfig {

	private Boolean root;

	private final List<Section> sections;

	public EditorConfig() {
		this.sections = new ArrayList<>();
	}

	public static EditorConfig load(Reader reader) throws IOException {
		EditorConfigHandler handler = new EditorConfigHandler();
		new EditorConfigParser<Section, Option>(handler).parse(reader);
		return handler.getEditorConfig();
	}

	public void addSection(Section section) {
		sections.add(section);
	}

	public List<Section> getSections() {
		return sections;
	}

	public Boolean getRoot() {
		return root;
	}

	public void setRoot(Boolean root) {
		this.root = root;
	}

	public boolean isRoot() {
		return root != null && root;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		if (getRoot() != null) {
			s.append("root=");
			s.append(isRoot());
			s.append("\n\n");
		}
		int i = 0;
		for (Section section : sections) {
			if (i > 0) {
				s.append("\n\n");
			}
			s.append(section.toString());
			i++;
		}
		return s.toString();
	}

}
