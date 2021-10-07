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

package dblearnstar.webapp.pages;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;

import dblearnstar.model.entities.Person;
import dblearnstar.webapp.annotations.StudentPage;
import dblearnstar.webapp.services.PersonManager;
import dblearnstar.webapp.services.TestManager;

@StudentPage
public class PersonalAnalyticsPage {

	@Inject
	private Session session;

	@Inject
	private TestManager testManager;
	@Inject
	private PersonManager personManager;

	public List<Object[]> getStudentsSolving() {
		return testManager.getStudentsSolving();
	}

	@Property
	private Object[] personCount;

	public String getName() {
		return personManager.getPersonFullNameWithId((Person) personCount[0]);
	}

	public Long getCount() {
		return (Long) personCount[1];
	}

}
