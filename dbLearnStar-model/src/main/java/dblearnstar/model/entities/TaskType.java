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
@Table(schema = "dblearnstar", name = "task_type")
public class TaskType implements java.io.Serializable {
	private long taskTypeId;
	private String title;
	private Integer points;
	private String codetype;
	private List<TaskTypeInTemplate> taskTypeInTemplates = new ArrayList<TaskTypeInTemplate>();
	private List<TaskIsOfType> taskIsOfTypes = new ArrayList<TaskIsOfType>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "task_type_id", unique = true, nullable = false)
	public long getTaskTypeId() {
		return this.taskTypeId;
	}

	public void setTaskTypeId(long taskTypeId) {
		this.taskTypeId = taskTypeId;
	}

	@Column(name = "title", nullable = false)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "points")
	public Integer getPoints() {
		return this.points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	@Column(name = "codetype", nullable = false)
	public String getCodetype() {
		return this.codetype;
	}

	public void setCodetype(String codetype) {
		this.codetype = codetype;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "taskType")
	public List<TaskTypeInTemplate> getTaskTypeInTemplates() {
		return this.taskTypeInTemplates;
	}

	public void setTaskTypeInTemplates(List<TaskTypeInTemplate> taskTypeInTemplates) {
		this.taskTypeInTemplates = taskTypeInTemplates;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "taskType")
	public List<TaskIsOfType> getTaskIsOfTypes() {
		return this.taskIsOfTypes;
	}

	public void setTaskIsOfTypes(List<TaskIsOfType> taskIsOfTypes) {
		this.taskIsOfTypes = taskIsOfTypes;
	}

}
