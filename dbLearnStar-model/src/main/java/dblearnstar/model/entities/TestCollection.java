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
@Table(schema = "sql_learning", name = "test_collection")
public class TestCollection implements java.io.Serializable {
	private long testCollectionId;
	private String title;
	private Integer ordering;
	private TestCollection parentCollection;
	private List<TestCollection> subCollections = new ArrayList<TestCollection>();
	private List<TestInstance> testInstances = new ArrayList<TestInstance>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "test_collection_id", unique = true, nullable = false)
	public long getTestCollectionId() {
		return this.testCollectionId;
	}

	public void setTestCollectionId(long testCollectionId) {
		this.testCollectionId = testCollectionId;
	}

	@Column(name = "title", unique = true, nullable = false)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "ordering")
	public Integer getOrdering() {
		return this.ordering;
	}

	public void setOrdering(Integer ordering) {
		this.ordering = ordering;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_collection_id", nullable = true, foreignKey = @ForeignKey(name = "fk_test_collection_test_collection"))
	public TestCollection getParentCollection() {
		return this.parentCollection;
	}

	public void setParentCollection(TestCollection parentCollection) {
		this.parentCollection = parentCollection;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parentCollection")
	public List<TestCollection> getSubCollections() {
		return this.subCollections;
	}

	public void setSubCollections(List<TestCollection> subCollections) {
		this.subCollections = subCollections;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "testCollection")
	public List<TestInstance> getTestInstances() {
		return this.testInstances;
	}

	public void setTestInstances(List<TestInstance> testInstances) {
		this.testInstances = testInstances;
	}

}
