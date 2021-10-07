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
@Table(schema = "sql_learning", name = "group_focus_on_test")
public class GroupFocusOnTest implements java.io.Serializable {
	private long groupFocusOnTestId;
	private Group group;
	private TestInstance testInstance;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "group_focus_on_test_id", unique = true, nullable = false)
	public long getGroupFocusOnTestId() {
		return this.groupFocusOnTestId;
	}

	public void setGroupFocusOnTestId(long groupFocusOnTestId) {
		this.groupFocusOnTestId = groupFocusOnTestId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", nullable = false, foreignKey = @ForeignKey(name = "fk_group_focus_on_test_group"))
	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_instance_id", nullable = false, foreignKey = @ForeignKey(name = "fk_group_focus_on_test_test_instance"))
	public TestInstance getTestInstance() {
		return this.testInstance;
	}

	public void setTestInstance(TestInstance testInstance) {
		this.testInstance = testInstance;
	}

}
