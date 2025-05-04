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
@Table(schema = "dblearnstar", name = "task_is_of_type")
public class TaskIsOfType implements java.io.Serializable {
	private long taskIsOfTypeId;
	private Task task;
	private TaskType taskType;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "task_is_of_type_id", unique = true, nullable = false)
	public long getTaskIsOfTypeId() {
		return this.taskIsOfTypeId;
	}

	public void setTaskIsOfTypeId(long taskIsOfTypeId) {
		this.taskIsOfTypeId = taskIsOfTypeId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id", nullable = false, foreignKey = @ForeignKey(name = "fk_task_is_of_type_task"))
	public Task getTask() {
		return this.task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_task_is_of_type_task_type"))
	public TaskType getTaskType() {
		return this.taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

}
