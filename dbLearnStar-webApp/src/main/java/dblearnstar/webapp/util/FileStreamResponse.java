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

package dblearnstar.webapp.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.http.services.Response;

public class FileStreamResponse implements StreamResponse {
	private InputStream is;
	private String filename = "download.dat";

	public FileStreamResponse(InputStream is, String... args) {
		this.is = is;
		if (args != null) {
			this.filename = args[0];
		}
	}

	@Override
	public String getContentType() {
		return "application/octet-stream";
	}

	@Override
	public InputStream getStream() throws IOException {
		return is;
	}

	@Override
	public void prepareResponse(Response arg0) {
		arg0.setHeader("Content-Disposition", "attachment; filename=" + filename);
	}

}
