package io.github.TheBusyBiscuit.CompanionLauncher.sorting;

import java.util.Comparator;

public class CategorySorter implements Comparator<Integer> {

	@Override
	public int compare(Integer id1, Integer id2) {
		return id1 - id2;
	}

}
