package io.github.TheBusyBiscuit.CompanionLauncher.sorting;

import java.util.Comparator;

import io.github.TheBusyBiscuit.CompanionLauncher.GameData;
import io.github.TheBusyBiscuit.CompanionLauncher.LauncherUI;

public abstract class DirectionalSorter implements Comparator<Integer> {
	
	public enum SortingDirection {
		
		UP(-1),
		DOWN(1);
		
		int n;
		
		SortingDirection(int n) {
			this.n = n;
		}
		
		public int toInt() {
			return n;
		}
	}
	
	public abstract int sort(GameData data1, GameData data2);
	public abstract SortingDirection getDirection();
	
	public int compare(Integer o1, Integer o2)
    {
        return getDirection().toInt() * sort(LauncherUI.games.games.get(o1), LauncherUI.games.games.get(o2));
    } 
}