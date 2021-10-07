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

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.commons.MappedConfiguration;

/**
 * This module is automatically included as part of the Tapestry IoC Registry if
 * <em>tapestry.execution-mode</em> includes <code>development</code>.
 */
public class DevelopmentModule {
	public static void contributeApplicationDefaults(MappedConfiguration<String, Object> configuration) {
		configuration.add(SymbolConstants.PRODUCTION_MODE, false);
		configuration.add(SymbolConstants.APPLICATION_VERSION, "0.6-SNAPSHOT-T5.6.1");
	}
}
