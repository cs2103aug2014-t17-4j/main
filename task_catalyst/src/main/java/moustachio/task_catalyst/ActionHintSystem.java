package moustachio.task_catalyst;

import moustachio.task_catalyst.Message;

public interface ActionHintSystem {
	public Message processCommand(String userCommand);

	public Message getMessageTyping(String userCommand);
	
	public void testMode();
}
