package moustachio.task_catalyst;

public class Highlight {
	public static final int TYPE_HASHTAG_DEFAULT = 0;
	public static final int TYPE_HASHTAG_CUSTOM = 1;
	public static final int TYPE_HASHTAG_INVALID = 2;
	public static final int TYPE_SEARCH = 3;
	public static final int TYPE_TASK_LAST_ADDED = 4;
	public static final int TYPE_TASK_OVERLAP_STATIC = 5;
	public static final int TYPE_TASK_OVERLAP_DYNAMIC = 6;
	public static final int TYPE_TASK_PRIORITY = 7;
	public static final int TYPE_TASK_PRIORITY_OVERLAP_STATIC = 8;
	public static final int TYPE_TASK_PRIORITY_OVERLAP_DYNAMIC = 9;

	private int taskIndex;
	private int highlightType;

	public Highlight(int highlightType, int taskIndex) {
		this.taskIndex = taskIndex;
		this.highlightType = highlightType;
	}

	public int getIndex() {
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
