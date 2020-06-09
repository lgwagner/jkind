package jkind.smv.util;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jkind.smv.SMVFunction;
import jkind.smv.SMVModule;
import jkind.smv.SMVType;
import jkind.smv.SMVVarDecl;

public class SMVUtil {

	public static List<SMVVarDecl> getSMVVarDecls(SMVModule module) {
		List<SMVVarDecl> decls = new ArrayList<>();
		decls.addAll(module.inputs);
		decls.addAll(module.outputs);
		decls.addAll(module.locals);
		return decls;
	}
	
	public static Map<String, SMVType> getTypeMap(SMVModule module) {
		return getSMVVarDecls(module).stream().collect(toMap(vd -> vd.id, vd -> vd.type));
	}

	public static List<String> getIds(List<SMVVarDecl> decls) {
		return decls.stream().map(decl -> decl.id).collect(toList());
	}

	public static Map<String, SMVModule> getModuleTable(List<SMVModule> modules) {
		return modules.stream().collect(toMap(n -> n.id, n -> n));
	}

	public static Map<String, SMVFunction> getFunctionTable(List<SMVFunction> functions) {
		return functions.stream().collect(toMap(f -> f.id, f -> f));
	}
	
	
}
