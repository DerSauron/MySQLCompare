/*
 * Copyright (C) 2020 Daniel Volk <mail@volkarts.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.va.securestore;

import java.util.List;

public interface DataStore
{
	boolean open();

	boolean persist();

	void close();

	List<String> keys();
	List<String> keys(String prefix);

	void putData(String key, String string);

	void putData(String key, int num);

	void putData(String key, byte[] data);

	String getString(String key);
	String getString(String key, String def);

	int getInt(String key);
	int getInt(String key, int def);

	byte[] getBytes(String key);
	byte[] getBytes(String key, byte[] def);
}
