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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class KeyInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final String tableName;
	private final String name;
	private final boolean unique;
	private final ArrayList<KeyField> fields;

	public KeyInfo(ResultSet result) throws SQLException
	{
		this.tableName = result.getString("Table");
		this.name = result.getString("Key_name");
		this.unique = !result.getBoolean("Non_unique");
		this.fields = new ArrayList<>();
		fillFields(result);
	}

	private void fillFields(ResultSet result) throws SQLException
	{
		while (!result.isAfterLast() && name.equals(result.getString("Key_name")))
		{
			String fieldName = result.getString("Column_name");
			int keyLength;
			try
			{
				keyLength = Integer.parseInt(result.getString("Sub_part"));
			}
			catch (NumberFormatException e)
			{
				keyLength = -1;
			}
			fields.add(new KeyField(fieldName, keyLength));
			result.next();
		}
		result.previous();
	}

	public String getTableName()
	{
		return tableName;
	}

	public String getName()
	{
		return name;
	}

	public boolean isUnique()
	{
		return unique;
	}

	public ArrayList<KeyField> getFields()
	{
		return fields;
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 53 * hash + Objects.hashCode(this.name == null ? null : this.name.toLowerCase());
		hash = 53 * hash + (this.unique ? 1 : 0);
		hash = 53 * hash + Objects.hashCode(this.fields);
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
		final KeyInfo other = (KeyInfo)obj;
		if (!Objects.equals(this.name, other.name))
		{
			if ((this.name == null) || !this.name.equalsIgnoreCase(other.name))
			{
				return false;
			}
		}
		if (this.unique != other.unique)
		{
			return false;
		}
		if (this.fields.size() == other.fields.size())
		{
			HashSet<KeyField> intersect = new HashSet<>(this.fields);
			intersect.retainAll(other.fields);
			if (intersect.size() != this.fields.size())
			{
				return false;
			}
		}
		else
		{
			return false;
		}
		return true;
	}

	public static class KeyField
	{
		private final String name;
		private final int length;

		public KeyField(String name, int length)
		{
			this.name = name;
			this.length = length;
		}

		public String getName()
		{
			return name;
		}

		public int getLength()
		{
			return length;
		}

		@Override
		public String toString()
		{
			return name + ((length > 0) ? " (" + length + ")" : "");
		}

		@Override
		public int hashCode()
		{
			int hash = 7;
			hash = 83 * hash + Objects.hashCode(this.name);
			hash = 83 * hash + this.length;
			return hash;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			final KeyField other = (KeyField)obj;
			if (this.length != other.length)
			{
				return false;
			}
			if (!Objects.equals(this.name, other.name))
			{
				return false;
			}
			return true;
		}

	}
}
