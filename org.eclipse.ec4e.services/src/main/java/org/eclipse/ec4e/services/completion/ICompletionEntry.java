package org.eclipse.ec4e.services.completion;

import org.eclipse.ec4e.services.model.options.ConfigPropertyType;

public interface ICompletionEntry {

	String getName();
	
	void setContextType(CompletionContextType type);

	void setOptionType(ConfigPropertyType<?> optionType);
	
	void setMatcher(ICompletionEntryMatcher matcher);
	
	void setInitialOffset(int offset);

	boolean updatePrefix(String prefix);
}
