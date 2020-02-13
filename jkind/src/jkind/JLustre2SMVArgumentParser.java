package jkind;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class JLustre2SMVArgumentParser extends ArgumentParser {
	private static final String ENCODE = "encode";
	private static final String OBFUSCATE = "obfuscate";
	private static final String STDOUT = "stdout";

	private final JLustre2SMVSettings settings;

	private JLustre2SMVArgumentParser() {
		this("JLustre2SMV", new JLustre2SMVSettings());
	}

	private JLustre2SMVArgumentParser(String name, JLustre2SMVSettings settings) {
		super(name, settings);
		this.settings = settings;
	}

	@Override
	protected Options getOptions() {
		Options options = super.getOptions();
		options.addOption(STDOUT, false, "write result to standard out");
		options.addOption(OBFUSCATE, false, "obfuscate variable names");
		options.addOption(ENCODE, false, "encode identifiers to avoid reserved symbols");
		return options;
	}

	public static JLustre2SMVSettings parse(String[] args) {
		JLustre2SMVArgumentParser parser = new JLustre2SMVArgumentParser();
		parser.parseArguments(args);
		return parser.settings;
	}

	@Override
	protected void parseCommandLine(CommandLine line) {
		super.parseCommandLine(line);

		if (line.hasOption(ENCODE)) {
			settings.encode = true;
		}
		
		if (line.hasOption(OBFUSCATE)) {
			settings.obfuscate = true;
		}

		if (line.hasOption(STDOUT)) {
			settings.stdout = true;
		}
	}
}
