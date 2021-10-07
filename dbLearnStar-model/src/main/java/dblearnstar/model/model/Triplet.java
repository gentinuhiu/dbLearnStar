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

package dblearnstar.model.model;

public class Triplet<T1, T2, T3> {

	private T1 firstItem;
	private T2 secondItem;
	private T3 thirdItem;

	public T3 getThirdItem() {
		return thirdItem;
	}

	public void setThirdItem(T3 thirdItem) {
		this.thirdItem = thirdItem;
	}

	public T1 getFirstItem() {
		return firstItem;
	}

	public void setFirstItem(T1 firstItem) {
		this.firstItem = firstItem;
	}

	public T2 getSecondItem() {
		return secondItem;
	}

	public Triplet(T1 firstItem, T2 secondItem, T3 thirdItem) {
		super();
		this.firstItem = firstItem;
		this.secondItem = secondItem;
		this.thirdItem = thirdItem;
	}

	public void setSecondItem(T2 secondItem) {
		this.secondItem = secondItem;
	}

}
