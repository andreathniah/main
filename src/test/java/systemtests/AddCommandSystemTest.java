package systemtests;

import static seedu.project.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.project.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.project.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.project.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.project.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.project.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.project.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.project.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.project.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.project.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.project.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.project.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.project.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.project.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.project.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.project.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.project.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.project.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.project.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.project.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.project.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.project.testutil.TypicalTasks.ALICE;
import static seedu.project.testutil.TypicalTasks.AMY;
import static seedu.project.testutil.TypicalTasks.BOB;
import static seedu.project.testutil.TypicalTasks.CARL;
import static seedu.project.testutil.TypicalTasks.HOON;
import static seedu.project.testutil.TypicalTasks.IDA;
import static seedu.project.testutil.TypicalTasks.KEYWORD_MATCHING_MEIER;

import org.junit.Test;

import seedu.project.commons.core.Messages;
import seedu.project.commons.core.index.Index;
import seedu.project.logic.commands.AddCommand;
import seedu.project.logic.commands.RedoCommand;
import seedu.project.logic.commands.UndoCommand;
import seedu.project.model.Model;
import seedu.project.model.tag.Tag;
import seedu.project.model.task.Address;
import seedu.project.model.task.Email;
import seedu.project.model.task.Name;
import seedu.project.model.task.Phone;
import seedu.project.model.task.Task;
import seedu.project.testutil.TaskBuilder;
import seedu.project.testutil.TaskUtil;

public class AddCommandSystemTest extends ProjectSystemTest {

    @Test
    public void add() {
        Model model = getModel();

        /* ------------------------ Perform add operations on the shown unfiltered list ----------------------------- */

        /* Case: add a task without tags to a non-empty address book, command with leading spaces and trailing spaces
         * -> added
         */
        Task toAdd = AMY;
        String command = "   " + AddCommand.COMMAND_WORD + "  " + NAME_DESC_AMY + "  " + PHONE_DESC_AMY + " "
                + EMAIL_DESC_AMY + "   " + ADDRESS_DESC_AMY + "   " + TAG_DESC_FRIEND + " ";
        assertCommandSuccess(command, toAdd);

        /* Case: undo adding Amy to the list -> Amy deleted */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: redo adding Amy to the list -> Amy added again */
        command = RedoCommand.COMMAND_WORD;
        model.addTask(toAdd);
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: add a task with all fields same as another task in the address book except name -> added */
        toAdd = new TaskBuilder(AMY).withName(VALID_NAME_BOB).build();
        command = AddCommand.COMMAND_WORD + NAME_DESC_BOB + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + TAG_DESC_FRIEND;
        assertCommandSuccess(command, toAdd);

        /* Case: add a task with all fields same as another task in the address book except phone and email
         * -> added
         */
        toAdd = new TaskBuilder(AMY).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).build();
        command = TaskUtil.getAddCommand(toAdd);
        assertCommandSuccess(command, toAdd);

        /* Case: add to empty address book -> added */
        deleteAllTasks();
        assertCommandSuccess(ALICE);

        /* Case: add a task with tags, command with parameters in random order -> added */
        toAdd = BOB;
        command = AddCommand.COMMAND_WORD + TAG_DESC_FRIEND + PHONE_DESC_BOB + ADDRESS_DESC_BOB + NAME_DESC_BOB
                + TAG_DESC_HUSBAND + EMAIL_DESC_BOB;
        assertCommandSuccess(command, toAdd);

        /* Case: add a task, missing tags -> added */
        assertCommandSuccess(HOON);

        /* -------------------------- Perform add operation on the shown filtered list ------------------------------ */

        /* Case: filters the task list before adding -> added */
        showTasksWithName(KEYWORD_MATCHING_MEIER);
        assertCommandSuccess(IDA);

        /* ------------------------ Perform add operation while a task card is selected --------------------------- */

        /* Case: selects first card in the task list, add a task -> added, card selection remains unchanged */
        selectTask(Index.fromOneBased(1));
        assertCommandSuccess(CARL);

        /* ----------------------------------- Perform invalid add operations --------------------------------------- */

        /* Case: add a duplicate task -> rejected */
        command = TaskUtil.getAddCommand(HOON);
        assertCommandFailure(command, AddCommand.MESSAGE_DUPLICATE_PERSON);

