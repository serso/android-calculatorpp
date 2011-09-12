package jscl.editorengine;

import jscl.editor.Engine;
import jscl.editor.EngineFactory;
import jscl.editor.EngineException;

public class EditorEngineFactory extends EngineFactory {
	public Engine getEngine() throws EngineException {
		return new EditorEngine();
	}
}
