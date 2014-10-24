package moustachio.task_catalyst;

public class Highlight {
	public static final int TYPE_HASHTAG = 0;
	public static final int TYPE_LAST_ADDED = 1;
	public static final int TYPE_LAST_RESTORED = 2;
	public static final int TYPE_OVERLAP_STATIC = 3;
	public static final int TYPE_OVERLAP_DYNAMIC = 4;

	private int taskIndex;
	private int highlightType;

	public Highlight(int taskIndex, int highlightType) {
		this.taskIndex = taskIndex;
		this.highlightType = highlightType;
	}

	public int getTaskIndex() {
		return taskIndex;
	}

	public void setTaskIndex(int taskIndex) {
		this.taskIndex = taskIndex;
	}

	public int getHighlightType() {
		return highlightType;
	}

	public void setHighlightType(int highlightType) {
		this.highlightType = highlightType;
	}
}
