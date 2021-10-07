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

package dblearnstar.webapp.pages.admin;

import java.util.List;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import dblearnstar.model.entities.Person;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.services.ActivityManager;

@AdministratorPage
public class ActiveUsersPage {

	@Inject
	private ActivityManager activityManager;

	public void onActivate() {
		if (seconds == null)
			seconds = 600;
	}

	public List<Person> getActivePersons() {
		return activityManager.activePersonsInInterval(seconds);
	}

	@Persist
	@Property
	private Integer seconds;

}
