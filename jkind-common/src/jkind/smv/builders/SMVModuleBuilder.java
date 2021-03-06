package jkind.smv.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jkind.smv.SMVEquation;
import jkind.smv.SMVExpr;
import jkind.smv.SMVIdExpr;
import jkind.smv.SMVModule;
import jkind.smv.SMVVarDecl;

public class SMVModuleBuilder {
	private String id;
	private List<SMVVarDecl> inputs = new ArrayList<>();
	private List<SMVVarDecl> outputs = new ArrayList<>();
	private List<SMVVarDecl> locals = new ArrayList<>();
	private List<SMVEquation> equations = new ArrayList<>();
	private List<SMVExpr> assertions = new ArrayList<>();
	private List<String> sMVSpecifications = new ArrayList<>();

	public SMVModuleBuilder(String id) {
		this.id = id;
	}

	public SMVModuleBuilder(SMVModule module) {
		this.id = module.id;
		this.inputs = new ArrayList<>(module.inputs);
		this.outputs = new ArrayList<>(module.outputs);
		this.locals = new ArrayList<>(module.locals);
		this.equations = new ArrayList<>(module.equations);
		this.assertions = new ArrayList<>(module.assertions);
		this.sMVSpecifications = new ArrayList<>(module.sMVSpecifications);

	}

	public SMVModuleBuilder setId(String id) {
		this.id = id;
		return this;
	}

	public SMVModuleBuilder addInput(SMVVarDecl input) {
		this.inputs.add(input);
		return this;
	}

	public SMVModuleBuilder addInputs(Collection<SMVVarDecl> inputs) {
		this.inputs.addAll(inputs);
		return this;
	}

	public SMVModuleBuilder addOutput(SMVVarDecl output) {
		this.inputs.add(output);
		return this;
	}

	public SMVModuleBuilder addOutputs(Collection<SMVVarDecl> outputs) {
		this.inputs.addAll(outputs);
		return this;
	}

	public SMVModuleBuilder addLocals(SMVVarDecl local) {
		this.inputs.add(local);
		return this;
	}
	
	public SMVModuleBuilder addLocals(List<SMVVarDecl> newLocals) {
		this.inputs.addAll(newLocals);
		return this;
		
	}

	public SMVModuleBuilder addAssertion(SMVExpr assertion) {
		this.assertions.add(assertion);
		return this;
	}

	public SMVModuleBuilder addAssertions(Collection<SMVExpr> assertions) {
		this.assertions.addAll(assertions);
		return this;
	}

	public SMVModuleBuilder clearAssertions() {
		this.assertions.clear();
		return this;
	}

	public SMVModuleBuilder addEquation(SMVEquation equation) {
		this.equations.add(equation);
		return this;
	}

	public SMVModuleBuilder addEquation(SMVIdExpr var, SMVExpr expr) {
		this.equations.add(new SMVEquation(var, expr));
		return this;
	}

	public SMVModuleBuilder addEquations(Collection<SMVEquation> equation) {
		this.equations.addAll(equations);
		return this;
	}

	public SMVModuleBuilder addSpecifications(Collection<String> sMVSpecifications) {
		this.sMVSpecifications.addAll(sMVSpecifications);
		return this;
	}

	public SMVModule build() {
		return new SMVModule(id, inputs, outputs, locals, equations, assertions, sMVSpecifications);
	}

	
}
