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

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.slf4j.Logger;

import dblearnstar.model.entities.Group;
import dblearnstar.model.entities.GroupFocusOnTest;
import dblearnstar.model.entities.GroupMember;
import dblearnstar.model.entities.Person;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.model.StudentSelectModel;
import dblearnstar.webapp.services.GenericService;
import dblearnstar.webapp.services.GroupManager;
import dblearnstar.webapp.services.PersonManager;
import dblearnstar.webapp.services.TestManager;
import dblearnstar.webapp.services.UsefulMethods;

@AdministratorPage
public class GroupManagement {

	@Inject
	private Logger logger;
	@Inject
	private GroupManager groupManager;
	@Inject
	private GenericService genericService;
	@Inject
	private PersonManager personManager;
	@Inject
	private TestManager testManager;
	@Inject
	private SelectModelFactory selectModelFactory;

	@Property
	private Group group;
	@Property
	@Persist
	private Group selectedGroup;

	@Property
	@Persist
	private Group editGroup;
	@Property
	@Persist
	private GroupMember editGroupMember;
	@Property
	@Persist
	private GroupFocusOnTest editGroupFocusOnTest;
	@Property
	private GroupMember groupMember;
	@Property
	private GroupFocusOnTest groupFocusOnTest;
	@Property
	private Person personToAdd;

	public void onActivate() {
		if (editGroup != null) {
			Group g = genericService.getByPK(Group.class, editGroup.getGroupId());
			if (g != null) {
				editGroup = g;
			}
		}
	}

	public StudentSelectModel getAllStudentsModel() {
		StudentSelectModel ssm = new StudentSelectModel(personManager.getAllStudents());
		return ssm;
	}

	public List<Group> getAllGroups() {
		return UsefulMethods.castList(Group.class, genericService.getAll(Group.class));
	}

	public void onActionFromNewGroup() {
		editGroup = new Group();
	}

	public void onActionFromEdit(Group g) {
		editGroup = g;
	}

	@CommitAfter
	public void onSuccessFromNewGroupForm() {
		logger.info("Submitting new group");
		if (editGroupMember != null) {
			genericService.saveOrUpdate(editGroupMember);
		}
		genericService.saveOrUpdate(editGroup);
		editGroupMember = null;
		editGroup = null;
	}

	@CommitAfter
	public void onSuccessFromNewMemberForm() {
		logger.info("Submitting new member");
		genericService.saveOrUpdate(editGroupMember);
		genericService.saveOrUpdate(editGroup);
		editGroupMember = null;
	}

	@CommitAfter
	public void onSuccessFromNewFocusOnTestForm() {
		logger.info("Submitting new focus on test");
		genericService.saveOrUpdate(editGroupFocusOnTest);
		genericService.saveOrUpdate(editGroup);
		editGroupFocusOnTest = null;
	}

	void onActionFromNewMember() {
		editGroupMember = new GroupMember();
		editGroup.getGroupMembers().add(editGroupMember);
		editGroupMember.setGroup(editGroup);
	}

	void onActionFromNewFocusOnTest() {
		editGroupFocusOnTest = new GroupFocusOnTest();
		editGroup.getGroupFocusOnTests().add(editGroupFocusOnTest);
		editGroupFocusOnTest.setGroup(editGroup);
	}

	void onRemoveRowFromGroupMembers(GroupMember groupMember) {
		logger.info("Submitting");
		genericService.delete(groupMember);
	}

	@Inject
	private BeanModelSource beanModelSource;

	@Inject
	private Messages messages;

	@Property
	@Persist
	private BeanModel<GroupMember> modelGroupMember;
	@Property
	@Persist
	private BeanModel<GroupFocusOnTest> modelGroupFocusOnTest;

	void setupRender() {
		modelGroupMember = beanModelSource.createEditModel(GroupMember.class, messages);
		modelGroupMember.add("student");
		modelGroupMember.exclude("groupMemberId");

		modelGroupFocusOnTest = beanModelSource.createEditModel(GroupFocusOnTest.class, messages);
		modelGroupFocusOnTest.add("testInstance");
		modelGroupFocusOnTest.exclude("groupFocusOnTestId");
	}

	public SelectModel getAllTestInstancesModel() {
		return selectModelFactory.create(testManager.getAllTestInstances(), "title");
	}

	@CommitAfter
	void onActionFromDeleteGroupMember(GroupMember gm) {
		genericService.delete(gm);
	}

	@CommitAfter
	void onActionFromDeleteGroupFocusOnTest(GroupFocusOnTest gfon) {
		genericService.delete(gfon);
	}

	@CommitAfter
	void onActionFromDelete(Group g) {
		if ((g.getGroupMembers() == null || g.getGroupMembers().size() == 0)
				&& (g.getGroupFocusOnTests().size() == 0 || g.getGroupFocusOnTests() == null)) {
			genericService.delete(g);
		}
	}
}
