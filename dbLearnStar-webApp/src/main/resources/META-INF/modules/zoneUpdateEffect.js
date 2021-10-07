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

define([ "jquery", "t5/core/dom", "t5/core/events" ], function($, dom, events) {

	$('*[data-container-type="zone"]').on(events.zone.didUpdate, function() {
		$(this).css("animation", "0.3s linear slidein");
	});

	$('*[data-container-type="zone"]').on("animationend", function() {
		$(this).css("animation", "none");
		$(this).offsetHeight;
		$(this).css("animation", "null");
	});

	//return null;
});
