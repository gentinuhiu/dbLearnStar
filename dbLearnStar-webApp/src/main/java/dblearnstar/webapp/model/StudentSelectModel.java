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

package dblearnstar.webapp.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;

import dblearnstar.model.entities.Person;
import dblearnstar.model.entities.Student;

public class StudentSelectModel extends AbstractSelectModel {
	private List<Student> students;

	public StudentSelectModel(List<Student> students) {
		if (students == null) {
			this.students = new ArrayList<Student>();
		} else {
			this.students = students;
		}
	}

	@Override
	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	@Override
	public List<OptionModel> getOptions() {
		List<OptionModel> options = new ArrayList<OptionModel>();
		for (Student student : students) {
			Person p = student.getPerson();
			String name = p.getLastName() + " " + p.getFirstName() + " [" + p.getUserName() + "]";
			options.add(new OptionModelImpl(name, student));
		}
		return options;
	}
}
