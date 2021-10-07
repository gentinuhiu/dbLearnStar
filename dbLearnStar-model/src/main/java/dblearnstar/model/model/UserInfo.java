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

import java.util.List;

public class UserInfo {
	public static enum UserRole {
		NONE, STUDENT, INSTRUCTOR, ADMINISTRATOR
	};

	private String userName;
	private Long personId;
	private List<UserRole> userRoles;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public List<UserRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	public boolean isNone() {
		if (userRoles != null) {
			return userRoles.contains(UserRole.NONE);
		} else {
			return false;
		}
	}

	public boolean isStudent() {
		if (userRoles != null) {
			return userRoles.contains(UserRole.STUDENT);
		} else {
			return false;
		}
	}

	public boolean isInstructor() {
		if (userRoles != null) {
			return userRoles.contains(UserRole.INSTRUCTOR);
		} else {
			return false;
		}
	}

	public boolean isAdministrator() {
		if (userRoles != null) {
			return userRoles.contains(UserRole.ADMINISTRATOR);
		} else {
			return false;
		}
	}

}
