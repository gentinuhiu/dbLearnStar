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
@Table(schema = "sql_learning", name = "task_type_in_template")
public class TaskTypeInTemplate implements java.io.Serializable {
	private long taskTypeInTemplateId;
	private TestTemplate testTemplate;
	private TaskType taskType;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "task_type_in_template_id", unique = true, nullable = false)
	public long getTaskTypeInTemplateId() {
		return this.taskTypeInTemplateId;
	}

	public void setTaskTypeInTemplateId(long taskTypeInTemplateId) {
		this.taskTypeInTemplateId = taskTypeInTemplateId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_template_id", nullable = false, foreignKey = @ForeignKey(name = "fk_task_type_in_template_test_template"))
	public TestTemplate getTestTemplate() {
		return this.testTemplate;
	}

	public void setTestTemplate(TestTemplate testTemplate) {
		this.testTemplate = testTemplate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_task_type_in_template_task_type"))
	public TaskType getTaskType() {
		return this.taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

}
