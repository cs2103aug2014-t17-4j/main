package moustachio.task_catalyst;

//@author A0111890L
/**
 * The Message object encapsulates a message and a type, which is generated by
 * the ActionHintSystem. These messages can represent a hint, status message, or
 * an autocomplete request.
 */
public class Message {
	MessageType messageType;
	String message;

	public Message(MessageType type, String message) {
		this.messageType = type;
		this.message = message;
	}

	public MessageType getType() {
		return this.messageType;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
