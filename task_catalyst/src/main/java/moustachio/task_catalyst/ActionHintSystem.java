package moustachio.task_catalyst;

import moustachio.task_catalyst.Message;

//@author A0111890
/**
 * ActionHintSystem is used to process commands and provide live
 * hints based on the user's input.
 */
public interface ActionHintSystem {
	public void testMode();

	public Message processCommand(String userCommand);

	public Message getMessageTyping(String userCommand);
}
