package org.eclipse.ec4e.services.dom;

import java.io.IOException;
import java.io.StringReader;

import org.eclipse.ec4e.services.model.EditorConfig;
import org.junit.Assert;
import org.junit.Test;

public class EditorConfigTest {

	@Test
	public void load() throws IOException {
		String s = "# EditorConfig is awesome: http://EditorConfig.org\n";
		s+="\n";
		s+="# top-most EditorConfig file\n";
		s+="root = true\n";
		s+="\n";
		s+="# Unix-style newlines with a newline ending every file\n";
		s+="[*]\n";
		s+="end_of_line = lf\n";
		s+="insert_final_newline = true\n\n";
		s+="# Matches multiple files with brace expansion notation\n";
		s+="# Set default charset\n";
		s+="[*.{js,py}]\n";
		s+="charset = utf-8";
		EditorConfig config = EditorConfig.load(new StringReader(s));
		Assert.assertEquals("root=true\n" + 
				"\n" + 
				"[*]\n" + 
				"end_of_line=lf\n" + 
				"insert_final_newline=true\n" + 
				"\n" + 
				"[*.{js,py}]\n" + 
				"charset=utf-8", config.toString());
	}
}
