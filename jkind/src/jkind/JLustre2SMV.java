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
import jkind.smv.builders.SMVProgramBuilder;
import jkind.smv.util.SMVFlattenArrow;
import jkind.smv.util.SMVPreDistribution;
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

			Program program = Main.parseLustre(filename);
			StaticAnalyzer.check(program, SolverOption.Z3, settings);

			program = Translate.translate(program);
			program = RemoveEnumTypes.program(program);
			Node mainNode = program.getMainNode();
			// TODO: We need to think about how readable we want the SMV translation to be.
			/*
			 * At this point the program has the following structure * 1. There are no
			 * constants 2. There are no typedefs 3. There could be functions 4. There is
			 * one node called main.
			 */

			/* to generate a lus file with just one node (main node) */
			// TODO: Think about slicing but I don't think we need this.
			Node main = LustreSlicer.slice(mainNode,
					new DependencyMap(mainNode, mainNode.properties, program.functions));
			if (settings.encode) {
				main = new KindEncodeIdsVisitor().visit(main);
			}
			if (settings.obfuscate) {
				main = new ObfuscateIdsVisitor().visit(main);
				main = new NodeBuilder(main).setId("main").build();
			}
			program = new ProgramBuilder(program).clearNodes().addNode(main).build();

			String outFilenameLus = filename.substring(0, +filename.length() - 4) + "-debug" + ".lus";
			if (settings.stdout) {
				System.out.println(program.toString());
			} else {
				Util.writeToFile(program.toString(), new File(outFilenameLus));
				System.out.println("Wrote " + outFilenameLus);
			}

			/* Translation of the lus program (generated above) into SMV program */
			SMVPreDistribution sfp = new SMVPreDistribution(program);
			Node smvMainNode = sfp.node(program.getMainNode());

			Program programForSMV = new ProgramBuilder().addNode(smvMainNode).build();

			SMVFlattenArrow fa = new SMVFlattenArrow(programForSMV);
			smvMainNode = fa.node(smvMainNode);

			SMVProgram smvp = new SMVProgramBuilder(programForSMV).build();
			SMVModule sMVModule = new SMV_Node2Module_Visitor().visit(smvMainNode);
			smvp = new SMVProgramBuilder(smvp).addModule(sMVModule).build();

			String outFilenameSMV = filename.substring(0, filename.length() - 4) + ".smv";
			Util.writeToFile(smvp.toString(), new File(outFilenameSMV));
			System.out.println("Wrote " + outFilenameSMV);

		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(ExitCodes.UNCAUGHT_EXCEPTION);
		}
	}
}
