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
@Table(schema = "dblearnstar", name = "student_submit_solution")
public class StudentSubmitSolution implements java.io.Serializable {
	private long studentSubmitSolutionId;
	private String submission;
	private Date submittedOn;
	private Boolean evaluationSimple;
	private Boolean evaluationComplex;
	private Boolean evaluationExam;
	private Boolean notForEvaluation;
	private String ipAddress;
	private String clientInfo;
	private StudentStartedTest studentStartedTest;
	private TaskInTestInstance taskInTestInstance;
	private List<SolutionAssessment> evaluations = new ArrayList<SolutionAssessment>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "student_submit_solution_id", unique = true, nullable = false)
	public long getStudentSubmitSolutionId() {
		return this.studentSubmitSolutionId;
	}

	public void setStudentSubmitSolutionId(long studentSubmitSolutionId) {
		this.studentSubmitSolutionId = studentSubmitSolutionId;
	}

	@Column(name = "submission")
	public String getSubmission() {
		return this.submission;
	}

	public void setSubmission(String submission) {
		this.submission = submission;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "submitted_on")
	public Date getSubmittedOn() {
		return this.submittedOn;
	}

	public void setSubmittedOn(Date submittedOn) {
		this.submittedOn = submittedOn;
	}

	@Column(name = "evaluation_simple")
	public Boolean getEvaluationSimple() {
		return this.evaluationSimple;
	}

	public void setEvaluationSimple(Boolean evaluationSimple) {
		this.evaluationSimple = evaluationSimple;
	}

	@Column(name = "evaluation_complex")
	public Boolean getEvaluationComplex() {
		return this.evaluationComplex;
	}

	public void setEvaluationComplex(Boolean evaluationComplex) {
		this.evaluationComplex = evaluationComplex;
	}

	@Column(name = "evaluation_exam")
	public Boolean getEvaluationExam() {
		return this.evaluationExam;
	}

	public void setEvaluationExam(Boolean evaluationExam) {
		this.evaluationExam = evaluationExam;
	}

	@Column(name = "not_for_evaluation")
	public Boolean getNotForEvaluation() {
		return this.notForEvaluation;
	}

	public void setNotForEvaluation(Boolean notForEvaluation) {
		this.notForEvaluation = notForEvaluation;
	}

	@Column(name = "ip_address")
	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Column(name = "client_info")
	public String getClientInfo() {
		return this.clientInfo;
	}

	public void setClientInfo(String clientInfo) {
		this.clientInfo = clientInfo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_started_test_id", nullable = false, foreignKey = @ForeignKey(name = "fk_student_submit_solution_student_started_test"))
	public StudentStartedTest getStudentStartedTest() {
		return this.studentStartedTest;
	}

	public void setStudentStartedTest(StudentStartedTest studentStartedTest) {
		this.studentStartedTest = studentStartedTest;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_in_test_instance_id", nullable = false, foreignKey = @ForeignKey(name = "fk_student_submit_solution_task_in_test_instance"))
	public TaskInTestInstance getTaskInTestInstance() {
		return this.taskInTestInstance;
	}

	public void setTaskInTestInstance(TaskInTestInstance taskInTestInstance) {
		this.taskInTestInstance = taskInTestInstance;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "studentSubmitSolution")
	public List<SolutionAssessment> getEvaluations() {
		return this.evaluations;
	}

	public void setEvaluations(List<SolutionAssessment> evaluations) {
		this.evaluations = evaluations;
	}

}
