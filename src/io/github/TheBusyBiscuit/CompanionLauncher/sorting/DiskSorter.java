package io.github.TheBusyBiscuit.CompanionLauncher.sorting;

import io.github.TheBusyBiscuit.CompanionLauncher.GameData;

public abstract class DiskSorter extends DirectionalSorter {

	@Override
	public int sort(GameData data1, GameData data2) {
		return data2.size < data1.size ? -1: data2.size > data1.size ? 1: 0;
	}

}
