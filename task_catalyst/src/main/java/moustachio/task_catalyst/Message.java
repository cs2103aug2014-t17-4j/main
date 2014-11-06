package moustachio.task_catalyst;

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
