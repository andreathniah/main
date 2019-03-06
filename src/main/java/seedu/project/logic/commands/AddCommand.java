package seedu.project.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.project.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.project.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.project.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.project.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.project.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.project.logic.CommandHistory;
import seedu.project.logic.commands.exceptions.CommandException;
import seedu.project.model.Model;
import seedu.project.model.task.Task;

/**
 * Adds a task to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";
    public static final String COMMAND_ALIAS = "a";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a task to the address book. " + "Parameters: "
            + PREFIX_NAME + "NAME " + PREFIX_PHONE + "PHONE " + PREFIX_EMAIL + "EMAIL " + PREFIX_ADDRESS + "ADDRESS "
            + "[" + PREFIX_TAG + "TAG]...\n" + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 " + PREFIX_EMAIL + "johnd@example.com " + PREFIX_ADDRESS
            + "311, Clementi Ave 2, #02-25 " + PREFIX_TAG + "friends " + PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New task added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This task already exists in the address book";

    private final Task toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Task}
     */
    public AddCommand(Task task) {
        requireNonNull(task);
        toAdd = task;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        if (model.hasTask(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.addTask(toAdd);
        model.commitProject();
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddCommand // instanceof handles nulls
                        && toAdd.equals(((AddCommand) other).toAdd));
    }
}
