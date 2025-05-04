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
import javax.validation.constraints.*;

/*
*/
@Entity
@Table(schema = "dblearnstar", name = "solution_assessment")
public class SolutionAssessment implements java.io.Serializable {
	private long solutionAssessmentId;
	private Date evaluatedOn;
	private String feedback;
	private Boolean passed;
	private String type;
	private Float grade;
	private String feedbackSource;
	private StudentSubmitSolution studentSubmitSolution;
	private TestInstanceParameters testInstanceParameters;
	private List<AssessmentDiscussion> assessmentDiscussions = new ArrayList<AssessmentDiscussion>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "solution_assessment_id", unique = true, nullable = false)
	public long getSolutionAssessmentId() {
		return this.solutionAssessmentId;
	}

	public void setSolutionAssessmentId(long solutionAssessmentId) {
		this.solutionAssessmentId = solutionAssessmentId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "evaluated_on")
	public Date getEvaluatedOn() {
		return this.evaluatedOn;
	}

	public void setEvaluatedOn(Date evaluatedOn) {
		this.evaluatedOn = evaluatedOn;
	}

	@Column(name = "feedback")
	public String getFeedback() {
		return this.feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	@Column(name = "passed")
	public Boolean getPassed() {
		return this.passed;
	}

	public void setPassed(Boolean passed) {
		this.passed = passed;
	}

	@Column(name = "type")
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "grade")
	public Float getGrade() {
		return this.grade;
	}

	public void setGrade(Float grade) {
		this.grade = grade;
	}

	@Column(name = "feedback_source", length = 1000000, columnDefinition = "TEXT")
	public String getFeedbackSource() {
		return this.feedbackSource;
	}

	public void setFeedbackSource(String feedbackSource) {
		this.feedbackSource = feedbackSource;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_submit_solution_id", nullable = false, foreignKey = @ForeignKey(name = "fk_solution_assessment_student_submit_solution"))
	public StudentSubmitSolution getStudentSubmitSolution() {
		return this.studentSubmitSolution;
	}

	public void setStudentSubmitSolution(StudentSubmitSolution studentSubmitSolution) {
		this.studentSubmitSolution = studentSubmitSolution;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_instance_parameters_id", nullable = true, foreignKey = @ForeignKey(name = "fk_solution_assessment_test_instance_parameters"))
	public TestInstanceParameters getTestInstanceParameters() {
		return this.testInstanceParameters;
	}

	public void setTestInstanceParameters(TestInstanceParameters testInstanceParameters) {
		this.testInstanceParameters = testInstanceParameters;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "solutionEvaluation")
	public List<AssessmentDiscussion> getAssessmentDiscussions() {
		return this.assessmentDiscussions;
	}

	public void setAssessmentDiscussions(List<AssessmentDiscussion> assessmentDiscussions) {
		this.assessmentDiscussions = assessmentDiscussions;
	}

}
