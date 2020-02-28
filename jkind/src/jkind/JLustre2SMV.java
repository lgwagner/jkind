package jkind;

import java.io.File;

import jkind.analysis.StaticAnalyzer;
import jkind.jlustre2kind.KindEncodeIdsVisitor;
import jkind.jlustre2kind.ObfuscateIdsVisitor;
import jkind.lustre.Node;
import jkind.lustre.Program;
import jkind.lustre.builders.NodeBuilder;
import jkind.lustre.builders.ProgramBuilder;
import jkind.slicing.DependencyMap;
import jkind.slicing.LustreSlicer;
import jkind.smv.SMVModule;
import jkind.smv.SMVProgram;
import jkind.smv.builders.SMVModuleBuilder;
import jkind.smv.builders.SMVProgramBuilder;
import jkind.smv.visitors.SMV_Lus2SMV_Visitor;
import jkind.smv.visitors.SMV_Node2Module_Visitor;
import jkind.translation.RemoveEnumTypes;
import jkind.translation.Translate;
import jkind.util.Util;

public class JLustre2SMV {
	public static void main(String args[]) {
		try {
			JLustre2SMVSettings settings = JLustre2SMVArgumentParser.parse(args);
			String filename = settings.filename;

			if (!filename.toLowerCase().endsWith(".lus")) {
				StdErr.error("input file must have .lus extension");
			}
			String outFilename = filename.substring(0, filename.length() - 4) + ".smv";

			Program program = Main.parseLustre(filename);
			StaticAnalyzer.check(program, SolverOption.Z3, settings);
			
			//TODO: We need to think about how readable we want the SMV translation to be.
			program = Translate.translate(program);		
			program = RemoveEnumTypes.program(program);	
			
			/* At this point the program has the following structure *
			 * 
			 * 1. There are no constants
			 * 2. There are no typedefs
			 * 3. There could be functions
			 * 4. There is one node called main.
			 */
			
			//TODO: Think about slicing but I don't think we need this.
			Node main = program.getMainNode();
			main = LustreSlicer.slice(main, new DependencyMap(main, main.properties, program.functions));

			if (settings.encode) {
				main = new KindEncodeIdsVisitor().visit(main);
			}
			
			if (settings.obfuscate) {
				main = new ObfuscateIdsVisitor().visit(main);
				main = new NodeBuilder(main).setId("main").build();
			}
			
			//do our translation to SMV
			//SMVProgram smvp = SMVTranslate.translate(program);
			
			SMVProgram smvp = new SMVProgramBuilder(program).build();
			SMVModule m1 = new SMV_Node2Module_Visitor().visit(main);
			smvp = new SMVProgramBuilder(smvp).addModule(m1).build();
			Util.writeToFile(smvp.toString(), new File(outFilename));
			
//			program = new ProgramBuilder(program).clearNodes().addNode(main).build();
//			if (settings.stdout) {
//				System.out.println(program.toString());
//			} else {
//				Util.writeToFile(program.toString(), new File(outFilename));
//				System.out.println("Wrote " + outFilename);
//			}
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(ExitCodes.UNCAUGHT_EXCEPTION);
		}
	}
}
