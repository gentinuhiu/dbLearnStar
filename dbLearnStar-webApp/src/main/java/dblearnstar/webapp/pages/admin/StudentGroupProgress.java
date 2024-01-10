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

package dblearnstar.webapp.pages.admin;

import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.SelectModelFactory;
import org.slf4j.Logger;

import dblearnstar.model.entities.Group;
import dblearnstar.model.entities.GroupFocusOnTest;
import dblearnstar.model.entities.GroupMember;
import dblearnstar.model.entities.TaskInTestInstance;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.services.GenericService;
import dblearnstar.webapp.services.GroupManager;
import dblearnstar.webapp.services.PersonManager;
import dblearnstar.webapp.services.TestManager;
import dblearnstar.webapp.services.TranslationService;

@AdministratorPage
@Import(module = { "zoneUpdateEffect" })
public class StudentGroupProgress {

	@Inject
	private SelectModelFactory selectModelFactory;
	@Inject
	private Logger logger;

	@Inject
	private GenericService genericService;
	@Inject
	private TestManager testManager;
	@Inject
	private GroupManager groupManager;
	@Inject
	private PersonManager personManager;

	@Property
	@Persist
	private Group selectedGroup;
	@Property
	private GroupFocusOnTest groupFocusOnTest;
	@Property
	private TaskInTestInstance taskInTestInstance;
	@Property
	@Persist
	private GroupMember groupMember;

	@InjectComponent
	private Zone groupZone;

	public void onActivate() {
		if (selectedGroup != null) {
			selectedGroup = genericService.getByPK(Group.class, selectedGroup.getGroupId());
		}
	}

	public Object onValueChangedFromSelectGroup(Group g) {
		selectedGroup = g;
		return groupZone.getBody();
	}

	public Object getAllGroups() {
		return selectModelFactory.create(groupManager.getAllGroups(), "title");
	}

	public Float getAssessmentsForTaskForStudent() {
		return testManager.getGradeForTaskInTestInstanceByStudent(taskInTestInstance.getTaskInTestInstanceId(),
				groupMember.getStudent().getStudentId());
	}

	public int getNumTasks() {
		return groupFocusOnTest.getTestInstance().getTaskInTestInstances().size();
	}

	public Integer getResultGrade() {
		return (Integer) result[0];
	}

	public String getResultColorClass() {
		Boolean shouldPass = (Boolean) result[1];
		Boolean passed = (Boolean) result[2];

		String colorClass = " ";
		if (passed != null && passed) {
			colorClass += " result-passed ";
		} else if (passed != null && !passed) {
			colorClass += " result-notpassed ";
		} else {

		}
		if (shouldPass != null && shouldPass) {
			colorClass += " result-queryCorrect ";
		} else if (shouldPass != null && !shouldPass) {
			colorClass += " result-queryError ";
		} else {

		}
		return colorClass;
	}

	public String getGroupMemberFullNameWithId() {
		return personManager.getPersonFullName(groupMember.getStudent().getPerson());
	}

	public String getGroupMemberUserName() {
		return groupMember.getStudent().getPerson().getUserName();
	}

	public List<TaskInTestInstance> getTaskInTestInstances() {
		return testManager.getTaskInTestInstancesByTestInstance(groupFocusOnTest.getTestInstance().getTestInstanceId());
	}

	@Property
	private Object[] result;

	public List<Object[]> getTestInstanceResultsByStudentSortedByTaskName() {
		return testManager.getTestInstanceResultsByStudentSortedByTaskName(groupMember.getStudent(),
				groupFocusOnTest.getTestInstance());
	}

	public Float getGroupMemberTotalPoints() {
		Float total = (float) 0;
		for (GroupFocusOnTest gft : selectedGroup.getGroupFocusOnTests()) {
			Float points = testManager.getTotalPoints(groupMember.getStudent().getStudentId(),
					gft.getTestInstance().getTestInstanceId());
			if (points != null) {
				total += points;
			}
		}
		return total;
	}

	@Inject
	private TranslationService translationService;
	@Inject
	private PersistentLocale persistentLocale;

	public String getGroupFocusTestInstanceTitleTranslated() {
		String translated = translationService.getTranslation("TestInstance", "title",
				groupFocusOnTest.getTestInstance().getTestInstanceId(),
				persistentLocale.get().getLanguage().toLowerCase());
		return (translated != null ? translated : groupFocusOnTest.getTestInstance().getTitle());
	}

	public String getTaskInTestInstanceTaskTitleTranslated() {
		String translated = translationService.getTranslation("Task", "title", taskInTestInstance.getTask().getTaskId(),
				persistentLocale.get().getLanguage().toLowerCase());
		return (translated != null ? translated : taskInTestInstance.getTask().getTitle());
	}

}
