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

package dblearnstar.model.entities;

import java.util.*;
import javax.persistence.*;

/*
*/
@Entity
@Table(schema = "sql_learning", name = "group")
public class Group implements java.io.Serializable {
	private long groupId;
	private String title;
	private List<GroupMember> groupMembers = new ArrayList<GroupMember>();
	private List<GroupFocusOnTest> groupFocusOnTests = new ArrayList<GroupFocusOnTest>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "group_id", unique = true, nullable = false)
	public long getGroupId() {
		return this.groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	@Column(name = "title")
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
	public List<GroupMember> getGroupMembers() {
		return this.groupMembers;
	}

	public void setGroupMembers(List<GroupMember> groupMembers) {
		this.groupMembers = groupMembers;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
	public List<GroupFocusOnTest> getGroupFocusOnTests() {
		return this.groupFocusOnTests;
	}

	public void setGroupFocusOnTests(List<GroupFocusOnTest> groupFocusOnTests) {
		this.groupFocusOnTests = groupFocusOnTests;
	}

}
