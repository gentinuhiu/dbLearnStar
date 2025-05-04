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
@Table(schema = "dblearnstar", name = "student_started_test")
public class StudentStartedTest implements java.io.Serializable {
	private long studentStartedTestId;
	private Student student;
	private TestInstance testInstance;
	private List<StudentSubmitSolution> studentSubmitSolutions = new ArrayList<StudentSubmitSolution>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "student_started_test_id", unique = true, nullable = false)
	public long getStudentStartedTestId() {
		return this.studentStartedTestId;
	}

	public void setStudentStartedTestId(long studentStartedTestId) {
		this.studentStartedTestId = studentStartedTestId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_student_started_test_student"))
	public Student getStudent() {
		return this.student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_instance_id", nullable = false, foreignKey = @ForeignKey(name = "fk_student_started_test_test_instance"))
	public TestInstance getTestInstance() {
		return this.testInstance;
	}

	public void setTestInstance(TestInstance testInstance) {
		this.testInstance = testInstance;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "studentStartedTest")
	public List<StudentSubmitSolution> getStudentSubmitSolutions() {
		return this.studentSubmitSolutions;
	}

	public void setStudentSubmitSolutions(List<StudentSubmitSolution> studentSubmitSolutions) {
		this.studentSubmitSolutions = studentSubmitSolutions;
	}

}
