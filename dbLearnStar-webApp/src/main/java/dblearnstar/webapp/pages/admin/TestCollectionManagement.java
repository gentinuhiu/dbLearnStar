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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.slf4j.Logger;

import dblearnstar.model.entities.TestCollection;
import dblearnstar.model.entities.TestInstance;
import dblearnstar.model.model.ComparatorTestCollection;
import dblearnstar.model.model.ComparatorTestInstance;
import dblearnstar.model.model.UserInfo;
import dblearnstar.webapp.annotations.AdministratorPage;
import dblearnstar.webapp.services.GenericService;
import dblearnstar.webapp.services.UsefulMethods;
import dblearnstar.webapp.util.AppConfig;

@AdministratorPage
public class TestCollectionManagement {
	@SessionState
	private UserInfo userInfo;

	private enum PageMode {
		NEW, UPDATE, TESTINSTANCE
	};

	@Inject
	private Logger logger;

	// The activation context

	@Property
	private Long editorTestCollectionId;
	@Property
	private TestCollection editorTestCollection;
	@Property
	private TestCollection listTestCollection;
	// @Property private TestCollection toAddTestInstanceToTestCollection;
	@Property
	private TestInstance listedTestInstance;
	@Property
	private TestInstance selectedTestInstance;

	@Property
	private Boolean toCancel;
	@Property
	private Boolean toCreateNew;
	@Property
	private TestCollection toAddTestInstance;

	@Environmental
	private ValidationTracker validationTracker;

	@Inject
	private GenericService genericService;

	@InjectComponent
	private BeanEditForm createForm;

	@Inject
	private SelectModelFactory selectModelFactory;

	void onActivate(EventContext ec) {
		if (ec.getCount() == 0) {
			editorTestCollectionId = null;
		} else if (ec.getCount() == 1) {
			toCreateNew = (ec.get(PageMode.class, 0)) == PageMode.NEW;
		} else {
			if ((ec.get(PageMode.class, 0)) == PageMode.TESTINSTANCE) {
				toAddTestInstance = genericService.getByPK(TestCollection.class, ec.get(Long.class, 1));
			} else {
				toCreateNew = false;
				editorTestCollectionId = ec.get(Long.class, 1);
			}
		}
	}

	Object[] onPassivate() {
		if (editorTestCollectionId != null) {
			return new Object[] { PageMode.UPDATE, editorTestCollectionId };
		} else if (toCreateNew != null && toCreateNew) {
			return new Object[] { PageMode.NEW };
		} else if (toAddTestInstance != null) {
			return new Object[] { PageMode.TESTINSTANCE, toAddTestInstance.getTestCollectionId() };
		} else {
			return null;
		}
	}

	public List<TestCollection> getListTestCollections() {
		List<TestCollection> list = UsefulMethods.castList(TestCollection.class,
				genericService.getAll(TestCollection.class));
		ComparatorTestCollection c = new ComparatorTestCollection();
		Collections.sort(list, c);
		return list;
	}

	public List<TestInstance> getListTestCollectionTestInstances() {
		List<TestInstance> list = listTestCollection.getTestInstances();
		ComparatorTestInstance c = new ComparatorTestInstance();
		Collections.sort(list, c);
		return list;
	}

	void setupRender() {
		if (editorTestCollectionId == null) {
			if (toCreateNew != null && toCreateNew) {
				editorTestCollection = new TestCollection();
			} else {
				editorTestCollection = null;
			}
		} else {
			if (editorTestCollection == null) {
				editorTestCollection = genericService.getByPK(TestCollection.class, editorTestCollectionId);
			}
		}
	}

	// CREATE
	/////////

	void onCreate() {
		editorTestCollectionId = null;
		toCreateNew = true;
	}

	void onPrepareForRenderFromCreateForm() throws Exception {
		if (createForm.isValid()) {
			if (toCreateNew != null && toCreateNew) {
				editorTestCollection = new TestCollection();
			} else {
				if (editorTestCollectionId != null) {
					editorTestCollection = genericService.getByPK(TestCollection.class, editorTestCollectionId);
				}
			}
		}
	}

	void onPrepareForSubmitFromCreateForm() throws Exception {
		if (toCreateNew != null && toCreateNew) {
			editorTestCollection = new TestCollection();
		} else {
			editorTestCollection = genericService.getByPK(TestCollection.class, editorTestCollectionId);
			if (editorTestCollection == null) {
				createForm.recordError("Record has been deleted by another user or process.");
				editorTestCollection = new TestCollection();
			}
		}
	}

	void onCanceled() {
		editorTestCollectionId = null;
		validationTracker.clear();
		toCancel = true;
		toAddTestInstance = null;
	}

	@CommitAfter
	void onValidateFromCreateForm() {
		if (toCancel == null || toCancel != true) {
			if (editorTestCollection.getTitle() == null) {
				createForm.recordError("Please set a title, taking into account to be a unique title.");
			}
			if (createForm.getHasErrors()) {
				return;
			}
			try {
				genericService.saveOrUpdate(editorTestCollection);
			} catch (Exception e) {
				createForm.recordError(e.getLocalizedMessage());
			}
		}
	}

	void onSuccessFromCreateForm() {
		toCreateNew = null;
		editorTestCollectionId = null;
	}

	public SelectModel getCollectionModel() {
		List<TestCollection> list = getListTestCollections();
		if (list == null) {
			return null;
		} else {
			ComparatorTestCollection c = new ComparatorTestCollection();
			Collections.sort(list, c);
			return selectModelFactory.create(list, "title");
		}
	}

	// EDIT
	/////////
	void onEdit(TestCollection tc) {
		editorTestCollectionId = tc.getTestCollectionId();
	}

	// DELETE
	/////////

	@CommitAfter
	void onDelete(TestCollection tc) {
		genericService.delete(tc);
	}

	// ADD TESTINSTANCE
	///////////////////

	void onAddTestInstance(TestCollection tc) {
		toAddTestInstance = tc;
		toCreateNew = null;
	}

	@CommitAfter
	void onValidateFromChooseTestInstance() {
		if (toCancel == null || toCancel != true) {
			try {
				selectedTestInstance.setTestCollection(toAddTestInstance);
				genericService.saveOrUpdate(selectedTestInstance);
			} catch (Exception e) {
				createForm.recordError(e.getLocalizedMessage());
			}
		}
	}

	void onSuccessFromChooseTestInstance() {
		toAddTestInstance = null;
	}

	@CommitAfter
	void onRemoveTestInstance(TestInstance ti) {
		ti.setTestCollection(null);
		genericService.saveOrUpdate(ti);
	}

	// OTHERS
	public Format getDateFormat() {
		return new SimpleDateFormat(AppConfig.getString("date.gui.format"));
	}

	public int recurseLevels(TestCollection tc) {
		if (tc.getParentCollection() == null) {
			return 1;
		} else {
			return recurseLevels(tc.getParentCollection()) + 1;
		}
	}

	public Integer getIndentLevel() {
		return recurseLevels(listTestCollection);
	}

	public SelectModel getModelTestInstance() {
		List<TestInstance> list = (UsefulMethods.castList(TestInstance.class,
				genericService.getAll(TestInstance.class))).stream().filter(ti -> ti.getTestCollection() == null)
						.collect(Collectors.toList());
		ComparatorTestInstance c = new ComparatorTestInstance();
		Collections.sort(list, c);
		return selectModelFactory.create(list, "title");
	}

}
