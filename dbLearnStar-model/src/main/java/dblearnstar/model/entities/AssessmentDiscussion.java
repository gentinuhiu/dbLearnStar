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
@Table(schema = "sql_learning", name = "assessment_discussion")
public class AssessmentDiscussion implements java.io.Serializable {
	private long assessmentDiscussionId;
	private String type;
	private String message;
	private Date postedOn;
	private Boolean complaint;
	private SolutionAssessment solutionEvaluation;
	private AssessmentDiscussion replyTo;
	private Person person;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "assessment_discussion_id", unique = true, nullable = false)
	public long getAssessmentDiscussionId() {
		return this.assessmentDiscussionId;
	}

	public void setAssessmentDiscussionId(long assessmentDiscussionId) {
		this.assessmentDiscussionId = assessmentDiscussionId;
	}

	@Column(name = "type")
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "message", nullable = false, length = 10000)
	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "posted_on")
	public Date getPostedOn() {
		return this.postedOn;
	}

	public void setPostedOn(Date postedOn) {
		this.postedOn = postedOn;
	}

	@Column(name = "complaint")
	public Boolean getComplaint() {
		return this.complaint;
	}

	public void setComplaint(Boolean complaint) {
		this.complaint = complaint;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "solution_assessment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_assessment_discussion_solution_assessment"))
	public SolutionAssessment getSolutionEvaluation() {
		return this.solutionEvaluation;
	}

	public void setSolutionEvaluation(SolutionAssessment solutionEvaluation) {
		this.solutionEvaluation = solutionEvaluation;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reply_to_assessment_discussion_id", nullable = true, foreignKey = @ForeignKey(name = "fk_assessment_discussion_assessment_discussion"))
	public AssessmentDiscussion getReplyTo() {
		return this.replyTo;
	}

	public void setReplyTo(AssessmentDiscussion replyTo) {
		this.replyTo = replyTo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "person_id", nullable = false, foreignKey = @ForeignKey(name = "fk_assessment_discussion_person"))
	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
