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
@Table(schema = "sql_learning", name = "activity_in_task")
public class ActivityInTask implements java.io.Serializable {
	private long activityInTaskId;
	private Date whenOccured;
	private String type;
	private String payload;
	private TaskInTestInstance taskInTestInstance;
	private Person person;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "activity_in_task_id", unique = true, nullable = false)
	public long getActivityInTaskId() {
		return this.activityInTaskId;
	}

	public void setActivityInTaskId(long activityInTaskId) {
		this.activityInTaskId = activityInTaskId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "when_occured")
	public Date getWhenOccured() {
		return this.whenOccured;
	}

	public void setWhenOccured(Date whenOccured) {
		this.whenOccured = whenOccured;
	}

	@Column(name = "type")
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "payload")
	public String getPayload() {
		return this.payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_in_test_instance_id", nullable = false, foreignKey = @ForeignKey(name = "fk_activity_in_task_task_in_test_instance"))
	public TaskInTestInstance getTaskInTestInstance() {
		return this.taskInTestInstance;
	}

	public void setTaskInTestInstance(TaskInTestInstance taskInTestInstance) {
		this.taskInTestInstance = taskInTestInstance;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "person_id", nullable = false, foreignKey = @ForeignKey(name = "fk_activity_in_task_person"))
	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
