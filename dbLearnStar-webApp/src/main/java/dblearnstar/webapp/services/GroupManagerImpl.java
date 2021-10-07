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

package dblearnstar.webapp.services;

import java.time.LocalDate;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;

import dblearnstar.model.entities.Group;
import dblearnstar.model.entities.GroupFocusOnTest;
import dblearnstar.model.entities.GroupMember;
import dblearnstar.model.entities.Student;
import dblearnstar.model.entities.TestInstance;

public class GroupManagerImpl implements GroupManager {

	@Inject
	private Session session;

	private Session getEntityManager() {
		return session.getSession();
	}

	@Override
	public void addMemberToGroup(Student s, Group g) {
		GroupMember gm = new GroupMember();
		gm.setGroup(g);
		gm.setStudent(s);
		session.save(gm);
	}

	@Override
	public void addGroupFocusOnTest(Group g, TestInstance ti) {
		GroupFocusOnTest gft = new GroupFocusOnTest();
		gft.setGroup(g);
		gft.setTestInstance(ti);
		session.save(gft);
	}

	@Override
	public Group createGroup(String title) {
		Group g = new Group();
		if (title == null) {
			title = LocalDate.now().toString();
		}
		g.setTitle(title);
		return g;
	}

	@Override
	public List<Group> getAllGroups() {
		return UsefulMethods.castList(Group.class,
				getEntityManager().createQuery("from Group g order by g.title desc").getResultList());
	}

}
