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

import dblearnstar.model.entities.TaskInTestInstance;

public class ComparatorTaskInTestInstance implements Comparator<TaskInTestInstance> {

	@Override
	public int compare(TaskInTestInstance o1, TaskInTestInstance o2) {
		if (o1.getTestInstance().getTestInstanceId() == o2.getTestInstance().getTestInstanceId()) {
			if (o1.getTask().getTitle() != null && o2.getTask().getTitle() != null) {
				return o1.getTask().getTitle().compareTo(o2.getTask().getTitle());
			} else {
				return Long.compare(o1.getTaskInTestInstanceId(), o2.getTaskInTestInstanceId());
			}
		} else {
			ComparatorTestInstance cti = new ComparatorTestInstance();
			return cti.compare(o1.getTestInstance(), o2.getTestInstance());
		}
	}

}
