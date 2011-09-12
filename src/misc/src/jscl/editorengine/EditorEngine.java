package jscl.editorengine;

import jscl.editor.Engine;
import jscl.editor.EngineException;
import bsh.Interpreter;
import bsh.EvalError;

public class EditorEngine extends Engine {
	Interpreter interp=new Interpreter();

	public EditorEngine() throws EngineException {
		try {
			interp.eval("importCommands(\"/jscl/editorengine/commands\");\n"
			           +"mml(x) { return tomathml(x); }\n");
		} catch (EvalError e) {
			throw new EngineException(e);
		}
	}

	public String eval(String str) throws EngineException {
		int n=str.length()-1;
		if(n<0 || "\n".equals(str.substring(n))) {
			exec(str);
			return str;
		} else return eval0(str);
	}

	public void exec(String str) throws EngineException {
		try {
			interp.eval(str);
		} catch (EvalError e) {
			throw new EngineException(e);
		}
	}

	String eval0(String str) throws EngineException {
		try {
			return interp.eval(commands(str)).toString();
		} catch (EvalError e) {
			throw new EngineException(e);
		}
	}

	String commands(String str) {
		return commands(str,false);
	}

	String commands(String str, boolean found) {
		for(int i=0;i<cmds.length;i++) {
			int n=str.length()-cmds[i].length()-1;
			if(n>=0 && (" "+cmds[i].toLowerCase()).equals(str.substring(n))) return commands(str.substring(0,n),true)+"."+cmds[i]+"()";
		}
		str=str.replaceAll("\n","");
		return found?"jscl.math.Expression.valueOf(\""+str+"\")":str;
	}

	static final String cmds[]=new String[] {"expand","factorize","elementary","simplify","numeric","toMathML","toJava"};
}
