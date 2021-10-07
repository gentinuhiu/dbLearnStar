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

import java.util.Comparator;

import dblearnstar.model.entities.TestInstance;

public class ComparatorTestInstance implements Comparator<TestInstance> {

	@Override
	public int compare(TestInstance o1, TestInstance o2) {
		if (o2.getScheduledFor() != null && o1.getScheduledFor() != null) {
			return o2.getScheduledFor().compareTo(o1.getScheduledFor());
		} else {
			return o2.getTitle().compareTo(o1.getTitle());
		}
	}

}
