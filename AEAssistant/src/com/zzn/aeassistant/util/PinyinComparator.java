package com.zzn.aeassistant.util;

import java.util.Comparator;

import com.zzn.aeassistant.activity.project.ProjectUserAdapter.UserItem;

public class PinyinComparator implements Comparator<UserItem> {

	public int compare(UserItem o1, UserItem o2) {
		if (o1.sortLetter.equals("@") || o2.sortLetter.equals("#")) {
			return -1;
		} else if (o1.sortLetter.equals("#") || o2.sortLetter.equals("@")) {
			return 1;
		} else {
			return o1.sortLetter.compareTo(o2.sortLetter);
		}
	}

}
