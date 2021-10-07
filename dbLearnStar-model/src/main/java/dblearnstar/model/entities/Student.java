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
@Table(schema = "sql_learning", name = "student")
public class Student implements java.io.Serializable {
	private long studentId;
	private Person person;
	private List<StudentStartedTest> studentStartedTests = new ArrayList<StudentStartedTest>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "student_id", unique = true, nullable = false)
	public long getStudentId() {
		return this.studentId;
	}

	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "person_id", nullable = false, foreignKey = @ForeignKey(name = "fk_student_person"))
	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "student")
	public List<StudentStartedTest> getStudentStartedTests() {
		return this.studentStartedTests;
	}

	public void setStudentStartedTests(List<StudentStartedTest> studentStartedTests) {
		this.studentStartedTests = studentStartedTests;
	}

}