        /* Case: add a duplicate task except with different phone -> rejected */
        toAdd = new TaskBuilder(HOON).withPhone(VALID_PHONE_BOB).build();
        command = TaskUtil.getAddCommand(toAdd);
        assertCommandFailure(command, AddCommand.MESSAGE_DUPLICATE_PERSON);

        /* Case: add a duplicate task except with different email -> rejected */
        toAdd = new TaskBuilder(HOON).withEmail(VALID_EMAIL_BOB).build();
        command = TaskUtil.getAddCommand(toAdd);
        assertCommandFailure(command, AddCommand.MESSAGE_DUPLICATE_PERSON);

        /* Case: add a duplicate task except with different address -> rejected */
        toAdd = new TaskBuilder(HOON).withAddress(VALID_ADDRESS_BOB).build();
        command = TaskUtil.getAddCommand(toAdd);
        assertCommandFailure(command, AddCommand.MESSAGE_DUPLICATE_PERSON);

        /* Case: add a duplicate task except with different tags -> rejected */
        command = TaskUtil.getAddCommand(HOON) + " " + PREFIX_TAG.getPrefix() + "friends";
        assertCommandFailure(command, AddCommand.MESSAGE_DUPLICATE_PERSON);

        /* Case: missing name -> rejected */
        command = AddCommand.COMMAND_WORD + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));

        /* Case: missing phone -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));

        /* Case: missing email -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + ADDRESS_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));

        /* Case: missing address -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));

        /* Case: invalid keyword -> rejected */
        command = "adds " + TaskUtil.getTaskDetails(toAdd);
        assertCommandFailure(command, Messages.MESSAGE_UNKNOWN_COMMAND);

        /* Case: invalid name -> rejected */
        command = AddCommand.COMMAND_WORD + INVALID_NAME_DESC + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        assertCommandFailure(command, Name.MESSAGE_CONSTRAINTS);

        /* Case: invalid phone -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + INVALID_PHONE_DESC + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        assertCommandFailure(command, Phone.MESSAGE_CONSTRAINTS);

        /* Case: invalid email -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + INVALID_EMAIL_DESC + ADDRESS_DESC_AMY;
        assertCommandFailure(command, Email.MESSAGE_CONSTRAINTS);

        /* Case: invalid address -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + INVALID_ADDRESS_DESC;
        assertCommandFailure(command, Address.MESSAGE_CONSTRAINTS);

        /* Case: invalid tag -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + INVALID_TAG_DESC;
        assertCommandFailure(command, Tag.MESSAGE_CONSTRAINTS);
    }

    /**
     * Executes the {@code AddCommand} that adds {@code toAdd} to the model and asserts that the,<br>
     * 1. Command box displays an empty string.<br>
     * 2. Command box has the default style class.<br>
     * 3. Result display box displays the success message of executing {@code AddCommand} with the details of
     * {@code toAdd}.<br>
     * 4. {@code Storage} and {@code TaskListPanel} equal to the corresponding components in
     * the current model added with {@code toAdd}.<br>
     * 5. Browser url and selected card remain unchanged.<br>
     * 6. Status bar's sync status changes.<br>
     * Verifications 1, 3 and 4 are performed by
     * {@code ProjectSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see ProjectSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(Task toAdd) {
        assertCommandSuccess(TaskUtil.getAddCommand(toAdd), toAdd);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(Task)}. Executes {@code command}
     * instead.
     * @see AddCommandSystemTest#assertCommandSuccess(Task)
     */
    private void assertCommandSuccess(String command, Task toAdd) {
        Model expectedModel = getModel();
        expectedModel.addTask(toAdd);
        String expectedResultMessage = String.format(AddCommand.MESSAGE_SUCCESS, toAdd);

        assertCommandSuccess(command, expectedModel, expectedResultMessage);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Task)} except asserts that
     * the,<br>
     * 1. Result display box displays {@code expectedResultMessage}.<br>
     * 2. {@code Storage} and {@code TaskListPanel} equal to the corresponding components in
     * {@code expectedModel}.<br>
     * @see AddCommandSystemTest#assertCommandSuccess(String, Task)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and asserts that the,<br>
     * 1. Command box displays {@code command}.<br>
     * 2. Command box has the error style class.<br>
     * 3. Result display box displays {@code expectedResultMessage}.<br>
     * 4. {@code Storage} and {@code TaskListPanel} remain unchanged.<br>
     * 5. Browser url, selected card and status bar remain unchanged.<br>
     * Verifications 1, 3 and 4 are performed by
     * {@code ProjectSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see ProjectSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}
