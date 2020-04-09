package elevatorsplus.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.command.help.HelpMessage;
import ru.soknight.lib.command.help.HelpMessageFactory;
import ru.soknight.lib.command.help.HelpMessageItem;
import ru.soknight.lib.command.placeholder.Placeholder;
import ru.soknight.lib.command.placeholder.SimplePlaceholder;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.validator.PermissionValidator;

public class CommandHelp extends ExtendedSubcommandExecutor {
	
	private final HelpMessage message;
	private final Messages messages;
	
	public CommandHelp(Messages messages) {
		super(messages);
		
		this.messages = messages;
		
		HelpMessageFactory factory = new HelpMessageFactory(messages, "eplus.command.%command%");
		
		Placeholder pcommand = new SimplePlaceholder(messages, "command");
		Placeholder pname = new SimplePlaceholder(messages, "name");
		Placeholder pelevator = new SimplePlaceholder(messages, "elevator");
		Placeholder poption = new SimplePlaceholder(messages, "option");
		Placeholder pvalues = new SimplePlaceholder(messages, "values");
		Placeholder ppage = new SimplePlaceholder(messages, "page");
		Placeholder pelements = new SimplePlaceholder(messages, "elements");
		
		HelpMessageItem help = new HelpMessageItem("help", messages, pcommand);
		HelpMessageItem create = new HelpMessageItem("create", messages, pname);
		HelpMessageItem delete = new HelpMessageItem("delete", messages, pelevator);
		HelpMessageItem modify = new HelpMessageItem("modify", messages, pelevator, poption, pvalues);
		HelpMessageItem info = new HelpMessageItem("info", messages, pelevator);
		HelpMessageItem list = new HelpMessageItem("list", messages, ppage);
		HelpMessageItem reload = new HelpMessageItem("reload", messages);
		HelpMessageItem done = new HelpMessageItem("done", messages);
		HelpMessageItem selection = new HelpMessageItem("selection", messages, pelevator, pelements);
		HelpMessageItem elements = new HelpMessageItem("elements", messages, pelevator, pelements, ppage);
		
		factory.appendItems(true, help, create, delete, modify, info, list, reload, done, selection, elements);
		
		this.message = factory.build();
		
		String permmsg = messages.get("error.no-permissions");
		
		PermissionValidator permval = new PermissionValidator("eplus.command.help", permmsg);
		
		super.addValidators(permval);
	}

	@Override
	public void executeCommand(CommandSender sender, String[] args) {
		if(!validateExecution(sender, args)) return;
		
		if(args.length == 1) {
			message.send(sender);
			return;
		}
		
		String path = "help.detailed." + args[1].toLowerCase();
		
		if(messages.getFileConfig().contains(path))
			messages.getColoredList(path).forEach(s -> messages.send(sender, s));
		else messages.getAndSend(sender, "help.command-not-found");
	}

	@Override
	public List<String> executeTabCompletion(CommandSender sender, String[] args) {
		if(args.length != 2 || !validateTabCompletion(sender, args)) return null;
		
		String arg = args[0].toLowerCase();
		List<String> output = new ArrayList<>();
		
		messages.getFileConfig().getConfigurationSection("help.detailed").getKeys(false).stream()
				.filter(k -> k.startsWith(arg))
				.forEach(k -> output.add(k));
		
		return output;
	}
	
}
