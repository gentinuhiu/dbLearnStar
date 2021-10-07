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

requirejs.config({
	shim: {
		'ckeditor-jquery': ['jquery', 'ckeditor-core']
	},
	paths: {
		'ckeditor-core': '../webjars/ckeditor/4.16.1/standard/ckeditor',
		'ckeditor-jquery': '../webjars/ckeditor/4.16.1/standard/adapters/jquery'
	}
});

define(["jquery", "ckeditor-jquery"], function($) {
	init = function(spec) {
		$('#' + spec.id).ckeditor(function() {
		},
			{
				specialChars: ['σ', 'θ', 'π', '⨯', '⋈', 'ρ', '÷', '&cup;', '&cap;',
					'←', '→', '∨', '∧', '¬', '∀', '∃', '∄', '∈', '∉', '⊂', '⊃',
					'⊆', '⊇', '≤', '≠', '≥'],
				toolbarGroups: [
					{ "name": "basicstyles", "groups": ["basicstyles"] },
					{ "name": "paragraph", "groups": ["list"] },
					{ "name": "insert", "groups": ["insert"] },
					{ "name": "styles", "groups": ["styles"] },
					{ "name": "document", "groups": ["mode"] }
				],
				stylesSet: [
					{
						name: 'Box',
						element: 'div',
						attributes: { 'class': 'box' }
					}
				],
				removeButtons: 'Anchor,Strike,blockquote',
				contentsCss: [spec.csspath],
				baseFloatZIndex: 40000
			});
	};
	return init;
}); 
