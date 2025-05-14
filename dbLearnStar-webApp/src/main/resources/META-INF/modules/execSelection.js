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

define(["jquery", "t5/core/ajax"], function($, ajax) {

	$(document).keydown(function(e) {
		if (e.ctrlKey && e.which == 13) {
			$('#RunOnlySelection').click();
			//event.preventDefault();
			return false;
		}
	}
	);

	function getEditorText(onlySelection) {
		tekst = "";
		if (typeof window.editor === 'undefined') {
			tekst = "NOEDITOR";
			if (typeof CKEDITOR === 'undefined') {
				tekst = "NOCMEDNOCKED";
			} else {
				var editorarea = CKEDITOR.instances['editorarea'];
				if (typeof editorarea === 'undefined') {
					tekst = "OTHERCK";
				} else {
					tekst = editorarea.getData();
				}
			}
		} else {
			// CodeMirror
			if (onlySelection) {
				tekst = window.editor.getSelection();
			} else {
				tekst = window.editor.getValue();
			}
		}

		if (tekst == null) tekst = "NULL";
		if (tekst == "") tekst = "NULL";

		return tekst;
	}

	$('#RunOnlySelection').click(function() {
		tekst = getEditorText(true);

		ajax("execSelection", {
			element: null,
			data: {
				"query": tekst,
				"issuer": "btnRunOnlySelection"
			},
			method: "POST"
		});
	});

	$('#RunOnly').click(function() {
		tekst = getEditorText(false);

		ajax("execAll", {
			element: null,
			data: {
				"query": tekst,
				"issuer": "btnRunOnly"
			},
			method: "POST"
		});
	});

	$('#Evaluate').click(function() {
		tekst = getEditorText(false);

		ajax("evalAll", {
			element: null,
			data: {
				"query": tekst,
				"issuer": "btnEvaluate"
			},
			method: "POST"
		});
	});

	$('#PlanOnly').click(function() {
		tekst = getEditorText(false);

		ajax("plan", {
			element: null,
			data: {
				"query": tekst,
				"issuer": "btnPlanOnly"
			},
			method: "POST"
		});
	});

});
