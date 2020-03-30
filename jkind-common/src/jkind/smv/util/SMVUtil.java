package jkind.smv.util;

import java.util.HashSet;
import java.util.Set;

import jkind.JKindException;

public class SMVUtil {

	private static Set<String> hash_Set = new HashSet<String>();

	public static boolean isKeyword(String str) {
		hash_Set.add("@F~");
		hash_Set.add("@O~");
		hash_Set.add("A");
		hash_Set.add("ABF");
		hash_Set.add("abs");
		hash_Set.add("AF");
		hash_Set.add("AG");
		hash_Set.add("array");
		hash_Set.add("ASSIGN");
		hash_Set.add("at next");
		hash_Set.add("at last");
		hash_Set.add("AX");
		hash_Set.add("bool");
		hash_Set.add("boolean");
		hash_Set.add("BU");
		hash_Set.add("case");
		hash_Set.add("Clock");
		hash_Set.add("clock");
		hash_Set.add("COMPASSION");
		hash_Set.add("COMPID");
		hash_Set.add("COMPUTE");
		hash_Set.add("COMPWFF");
		hash_Set.add("CONSTANTS");
		hash_Set.add("CONSTARRAY");
		hash_Set.add("CONSTRAINT");
		hash_Set.add("cos");
		hash_Set.add("count");
		hash_Set.add("CTLSPEC");
		hash_Set.add("CTLWFF");
		hash_Set.add("DEFINE");
		hash_Set.add("E");
		hash_Set.add("EBF");
		hash_Set.add("EBG");
		hash_Set.add("EF");
		hash_Set.add("EG");
		hash_Set.add("esac");
		hash_Set.add("EX");
		hash_Set.add("exp");
		hash_Set.add("extend");
		hash_Set.add("F");
		hash_Set.add("FAIRNESS");
		hash_Set.add("FALSE");
		hash_Set.add("floor");
		hash_Set.add("FROZENVER");
		hash_Set.add("FUN");
		hash_Set.add("G");
		hash_Set.add("H");
		hash_Set.add("IN");
		hash_Set.add("in");
		hash_Set.add("INIT");
		hash_Set.add("init");
		hash_Set.add("Integer");
		hash_Set.add("integer");
		hash_Set.add("INVAR");
		hash_Set.add("INVARSPEC");
		hash_Set.add("ISA");
		hash_Set.add("ITYPE");
		hash_Set.add("IVAR");
		hash_Set.add("JUSTICE");
		hash_Set.add("ln");
		hash_Set.add("LTLSPEC");
		hash_Set.add("LTLWFF");
		hash_Set.add("MAX");
		hash_Set.add("max");
		hash_Set.add("MDEFINE");
		hash_Set.add("MIN");
		hash_Set.add("min");
		hash_Set.add("MIRROR");
		hash_Set.add("mod");
		hash_Set.add("MODULE");
		hash_Set.add("NAME");
		hash_Set.add("next");
		hash_Set.add("NEXTWFF");
		hash_Set.add("noncontinuous");
		hash_Set.add("self");
		hash_Set.add("signed");
		hash_Set.add("STMPWFF");
		hash_Set.add("sin");
		hash_Set.add("sizeof");
		hash_Set.add("SPEC");
		hash_Set.add("swconst");
		hash_Set.add("T");
		hash_Set.add("tan");
		hash_Set.add("time");
		hash_Set.add("time_since");
		hash_Set.add("time_until");
		hash_Set.add("toint");
		hash_Set.add("TRANS");
		hash_Set.add("TRUE");
		hash_Set.add("typeof");
		hash_Set.add("U");
		hash_Set.add("union");
		hash_Set.add("unsigned");
		hash_Set.add("URGENT");
		hash_Set.add("uwconst");
		hash_Set.add("V");
		hash_Set.add("VALID");
		hash_Set.add("VAR");
		hash_Set.add("Word");
		hash_Set.add("word");
		hash_Set.add("word1");
		hash_Set.add("WRITE");
		hash_Set.add("X");
		hash_Set.add("xnor");
		hash_Set.add("xor");
		hash_Set.add("X~Y");
		hash_Set.add("Y~");
		hash_Set.add("Z");
		
		if(hash_Set.contains(str.toString())) {
			return true;
			//throw new JKindException("'"+str+"'"+" is a SMV keyword");
		}
		return false;
	}
	
	
	public static String replaceIllegalStr(String id) {
		String str = id.replaceAll("~", "_");
		str = str.replaceAll("\\.", "_");
		str = str.replaceAll("true", "TRUE");
		str = str.replaceAll("false", "FALSE");
		if(isKeyword(str)) {
			str = id.replaceAll(id, id+"_new");
		}
		return str;
	}
	
	public static void isValid() {
		
	}
}
