package jkind;

import java.io.File;

import jkind.analysis.StaticAnalyzer;
import jkind.jlustre2kind.KindEncodeIdsVisitor;
import jkind.jlustre2kind.ObfuscateIdsVisitor;
import jkind.lustre.Node;
import jkind.lustre.Program;
import jkind.lustre.builders.NodeBuilder;
import jkind.lustre.builders.ProgramBuilder;
import jkind.smv.SMVProgram;
import jkind.smv.util.PreDistribution;
import jkind.smv.util.SMVFlattenArrow;
import jkind.smv.util.SMVRemoveArrow;
import jkind.smv.util.SMVRemovePre;
import jkind.smv.visitors.SMVLustre2SMVVisitor;
import jkind.translation.FlattenPres;
import jkind.translation.InlineConstants;
import jkind.translation.InlineEnumValues;
import jkind.translation.InlineNodeCalls;
import jkind.translation.InlineUserTypes;
import jkind.translation.RemoveCondacts;
import jkind.translation.RemoveEnumTypes;
import jkind.translation.compound.FlattenCompoundTypes;
import jkind.util.Util;

public class JLustre2SMV {
	public static void main(String args[]) {
		try {
			JLustre2SMVSettings settings = JLustre2SMVArgumentParser.parse(args);
			String filename = settings.filename;
			if (!filename.toLowerCase().endsWith(".lus")) {
				StdErr.error("input file must have .lus extension");
			}

			String base = filename.substring(0, +filename.length() - 4);
			String debugFileName = base + "_debug.lus";
			String outFileName = base + ".smv";

			Program program = Main.parseLustre(filename);
			StaticAnalyzer.check(program, SolverOption.Z3, settings);

			program = InlineEnumValues.program(program);
			program = InlineUserTypes.program(program);
			program = InlineConstants.program(program);
			program = RemoveCondacts.program(program);
			program = InlineNodeCalls.program(program);
			program = FlattenCompoundTypes.program(program);
			program = FlattenPres.program(program);
			program = RemoveEnumTypes.program(program);
			Node main = program.getMainNode();
			// TODO: We need to think about how readable we want the SMV translation to be.
			/*
			 * At this point the program has the following structure 
			 * 1. There are no constants 
			 * 2. There are no typedefs 
			 * 3. There could be functions 
			 * 4. There is one node called main.
			 * 5. Pres are not nested.
			 * 6. Arrows are not nested.
			 * 7. No contract, 
			 * 8. No array, condact, record, or tuple expressions.
			 * 9. No equation which has two variables on left side 
			 * e.g. hold, state = testgen(reset, trueCond, falseCond)
			 */


			// TODO: Think about slicing but I don't think we need this.
			//Node main = LustreSlicer.slice(mainNode,
			//		new DependencyMap(mainNode, mainNode.properties, program.functions));
			if (settings.encode) {
				main = new KindEncodeIdsVisitor().visit(main);
			}
			if (settings.obfuscate) {
				main = new ObfuscateIdsVisitor().visit(main);
				main = new NodeBuilder(main).setId("main").build();
			}
			program = new ProgramBuilder(program).clearNodes().addNode(main).build();

			if (settings.debug) {
				if (settings.stdout) {
					System.out.println(program.toString());
				}else {
					Util.writeToFile(program.toString(), new File(debugFileName));
					System.out.println("Wrote " + debugFileName);
				}
			}
			/*Next four transformations generate the programs which might have constructs from Lustre and SMV both.
			 * However, still extension will be ".lus".*/
			
			/* Distributing pre, e.g., "pre(a and b)" get replaced with "pre a and pre b" */
			program = PreDistribution.program(program);
			/*
			 * Removing pre, e.g., "x = pre a" get replaced with "x = _pre_a" and
			 * "next(_pre_a) = a"
			 */
			program = SMVRemovePre.program(program);
			/*
			 * Flattening arrow, e.g., "y = (a -> b) -> c" get replaced with
			 * "y = _smashed1 -> c" and "_smashed1 = a -> b "
			 */
			program = SMVFlattenArrow.program(program);

			/*
			 * Removing arrow from a Lustre file (replacing arrow expression with ternary expression).
			 * However, ternary expression does not belong to SMV AST. Need to think whether
			 * ternary expr has to be included in SMV AST.
			 */
			/*
			 * Removing arrow from a Lustre file (replacing arrow expression with CASE expression)
			 * , e.g., "y = a -> b" get replaced with
			 * "y = case
			 * 			initState : a; 
			 * 			TRUE      : b;
			 *      esac;" 
			 * where initState = true means first state and initState = false means all subsequent steps
			 *
			 */
			program = SMVRemoveArrow.program(program);

			/* translate to SMV */

			SMVProgram smvprogram = SMVLustre2SMVVisitor.program(program);

			if (settings.stdout) {
				System.out.println(program.toString());
			} else {
				Util.writeToFile(smvprogram.toString(), new File(outFileName));
				System.out.println("Wrote " + outFileName);
			}

		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(ExitCodes.UNCAUGHT_EXCEPTION);
		}
	}
}
