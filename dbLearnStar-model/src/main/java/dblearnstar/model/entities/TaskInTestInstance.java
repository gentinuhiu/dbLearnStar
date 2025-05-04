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
@Table(schema = "dblearnstar", name = "task_in_test_instance")
public class TaskInTestInstance implements java.io.Serializable {
	private long taskInTestInstanceId;
	private Float points;
	private Task task;
	private TestInstance testInstance;
	private List<StudentSubmitSolution> studentSubmitSolutions = new ArrayList<StudentSubmitSolution>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "task_in_test_instance_id", unique = true, nullable = false)
	public long getTaskInTestInstanceId() {
		return this.taskInTestInstanceId;
	}

	public void setTaskInTestInstanceId(long taskInTestInstanceId) {
		this.taskInTestInstanceId = taskInTestInstanceId;
	}

	@Column(name = "points")
	public Float getPoints() {
		return this.points;
	}

	public void setPoints(Float points) {
		this.points = points;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id", nullable = false, foreignKey = @ForeignKey(name = "fk_task_in_test_instance_task"))
	public Task getTask() {
		return this.task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_instance_id", nullable = false, foreignKey = @ForeignKey(name = "fk_task_in_test_instance_test_instance"))
	public TestInstance getTestInstance() {
		return this.testInstance;
	}

	public void setTestInstance(TestInstance testInstance) {
		this.testInstance = testInstance;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "taskInTestInstance")
	public List<StudentSubmitSolution> getStudentSubmitSolutions() {
		return this.studentSubmitSolutions;
	}

	public void setStudentSubmitSolutions(List<StudentSubmitSolution> studentSubmitSolutions) {
		this.studentSubmitSolutions = studentSubmitSolutions;
	}

}
