package io.github.TheBusyBiscuit.CompanionLauncher.sorting;

import io.github.TheBusyBiscuit.CompanionLauncher.GameData;

public abstract class AlphabeticalSorter extends DirectionalSorter {

	@Override
	public int sort(GameData data1, GameData data2) {
		return data1.name.compareTo(data2.name);
	}

}
