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
@Table(schema = "sql_learning", name = "test_instance")
public class TestInstance implements java.io.Serializable {
	private long testInstanceId;
	private String title;
	private Boolean openToAllStudents;
	private String description;
	private Boolean openForReviewByStudents;
	private Date scheduledFor;
	private Date scheduledUntil;
	private Integer ordering;
	private List<TestInstanceParameters> testInstanceParameters = new ArrayList<TestInstanceParameters>();
	private TestTemplate testTemplate;
	private List<TaskInTestInstance> taskInTestInstances = new ArrayList<TaskInTestInstance>();
	private TestCollection testCollection;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "test_instance_id", unique = true, nullable = false)
	public long getTestInstanceId() {
		return this.testInstanceId;
	}

	public void setTestInstanceId(long testInstanceId) {
		this.testInstanceId = testInstanceId;
	}

	@Column(name = "title", nullable = false)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "open_to_all_students", nullable = false)
	public Boolean getOpenToAllStudents() {
		return this.openToAllStudents;
	}

	public void setOpenToAllStudents(Boolean openToAllStudents) {
		this.openToAllStudents = openToAllStudents;
	}

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "open_for_review_by_students", nullable = false)
	public Boolean getOpenForReviewByStudents() {
		return this.openForReviewByStudents;
	}

	public void setOpenForReviewByStudents(Boolean openForReviewByStudents) {
		this.openForReviewByStudents = openForReviewByStudents;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "scheduled_for")
	public Date getScheduledFor() {
		return this.scheduledFor;
	}

	public void setScheduledFor(Date scheduledFor) {
		this.scheduledFor = scheduledFor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "scheduled_until")
	public Date getScheduledUntil() {
		return this.scheduledUntil;
	}

	public void setScheduledUntil(Date scheduledUntil) {
		this.scheduledUntil = scheduledUntil;
	}

	@Column(name = "ordering")
	public Integer getOrdering() {
		return this.ordering;
	}

	public void setOrdering(Integer ordering) {
		this.ordering = ordering;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "testInstance")
	public List<TestInstanceParameters> getTestInstanceParameters() {
		return this.testInstanceParameters;
	}

	public void setTestInstanceParameters(List<TestInstanceParameters> testInstanceParameters) {
		this.testInstanceParameters = testInstanceParameters;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_template_id", nullable = false, foreignKey = @ForeignKey(name = "fk_test_instance_test_template"))
	public TestTemplate getTestTemplate() {
		return this.testTemplate;
	}

	public void setTestTemplate(TestTemplate testTemplate) {
		this.testTemplate = testTemplate;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "testInstance")
	public List<TaskInTestInstance> getTaskInTestInstances() {
		return this.taskInTestInstances;
	}

	public void setTaskInTestInstances(List<TaskInTestInstance> taskInTestInstances) {
		this.taskInTestInstances = taskInTestInstances;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_collection_id", nullable = true, foreignKey = @ForeignKey(name = "fk_test_instance_test_collection"))
	public TestCollection getTestCollection() {
		return this.testCollection;
	}

	public void setTestCollection(TestCollection testCollection) {
		this.testCollection = testCollection;
	}

}
