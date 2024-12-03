/*******************************************************************************
 * Copyright (C) 2021 Vangel V. Ajanovski
 *     
 * This file is part of the dbLearnStar system (hereinafter: dbLearn*).
 *     
 * dbLearn* is free software: you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your option) any later 
 * version.
 *     
 * dbLearn* is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
 * details.
 *     
 * You should have received a copy of the GNU General Public License along 
 * with dbLearn*.  If not, see <https://www.gnu.org/licenses/>.
 * 
 ******************************************************************************/

package dblearnstar.model.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import dblearnstar.model.entities.TestCollection;

public class ComparatorTestCollection implements Comparator<TestCollection> {

	public List<Integer> getCoding(TestCollection i) {
		Integer broj = i.getOrdering();
		if (broj == null) {
			broj = 0;
		}
		List<Integer> l;
		if (i.getParentCollection() == null) {
			l = new ArrayList<Integer>();
		} else {
			l = getCoding(i.getParentCollection());
		}
		l.add(broj);
		return l;
	}

	@Override
	public int compare(TestCollection tc1, TestCollection tc2) {
		List<Integer> o1 = getCoding(tc1);
		List<Integer> o2 = getCoding(tc2);
		for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
			int c = o1.get(i).compareTo(o2.get(i));
			if (c != 0) {
				return c;
			}
		}
		return Integer.compare(o1.size(), o2.size());
	}

}
