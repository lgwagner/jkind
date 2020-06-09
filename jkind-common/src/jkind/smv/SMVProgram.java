package jkind.smv;

import java.util.Arrays;
import java.util.List;

import jkind.smv.visitors.SMVAstVisitor;
import jkind.util.Util;

public class SMVProgram extends SMVAst {

	public final List<SMVFunction> functions;
	public final List<SMVModule> modules;
	public final String main;

	public SMVProgram(List<SMVFunction> functions, List<SMVModule> modules, String main) {
		this.functions = Util.safeList(functions);
		this.modules = Util.safeList(modules);
		if (main == null && modules != null && modules.size() > 0) {
			this.main = modules.get(modules.size() - 1).id;
		} else {
			this.main = main;
		}
	}

	public SMVProgram(SMVModule... modules) {
		this(null, Arrays.asList(modules), null);
	}

	public SMVModule getMainModule() {
		for (SMVModule module : modules) {
			if (module.id.equals(main)) {
				return module;
			}
		}
		return null;
	}

	@Override
	public <T, S extends T> T accept(SMVAstVisitor<T, S> visitor) {
		return visitor.visit(this);
	}
}