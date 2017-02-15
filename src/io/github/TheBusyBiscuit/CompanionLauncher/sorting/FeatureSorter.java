package io.github.TheBusyBiscuit.CompanionLauncher.sorting;

import io.github.TheBusyBiscuit.CompanionLauncher.GameData;

public abstract class FeatureSorter extends DirectionalSorter {

	@Override
	public int sort(GameData data1, GameData data2) {
		return data2.features - data1.features;
	}

}
