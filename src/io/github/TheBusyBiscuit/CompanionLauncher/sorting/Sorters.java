package io.github.TheBusyBiscuit.CompanionLauncher.sorting;

import java.util.Map;

public class Sorters {
	
	public static void load(Map<Integer, DirectionalSorter> sorters) {
		sorters.put(0, new AlphabeticalSorter() {
			
			@Override
			public SortingDirection getDirection() {
				return SortingDirection.DOWN;
			}
		});
		
		sorters.put(1, new AlphabeticalSorter() {
			
			@Override
			public SortingDirection getDirection() {
				return SortingDirection.UP;
			}
		});
		
		sorters.put(2, new FeatureSorter() {
			
			@Override
			public SortingDirection getDirection() {
				return SortingDirection.DOWN;
			}
		});
		
		sorters.put(3, new FeatureSorter() {
			
			@Override
			public SortingDirection getDirection() {
				return SortingDirection.UP;
			}
		});
		
		sorters.put(4, new PriceSorter() {
			
			@Override
			public SortingDirection getDirection() {
				return SortingDirection.DOWN;
			}
		});
		
		sorters.put(5, new PriceSorter() {
			
			@Override
			public SortingDirection getDirection() {
				return SortingDirection.UP;
			}
		});
		
		sorters.put(6, new DiskSorter() {
			
			@Override
			public SortingDirection getDirection() {
				return SortingDirection.DOWN;
			}
		});
		
		sorters.put(7, new DiskSorter() {
			
			@Override
			public SortingDirection getDirection() {
				return SortingDirection.UP;
			}
		});
	}

}
