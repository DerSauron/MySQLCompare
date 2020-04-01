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

public class ProcedureInfo implements NamedObject
{
	private final String name;
	private final String type;
	private final String createStatement;
	private String cleanCreateStatement = null;

	public ProcedureInfo(String name, String type, String createStatement)
	{
		this.name = name;
		this.type = type;
		this.createStatement = createStatement
			.replaceAll("DEFINER\\s*=\\s*`?[^`]+`?@`?[^`]+`?\\s*", "");
	}

	@Override
	public String getName()
	{
		return name;
	}

	public String getType()
	{
		return type;
	}

	public String getCreateStatement()
	{
		return createStatement;
	}

	private String getCleanCreateStatement()
	{
		if (cleanCreateStatement == null)
		{
			cleanCreateStatement = createStatement
				.replaceAll("[\\s]+", " ")
				.toLowerCase();
		}

		return cleanCreateStatement;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.name);
		hash = 29 * hash + Objects.hashCode(this.type);
		hash = 17 * hash + Objects.hashCode(this.getCleanCreateStatement());
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
		final ProcedureInfo other = (ProcedureInfo)obj;
		if (!Objects.equals(this.name, other.name))
		{
			return false;
		}
		if (!Objects.equals(this.type, other.type))
		{
			return false;
		}
		if (!Objects.equals(this.getCleanCreateStatement(), other.getCleanCreateStatement()))
		{
			return false;
		}
		return true;
	}
}
