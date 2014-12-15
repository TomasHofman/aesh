package org.jboss.aesh.console.eof;

import org.jboss.aesh.cl.CommandDefinition;
import org.jboss.aesh.console.AeshConsole;
import org.jboss.aesh.console.AeshConsoleBuilder;
import org.jboss.aesh.console.BaseConsoleTest;
import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.command.Command;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.invocation.CommandInvocation;
import org.jboss.aesh.console.command.registry.AeshCommandRegistryBuilder;
import org.jboss.aesh.console.command.registry.CommandRegistry;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.aesh.terminal.TestTerminal;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tomas Hofman (thofman@redhat.com)
 */
public class EofTest extends BaseConsoleTest {

	@Test
	public void writeInputAndClose() throws Exception {
		PipedOutputStream outputStream = new PipedOutputStream();
		PipedInputStream pipedInputStream = new PipedInputStream(outputStream);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		Settings settings = new SettingsBuilder()
				.terminal(new TestTerminal())
				.inputStream(pipedInputStream)
				.outputStream(new PrintStream(byteArrayOutputStream))
				.setPersistExport(false)
				.logging(true)
				.enableExport(true)
				.create();

		CommandRegistry registry = new AeshCommandRegistryBuilder()
				.command(FooCommand.class)
				.create();

		AeshConsoleBuilder consoleBuilder = new AeshConsoleBuilder()
				.settings(settings)
				.commandRegistry(registry)
				.prompt(new Prompt(""));

		AeshConsole aeshConsole = consoleBuilder.create();
		aeshConsole.start();

		outputStream.write(("foo" + Config.getLineSeparator()).getBytes());
		outputStream.close();
		Thread.sleep(100);

		assertFalse(aeshConsole.isRunning());
		assertTrue(byteArrayOutputStream.toString().contains("bar\n")); // command should be processed before console exits
	}

	@CommandDefinition(name="foo", description = "Prints 'bar'")
	private class FooCommand implements Command {

		@Override
		public CommandResult execute(CommandInvocation commandInvocation) throws IOException, InterruptedException {
			commandInvocation.getShell().out().println("bar");
			return CommandResult.SUCCESS;
		}
	}

}
