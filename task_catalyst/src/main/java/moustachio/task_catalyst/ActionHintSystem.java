package moustachio.task_catalyst;

import moustachio.task_catalyst.Message;

// @author A0111890
public interface ActionHintSystem {
	public void testMode();

	public Message processCommand(String userCommand);

	public Message getMessageTyping(String userCommand);
}
