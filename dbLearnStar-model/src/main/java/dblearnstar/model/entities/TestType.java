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
@Table(schema = "sql_learning", name = "test_type")
public class TestType implements java.io.Serializable {
	private long testTypeId;
	private String title;
	private List<TestTemplate> testTemplates = new ArrayList<TestTemplate>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "test_type_id", unique = true, nullable = false)
	public long getTestTypeId() {
		return this.testTypeId;
	}

	public void setTestTypeId(long testTypeId) {
		this.testTypeId = testTypeId;
	}

	@Column(name = "title", unique = true, nullable = false)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "testType")
	public List<TestTemplate> getTestTemplates() {
		return this.testTemplates;
	}

	public void setTestTemplates(List<TestTemplate> testTemplates) {
		this.testTemplates = testTemplates;
	}

}
