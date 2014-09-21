package moustachio;

import java.util.ArrayList;

public class ProcessorStub implements Processor {

	@Override
	public ArrayList<Task> searchByHashtag(ArrayList<Task> list, String hashtag) {
		return list;
	}

	@Override
	public ArrayList<Task> searchByKeyword(ArrayList<Task> list, String keyword) {
		return list;
	}

	@Override
	public ArrayList<Task> sortByDate(ArrayList<Task> list) {
		return list;
	}

}
