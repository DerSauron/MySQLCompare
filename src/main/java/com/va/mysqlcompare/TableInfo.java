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

import java.util.Objects;

public class TableInfo implements NamedObject
{
	private final String name;
	private final String createStatement;
	private final String engine;
	private final String charset;
	private final String collation;

	public TableInfo(String name, String createStatement, String engine, String charset, String collation)
	{
		this.name = name;
		this.createStatement = createStatement;
		this.engine = engine;
		this.charset = charset;
		this.collation = collation;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public String getCreateStatement()
	{
		return createStatement;
	}

	public String getEngine()
	{
		return engine;
	}

	public String getCharset()
	{
		return charset;
	}

	public String getCollation()
	{
		return collation;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 61 * hash + Objects.hashCode(this.name);
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final TableInfo other = (TableInfo)obj;
		return Objects.equals(this.name, other.name)
			&& Objects.equals(this.engine, other.engine)
			&& Objects.equals(this.charset, other.charset)
			&& Objects.equals(this.collation, other.collation);
	}
}
