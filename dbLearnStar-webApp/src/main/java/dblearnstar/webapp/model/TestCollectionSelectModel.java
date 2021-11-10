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

package dblearnstar.webapp.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;

import dblearnstar.model.entities.TestCollection;

public class TestCollectionSelectModel extends AbstractSelectModel {
	private List<TestCollection> testCollections;

	public TestCollectionSelectModel(List<TestCollection> testCollections) {
		if (testCollections == null) {
			this.testCollections = new ArrayList<TestCollection>();
		} else {
			this.testCollections = testCollections;
		}
	}

	@Override
	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public String getPrefix(TestCollection i) {
		if (i.getParentCollection() == null) {
			return "";
		} else {
			return getPrefix(i.getParentCollection()) + "  "; // unicode double em-space
		}
	}

	@Override
	public List<OptionModel> getOptions() {
		List<OptionModel> options = new ArrayList<OptionModel>();
		for (TestCollection tc : testCollections) {
			String name = getPrefix(tc) + tc.getTitle();
			options.add(new OptionModelImpl(name, tc));
		}
		return options;
	}
}
