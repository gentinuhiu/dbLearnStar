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
@Table(schema = "sql_learning", name = "test_template")
public class TestTemplate implements java.io.Serializable {
	private long testTemplateId;
	private String title;
	private Model model;
	private List<TaskTypeInTemplate> taskTypeInTemplates = new ArrayList<TaskTypeInTemplate>();
	private TestType testType;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "test_template_id", unique = true, nullable = false)
	public long getTestTemplateId() {
		return this.testTemplateId;
	}

	public void setTestTemplateId(long testTemplateId) {
		this.testTemplateId = testTemplateId;
	}

	@Column(name = "title")
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "model_id", nullable = false, foreignKey = @ForeignKey(name = "fk_test_template_model"))
	public Model getModel() {
		return this.model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "testTemplate")
	public List<TaskTypeInTemplate> getTaskTypeInTemplates() {
		return this.taskTypeInTemplates;
	}

	public void setTaskTypeInTemplates(List<TaskTypeInTemplate> taskTypeInTemplates) {
		this.taskTypeInTemplates = taskTypeInTemplates;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_test_template_test_type"))
	public TestType getTestType() {
		return this.testType;
	}

	public void setTestType(TestType testType) {
		this.testType = testType;
	}

}
