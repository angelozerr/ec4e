package org.eclipse.ec4e.services.handlers;

import org.eclipse.ec4e.services.parser.EditorConfigParser;
import org.eclipse.ec4e.services.parser.ParseException;

public interface IEditorConfigHandler<S, O> {

	public void setParser(EditorConfigParser editorConfigParser);
	
	public S startSection();

	public void endSection(S section);

	public void startMultiPatternSection(S section);

	public void endMultiPatternSection(S section);

	public void startPattern(S section, int i);

	public void endPattern(S section, String pattern, int i);

	public O startOption();

	public void endOption(O option);

	public void startOptionName(O option);

	public void endOptionName(O option, String name);

	public void startOptionValue(Object option, String name);

	public void endOptionValue(Object option, String value, String name);

	public void error(ParseException e);

}