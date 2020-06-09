package jkind.smv.visitors;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Function;

import jkind.smv.SMVArrayAccessExpr;
import jkind.smv.SMVArrayExpr;
import jkind.smv.SMVArrayUpdateExpr;
import jkind.smv.SMVBinaryExpr;
import jkind.smv.SMVBoolExpr;
import jkind.smv.SMVCaseExpr;
import jkind.smv.SMVCastExpr;
import jkind.smv.SMVExpr;
import jkind.smv.SMVFunctionCallExpr;
import jkind.smv.SMVIdExpr;
import jkind.smv.SMVIntExpr;
import jkind.smv.SMVModuleCallExpr;
import jkind.smv.SMVRealExpr;
import jkind.smv.SMVUnaryExpr;

public class SMVExprMapVisitor implements SMVExprVisitor<SMVExpr> {

	@Override
	public SMVExpr visit(SMVArrayAccessExpr e) {
		return new SMVArrayAccessExpr(e.array.accept(this), e.index.accept(this));
	}

	@Override
	public SMVExpr visit(SMVArrayExpr e) {
		return new SMVArrayExpr(visitSMVExprs(e.elements));
	}

	@Override
	public SMVExpr visit(SMVArrayUpdateExpr e) {
		return new SMVArrayUpdateExpr(e.array.accept(this), e.index.accept(this), e.value.accept(this));
	}

	@Override
	public SMVExpr visit(SMVBinaryExpr e) {
		SMVExpr left = e.left.accept(this);
		SMVExpr right = e.right.accept(this);
		if (e.left == left && e.right == right) {
			return e;
		}
		return new SMVBinaryExpr(left, e.op, right);
	}

	@Override
	public SMVExpr visit(SMVBoolExpr e) {
		return e;
	}

	@Override
	public SMVExpr visit(SMVCastExpr e) {
		return new SMVCastExpr(e.type, e.expr.accept(this));
	}

	@Override
	public SMVExpr visit(SMVFunctionCallExpr e) {
		return new SMVFunctionCallExpr(e.function, visitSMVExprs(e.args));
	}

	@Override
	public SMVExpr visit(SMVModuleCallExpr e) {
		return new SMVModuleCallExpr(e.module, visitSMVExprs(e.args));
	}

	@Override
	public SMVExpr visit(SMVIdExpr e) {
		return e;
	}

	@Override
	public SMVExpr visit(SMVCaseExpr e) {
		SMVExpr cond = e.cond.accept(this);
		SMVExpr thenExpr = e.thenExpr.accept(this);
		SMVExpr elseExpr = e.elseExpr.accept(this);
		if (e.cond == cond && e.thenExpr == thenExpr && e.elseExpr == elseExpr) {
			return e;
		}
		return new SMVCaseExpr(e.cond, e.elseExpr, e.thenExpr);
	}

	@Override
	public SMVExpr visit(SMVIntExpr e) {
		return e;
	}

	@Override
	public SMVExpr visit(SMVRealExpr e) {
		return e;
	}

	@Override
	public SMVExpr visit(SMVUnaryExpr e) {
		SMVExpr expr = e.expr.accept(this);
		if (e.expr == expr) {
			return e;
		}
		return new SMVUnaryExpr(e.op, expr);
	}

	protected List<SMVExpr> visitSMVExprs(List<? extends SMVExpr> es) {
		return map(e -> e.accept(this), es);
	}

	protected <A, B> List<B> map(Function<? super A, ? extends B> f, List<A> xs) {
		return xs.stream().map(f).collect(toList());
	}

}
