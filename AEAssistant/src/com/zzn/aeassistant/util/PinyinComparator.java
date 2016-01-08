package com.zzn.aeassistant.util;

import java.util.Comparator;

import com.zzn.aeassistant.activity.project.ProjectUserAdapter.UserItem;
import com.zzn.aeassistant.vo.PhoneContact;

public class PinyinComparator {
	public static UserComparator userComparator = new UserComparator();
	private static class UserComparator implements Comparator<UserItem> {
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

	public static PhoneComparator phoneComparator = new PhoneComparator();
	private static class PhoneComparator implements Comparator<PhoneContact> {
		public int compare(PhoneContact o1, PhoneContact o2) {
			if (o1.getSortLetter().equals("@") || o2.getSortLetter().equals("#")) {
				return -1;
			} else if (o1.getSortLetter().equals("#") || o2.getSortLetter().equals("@")) {
				return 1;
			} else {
				return o1.getSortLetter().compareTo(o2.getSortLetter());
			}
		}
	}
}