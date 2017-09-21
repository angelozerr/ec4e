package org.eclipse.ec4e.services.completion;

public class CompletionContext {
	public final String prefix;
	public final String name; // option name, only available when context type is an option value
	public final CompletionContextType type;

	public CompletionContext(String prefix, String name, CompletionContextType type) {
		this.prefix = prefix;
		this.name = name;
		this.type = type;
	}

	public CompletionContext(String prefix, CompletionContextType type) {
		this(prefix, null, type);
	}
}
