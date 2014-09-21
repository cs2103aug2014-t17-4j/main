package moustachio;

import java.util.List;

public class ListProcessorStub implements ListProcessor {

	@Override
	public List<Task> searchByHashtag(List<Task> list, String hashtag) {
		return list;
	}

	@Override
	public List<Task> searchByKeyword(List<Task> list, String keyword) {
		return list;
	}

	@Override
	public List<Task> sortByDate(List<Task> list) {
		return list;
	}

}
