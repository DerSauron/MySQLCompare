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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldInfo implements NamedObject, Serializable
{
	public enum GenerationType
	{
		NONE,
		STORED,
		VIRTUAL,
	}

	private static final long serialVersionUID = 1L;
	private final String tableName;
	private final String name;
	private final String previousFieldName;
	private String type;
	private Integer length;
	private final String collation;
	private final boolean nullValue;
	private final String defaultValue;
	private final boolean autoIncrement;
	private final GenerationType generationType;
	private final String generationExpression;
	private final boolean generationUniqueKey;

	public FieldInfo(String tableName, ResultSet result, String previousFieldName) throws SQLException
	{
		this.tableName = tableName;
		name = result.getString("COLUMN_NAME");
		this.previousFieldName = previousFieldName;

		decodeType(result.getString("COLUMN_TYPE"));
		collation = result.getString("COLLATION_NAME");
		nullValue = "YES".equalsIgnoreCase(result.getString("IS_NULLABLE"));
		defaultValue = result.getString("COLUMN_DEFAULT");
		autoIncrement = getExtraFlag(result.getString("EXTRA"), "auto_increment") != -1;

		final int generationFlag = getExtraFlag(result.getString("EXTRA"), "STORED GENERATED", "VIRTUAL GENERATED");
		if (generationFlag != -1)
		{
			generationType = GenerationType.values()[generationFlag + 1];
			generationExpression = result.getString("GENERATION_EXPRESSION");
			generationUniqueKey = getExtraFlag(result.getString("COLUMN_KEY"), "UNI") != -1;
		}
		else
		{
			generationType = GenerationType.NONE;
			generationExpression = null;
			generationUniqueKey = false;
		}
	}

	private void decodeType(String type0) throws SQLException
	{
		Pattern pattern = Pattern.compile("([a-z]+)(\\((\\d+)\\))?", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(type0);
		if (matcher.find())
		{
			type = matcher.group(1).toLowerCase();
			try
			{
				if (matcher.group(3) != null)
				{
					length = Integer.parseInt(matcher.group(3));
				}
				else
				{
					length = null;
				}
			}
			catch (NumberFormatException e)
			{
				throw new SQLException("Could not decode type string `" + type0 + "`", e);
			}
		}
		else
		{
			throw new SQLException("Could not decode type string `" + type0 + "`");
		}
	}

	private int getExtraFlag(String extra, String... flags)
	{
		if (extra == null)
			return -1;

		int i = 0;
		for (String flag : flags)
		{
			if (extra.contains(flag))
				return i;
		}

		return -1;
	}

	public String getTableName()
	{
		return tableName;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public String getPreviousFieldName()
	{
		return previousFieldName;
	}

	public String getType()
	{
		return type;
	}

	public String getCollation()
	{
		return collation;
	}

	public Integer getLength()
	{
		return length;
	}

	public boolean isNull()
	{
		return nullValue;
	}

	public String getDefault()
	{
		return defaultValue;
	}

	public boolean isAutoIncrement()
	{
		return autoIncrement;
	}

	public GenerationType getGenerationType()
	{
		return generationType;
	}

	public String getGenerationExpression()
	{
		return generationExpression;
	}

	public boolean isGenerationUniqueKey()
	{
		return generationUniqueKey;
	}

	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 17 * hash + Objects.hashCode(this.name == null ? null : this.name.toLowerCase());
		hash = 17 * hash + Objects.hashCode(this.type);
		hash = 17 * hash + Objects.hashCode(this.length);
		hash = 17 * hash + Objects.hashCode(this.collation);
		hash = 17 * hash + (this.nullValue ? 1 : 0);
		hash = 17 * hash + Objects.hashCode(this.defaultValue);
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
		final FieldInfo other = (FieldInfo)obj;

		return collationEquals(other) && typeEquals(other) && simpleEquals(other);
	}

	public boolean simpleEquals(FieldInfo other)
	{
		if (!Objects.equals(this.name, other.name))
		{
			if ((this.name == null) || !this.name.equalsIgnoreCase(other.name))
			{
				return false;
			}
		}
		if (this.nullValue != other.nullValue)
		{
			return false;
		}
		return Objects.equals(this.defaultValue, other.defaultValue);
	}

	public boolean collationEquals(FieldInfo other)
	{
		return Objects.equals(this.collation, other.collation);
	}

	public boolean typeEquals(FieldInfo other)
	{
		return Objects.equals(this.type, other.type)
			&& Objects.equals(this.length, other.length);
	}
}
