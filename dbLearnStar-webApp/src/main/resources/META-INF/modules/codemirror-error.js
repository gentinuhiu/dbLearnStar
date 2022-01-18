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

define(["jquery", "bootstrap/modal"], function($) {

	var positionToError = function(errorPosition) {
		line = window.editor.posFromIndex(errorPosition).line;
		col = window.editor.posFromIndex(errorPosition).ch;
		if (col > 1)
			line++;
		realposition = window.editor.posFromIndex(errorPosition - line);
		window.editor.focus();
		window.editor.setCursor(realposition);
	}

	return {
		positionToError: positionToError
	}

});
