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
@Table(schema = "dblearnstar", name = "task")
public class Task implements java.io.Serializable {
	private long taskId;
	private String title;
	private String description;
	private String shortDescription;
	private Model model;
	private List<TaskInTestInstance> taskInTestInstances = new ArrayList<TaskInTestInstance>();
	private List<TaskIsOfType> taskIsOfTypes = new ArrayList<TaskIsOfType>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "task_id", unique = true, nullable = false)
	public long getTaskId() {
		return this.taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	@Column(name = "title", nullable = false)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "short_description")
	public String getShortDescription() {
		return this.shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "model_id", nullable = false, foreignKey = @ForeignKey(name = "fk_task_model"))
	public Model getModel() {
		return this.model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "task")
	public List<TaskInTestInstance> getTaskInTestInstances() {
		return this.taskInTestInstances;
	}

	public void setTaskInTestInstances(List<TaskInTestInstance> taskInTestInstances) {
		this.taskInTestInstances = taskInTestInstances;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "task")
	public List<TaskIsOfType> getTaskIsOfTypes() {
		return this.taskIsOfTypes;
	}

	public void setTaskIsOfTypes(List<TaskIsOfType> taskIsOfTypes) {
		this.taskIsOfTypes = taskIsOfTypes;
	}

}
