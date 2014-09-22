package moustachio.task_catalyst;

public class Message {

	public static final int TYPE_SUCCESS = 0;
	public static final int TYPE_ERROR = 1;
	public static final int TYPE_HINT = 2;
	public static final int TYPE_AUTOCOMPLETE = 3;

	int type;
	String message;

	public Message(int type, String message) {
		this.type = type;
		this.message = message;
	}

	public int getType() {
		return type;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
