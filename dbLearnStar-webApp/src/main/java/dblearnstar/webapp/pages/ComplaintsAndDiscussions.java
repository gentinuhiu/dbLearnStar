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

package dblearnstar.webapp.pages;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import dblearnstar.model.entities.AssessmentDiscussion;
import dblearnstar.model.entities.Person;
import dblearnstar.model.entities.SolutionAssessment;
import dblearnstar.model.model.ComparatorAssessmentDiscussionByReplyTo;
import dblearnstar.model.model.UserInfo;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.annotations.StudentPage;
import dblearnstar.webapp.services.GenericService;

@StudentPage
@AdministratorPage
@Import(module = { "bootstrap/collapse" })
public class ComplaintsAndDiscussions {

	@SessionState
	private UserInfo userInfo;

	@Inject
	private GenericService genericService;

	@Persist
	@Property
	SolutionAssessment solutionAssessmentToDiscuss;
	@Property
	private AssessmentDiscussion runningAssessmentDiscussion;

	@Persist
	@Property
	AssessmentDiscussion newAssessmentDiscussion;

	public void onActivate() {
		if (solutionAssessmentToDiscuss != null) {
			solutionAssessmentToDiscuss = genericService.getByPK(SolutionAssessment.class,
					solutionAssessmentToDiscuss.getSolutionAssessmentId());
		}

	}

	public void onActivate(long solutionAssessmentToDiscussId) {
		solutionAssessmentToDiscuss = genericService.getByPK(SolutionAssessment.class, solutionAssessmentToDiscussId);
	}

	public boolean isYourSolution() {
		return (solutionAssessmentToDiscuss.getStudentSubmitSolution().getStudentStartedTest().getStudent().getPerson()
				.getPersonId() == userInfo.getPersonId());
	}

	public void onActionFromFileAComplaint() {
		if (isYourSolution()) {
			newAssessmentDiscussion = new AssessmentDiscussion();
			Person p = genericService.getByPK(Person.class, userInfo.getPersonId());
			newAssessmentDiscussion.setPerson(p);
			newAssessmentDiscussion.setComplaint(true);
			newAssessmentDiscussion.setPostedOn(new Date());
			newAssessmentDiscussion.setReplyTo(null);
			newAssessmentDiscussion.setSolutionEvaluation(solutionAssessmentToDiscuss);
		} else {

		}
	}

	public void onActionFromReplyTo(long replyToAssessmentDiscussionId) {
		AssessmentDiscussion replyTo = genericService.getByPK(AssessmentDiscussion.class,
				replyToAssessmentDiscussionId);
		newAssessmentDiscussion = new AssessmentDiscussion();
		Person p = genericService.getByPK(Person.class, userInfo.getPersonId());
		newAssessmentDiscussion.setPerson(p);
		newAssessmentDiscussion.setPostedOn(new Date());
		newAssessmentDiscussion.setReplyTo(replyTo);
		newAssessmentDiscussion.setSolutionEvaluation(solutionAssessmentToDiscuss);
	}

	@CommitAfter
	public void onSuccessFromNewAssessmentDiscussionForm() {
		genericService.save(newAssessmentDiscussion);
		newAssessmentDiscussion = null;
	}

	public String getCoding(AssessmentDiscussion i) {
		if (i.getReplyTo() == null) {
			return Long.toString(i.getPostedOn().getTime()) + "-";
		} else {
			return getCoding(i.getReplyTo()) + Long.toString(i.getPostedOn().getTime()) + "-";
		}
	}

	public String getIndentation() {
		int a = getCoding(runningAssessmentDiscussion).split("-").length;
		return "indentation" + a;
	}

	public List<AssessmentDiscussion> getReorderedAssessmentDiscussions() {
		List<AssessmentDiscussion> lista = solutionAssessmentToDiscuss.getAssessmentDiscussions();
		ComparatorAssessmentDiscussionByReplyTo c = new ComparatorAssessmentDiscussionByReplyTo();
		Collections.sort(lista, c);
		return lista;
	}

	public boolean isAdministrator() {
		return userInfo.isAdministrator();
	}

	@CommitAfter
	public void onActionFromDeleteDiscussion(long assessmentDiscussionId) {
		genericService.deleteByPK(AssessmentDiscussion.class, assessmentDiscussionId);
	}

}
