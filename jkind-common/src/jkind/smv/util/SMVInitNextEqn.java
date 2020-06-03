package jkind.smv.util;

import java.util.ArrayList;
import java.util.List;

import jkind.smv.SMVBinaryExpr;
import jkind.smv.SMVEquation;
import jkind.smv.SMVExpr;
import jkind.smv.SMVIdExpr;
import jkind.smv.SMVModule;
import jkind.smv.SMVProgram;

public class SMVInitNextEqn {
	
	
	public static SMVProgram program(SMVProgram program) {
		List<SMVModule> modules = visitModules(program.modules);
		return new SMVProgram(modules, program.main);
	}

	private static List<SMVModule> visitModules(List<SMVModule> modules) {
		List<SMVModule> sm = new ArrayList<SMVModule>();
		for(SMVModule sMVModule : modules) {
			List<SMVEquation> sMVEquations = new ArrayList<SMVEquation>();
			sMVEquations = visitEquations(sMVModule.equations);
			sm.add(new SMVModule(sMVModule.id, sMVModule.inputs, sMVModule.outputs, 
					sMVModule.locals, sMVEquations, sMVModule.assertions, sMVModule.sMVSpecifications));
		}
		return sm;
	}

	private static List<SMVEquation> visitEquations(List<SMVEquation> equations) {
		List<SMVEquation> sMVEquationss = new ArrayList<SMVEquation>();
		
		for(SMVEquation sMVEquation: equations) {
			sMVEquationss.add(new SMVEquation(sMVEquation.lhs.get(0),fun(sMVEquation.expr)));
		}
		
		List<SMVEquation> sMVEquations = new ArrayList<SMVEquation>();
		for(SMVEquation sMVEquation: sMVEquationss){
			if(sMVEquation.expr instanceof SMVBinaryExpr) {
				if(((SMVBinaryExpr)sMVEquation.expr).op.toString().contains("->")) {
					SMVEquation equationInit = generateInitExpr(sMVEquation.lhs.get(0),((SMVBinaryExpr)sMVEquation.expr).left);
					sMVEquations.add(equationInit);
					SMVEquation equationNext = generateNextExpr(sMVEquation.lhs.get(0),((SMVBinaryExpr)sMVEquation.expr).right);
					sMVEquations.add(equationNext);
				}else {
					sMVEquations.add(sMVEquation);
				}
			}else {
				sMVEquations.add(sMVEquation);
			}
		}
		return sMVEquations;
	}

	private static SMVEquation generateInitExpr(SMVIdExpr smvIdExpr, SMVExpr left) {
		List<SMVIdExpr> lhs = new ArrayList<SMVIdExpr>();
		lhs.add(new SMVIdExpr("init("+smvIdExpr.id+")"));
		return new SMVEquation(lhs,left);
		
		
	}

	private static SMVEquation generateNextExpr(SMVIdExpr smvIdExpr, SMVExpr right) {
		List<SMVIdExpr> lhs = new ArrayList<SMVIdExpr>();
		lhs.add(new SMVIdExpr("next("+smvIdExpr.id+")"));
		return new SMVEquation(lhs,right);
		
	}
	
	public static SMVExpr fun(SMVExpr sMVExpr) {
		if(sMVExpr instanceof SMVIdExpr) {
			return sMVExpr;
		}
		if(sMVExpr instanceof SMVBinaryExpr) {
			if(((SMVBinaryExpr)sMVExpr).left instanceof SMVBinaryExpr){
				SMVBinaryExpr s_left = (SMVBinaryExpr) ((SMVBinaryExpr)sMVExpr).left;
				if(s_left.op.toString().contains("->")) {
					SMVBinaryExpr left = new SMVBinaryExpr(s_left.left, ((SMVBinaryExpr)sMVExpr).op,((SMVBinaryExpr)sMVExpr).right);
					SMVBinaryExpr right = new SMVBinaryExpr(s_left.right, ((SMVBinaryExpr)sMVExpr).op,((SMVBinaryExpr)sMVExpr).right);
					return new SMVBinaryExpr(left,s_left.op,right); 
				}
			}
			if(((SMVBinaryExpr)sMVExpr).right instanceof SMVBinaryExpr){
				SMVBinaryExpr s_right = (SMVBinaryExpr) ((SMVBinaryExpr)sMVExpr).right;
				if(s_right.op.toString().contains("->")) {
					SMVBinaryExpr left = new SMVBinaryExpr(((SMVBinaryExpr)sMVExpr).left, ((SMVBinaryExpr)sMVExpr).op,s_right.left);
					SMVBinaryExpr right = new SMVBinaryExpr(((SMVBinaryExpr)sMVExpr).left, ((SMVBinaryExpr)sMVExpr).op,s_right.right);
					return new SMVBinaryExpr(left,s_right.op,right); 
				}
			}
			
		}else
			return sMVExpr;
		return sMVExpr;
		
	}
	
}
