package jkind.smv.visitors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jkind.smv.SMVArrayAccessExpr;
import jkind.smv.SMVArrayExpr;
import jkind.smv.SMVArrayType;
import jkind.smv.SMVArrayUpdateExpr;
import jkind.smv.SMVBinaryExpr;
import jkind.smv.SMVBoolExpr;
import jkind.smv.SMVCaseExpr;
import jkind.smv.SMVCastExpr;
import jkind.smv.SMVEnumType;
import jkind.smv.SMVFunction;
import jkind.smv.SMVFunctionCallExpr;
import jkind.smv.SMVIdExpr;
import jkind.smv.SMVIntExpr;
import jkind.smv.SMVModule;
import jkind.smv.SMVModuleCallExpr;
import jkind.smv.SMVNamedType;
import jkind.smv.SMVProgram;
import jkind.smv.SMVRealExpr;
import jkind.smv.SMVSubrangeIntType;
import jkind.smv.SMVType;
import jkind.smv.SMVUnaryExpr;
import jkind.smv.SMVVarDecl;
import jkind.smv.util.SMVUtil;

/**
 * Assuming everything is well-typed, this class can be used to quickly
 * reconstruct the type of an expression (often useful during translations).
 * This class treats subrange and enumeration types as integer types. It has
 * been assumed that SMV module and SMV function cannot be declared in such way
 * that depicts tuple return like in Lustre node and Lustre function.
 */

public class SMVTypeReconstructor implements SMVExprVisitor<SMVType> {
	private final Map<String, SMVType> typeTable = new HashMap<>();
	private final Map<String, SMVEnumType> enumValueTable = new HashMap<>();
	private final Map<String, SMVType> variableTable = new HashMap<>();
	private final Map<String, SMVModule> moduleTable = new HashMap<>();
	private final Map<String, SMVFunction> functionTable = new HashMap<>();
	private final boolean enumsAsInts;

	public SMVTypeReconstructor(SMVProgram program) {
		this(program, true);
	}

	public SMVTypeReconstructor(SMVProgram program, boolean enumsAsInts) {
		this.enumsAsInts = enumsAsInts;
		functionTable.putAll(SMVUtil.getFunctionTable(program.functions));
		moduleTable.putAll(SMVUtil.getModuleTable(program.modules));
	}

	public void setModuleContext(SMVModule e) {
		variableTable.clear();
		SMVUtil.getSMVVarDecls(e).forEach(this::addVariable);
	}

	public void addVariable(SMVVarDecl varDecl) {
		variableTable.put(varDecl.id, resolveType(varDecl.type));
	}

	@Override
	public SMVType visit(SMVArrayAccessExpr e) {
		SMVArrayType array = (SMVArrayType) e.array.accept(this);
		return array.base;
	}

	@Override
	public SMVType visit(SMVArrayExpr e) {
		return new SMVArrayType(e.elements.get(0).accept(this), e.elements.size());
	}

	@Override
	public SMVType visit(SMVArrayUpdateExpr e) {
		return e.array.accept(this);
	}

	@Override
	public SMVType visit(SMVBinaryExpr e) {
		switch (e.op) {
		case PLUS:
		case MINUS:
		case MULTIPLY:
			return e.left.accept(this);

		case SMVDIV:
		case DIVIDE:
			return SMVNamedType.REAL;

		case INT_DIVIDE:
		case MODULUS:
			return SMVNamedType.INT;

		case EQUAL:
		case SMVNOTEQUAL:
		case GREATER:
		case LESS:
		case GREATEREQUAL:
		case LESSEQUAL:
		case SMVOR:
		case SMVAND:
		case XOR:
		case IMPLIES:
			return SMVNamedType.BOOL;

		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public SMVType visit(SMVBoolExpr e) {
		return SMVNamedType.BOOL;
	}

	@Override
	public SMVType visit(SMVCastExpr e) {
		return e.type;
	}

	@Override
	public SMVType visit(SMVIdExpr e) {
		if (variableTable.containsKey(e.id)) {
			return variableTable.get(e.id);
		} else if (enumValueTable.containsKey(e.id)) {
			return enumsAsInts ? SMVNamedType.INT : enumValueTable.get(e.id);
		} else {
			throw new IllegalArgumentException("Unknown variable: " + e.id);
		}
	}

	@Override
	public SMVType visit(SMVCaseExpr e) {
		return e.thenExpr.accept(this);
	}

	@Override
	public SMVType visit(SMVIntExpr e) {
		return SMVNamedType.INT;
	}

	@Override
	public SMVType visit(SMVModuleCallExpr e) {
		SMVModule module = moduleTable.get(e.module);
		return visitCallOutputs(module.outputs);
	}

	@Override
	public SMVType visit(SMVFunctionCallExpr e) {
		SMVFunction function = functionTable.get(e.function);
		return visitCallOutputs(function.outputs);
	}

	private SMVType visitCallOutputs(List<SMVVarDecl> outputDecls) {
		SMVVarDecl output = outputDecls.get(0);
		return resolveType((output.type));
	}

	@Override
	public SMVType visit(SMVRealExpr e) {
		return SMVNamedType.REAL;
	}

	@Override
	public SMVType visit(SMVUnaryExpr e) {
		switch (e.op) {
		case SMVNEGATIVE:
			return e.expr.accept(this);

		case SMVNOT:
			return SMVNamedType.BOOL;

		default:
			throw new IllegalArgumentException();
		}
	}

	private SMVType resolveType(SMVType type) {
		return type.accept(new SMVTypeMapVisitor() {
			@Override
			public SMVType visit(SMVSubrangeIntType e) {
				return SMVNamedType.INT;
			}

			@Override
			public SMVType visit(SMVEnumType e) {
				return enumsAsInts ? SMVNamedType.INT : e;
			}

			@Override
			public SMVType visit(SMVNamedType e) {
				if (e.isBuiltin()) {
					return e;
				} else {
					return typeTable.get(e.name);
				}
			}
		});
	}

}