package jkind.smv.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jkind.lustre.Program;
import jkind.smv.SMVModule;
import jkind.smv.SMVProgram;


public class SMVProgramBuilder {

	
	private List<SMVModule> modules = new ArrayList<>();
	
	private String main;

	public SMVProgramBuilder() {
	}
	
	public SMVProgramBuilder(Program program) {
		// TODO Auto-generated constructor stub
		this.main = program.main;
	}
	
	public SMVProgramBuilder(SMVProgram program) {
		// TODO Auto-generated constructor stub
		this.modules = new ArrayList<>(program.modules);
		this.main = program.main;
	}

	public SMVProgramBuilder setMain(String main) {
		this.main = main;
		return this;
	}
	
	public SMVProgramBuilder addModule(SMVModule module) {
		this.modules.add(module);
		return this;
	}
	
	public SMVProgramBuilder addModules(Collection<SMVModule> modules) {
		this.modules.addAll(modules);
		return this;
	}

	public SMVProgramBuilder clearModules() {
		this.modules.clear();
		return this;
	}
	
	public SMVProgram build() {
		return new SMVProgram(modules, main);
	}
}

