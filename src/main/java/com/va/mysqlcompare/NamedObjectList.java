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
package com.va.mysqlcompare;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

class NamedObjectList<T extends NamedObject> implements Serializable, Iterable<T>
{
	private static final long serialVersionUID = 1L;
	private final ArrayList<T> fields;
	private final HashMap<String, T> map;

	public NamedObjectList()
	{
		this.fields = new ArrayList<>();
		this.map = new HashMap<>();
	}

	public void add(T fieldInfo)
	{
		fields.add(fieldInfo);
		map.put(fieldInfo.getName().toLowerCase(), fieldInfo);
	}

	@Override
	public Iterator<T> iterator()
	{
		return fields.iterator();
	}

	public boolean contains(String fieldName)
	{
		return map.containsKey(fieldName.toLowerCase());
	}

	public T get(String fieldName)
	{
		return map.get(fieldName.toLowerCase());
	}
}
