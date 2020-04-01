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
import java.util.List;

public class CompareResult implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final ArrayList<Diff> diffs;

	private final String databaseA;
	private final String databaseB;

	public CompareResult(String databaseA, String databaseB)
	{
		this.databaseA = databaseA;
		this.databaseB = databaseB;

		this.diffs = new ArrayList<>();
	}

	public void addDiff(Diff diff)
	{
		diffs.add(diff);
	}

	public String getDatabaseA()
	{
		return databaseA;
	}

	public String getDatabaseB()
	{
		return databaseB;
	}

	public List<Diff> getDiffs()
	{
		return diffs;
	}

	public static class Diff implements Serializable
	{
		private static final long serialVersionUID = 1L;

		public enum Type
		{
			TABLE,
			FIELD,
			KEY,
			PROCEDURE,
		}

		public enum Mode
		{
			EQUAL,
			CHILDREN_DIFFER,
			LEFT_ONLY,
			RIGHT_ONLY,
			DIFFERENT
		}

		private final Type type;
		private final Mode mode;

		public Diff(Type type, Mode mode)
		{
			this.type = type;
			this.mode = mode;
		}

		public Type getType()
		{
			return type;
		}

		public Mode getMode()
		{
			return mode;
		}
	}

	public static class TableDiff extends Diff
	{
		private static final long serialVersionUID = 1L;

		private final TableInfo tableInfoA;
		private final TableInfo tableInfoB;

		public TableDiff(Mode type, TableInfo tableInfoA, TableInfo tableInfoB)
		{
			super(Type.TABLE, type);

			this.tableInfoA = tableInfoA;
			this.tableInfoB = tableInfoB;
		}

		public TableInfo getTableInfoA()
		{
			return tableInfoA;
		}

		public TableInfo getTableInfoB()
		{
			return tableInfoB;
		}
	}

	public static class FieldDiff extends Diff
	{
		private static final long serialVersionUID = 1L;

		private final FieldInfo fieldInfoA;
		private final FieldInfo fieldInfoB;
		private final boolean simpleEquals;
		private final boolean typeEquals;
		private final boolean collationEquals;

		public FieldDiff(Mode type, FieldInfo fieldInfoA, FieldInfo fieldInfoB)
		{
			super(Type.FIELD, type);

			this.fieldInfoA = fieldInfoA;
			this.fieldInfoB = fieldInfoB;

			if (fieldInfoA != null && fieldInfoB != null)
			{
				this.simpleEquals = fieldInfoA.simpleEquals(fieldInfoB);
				this.typeEquals = fieldInfoA.typeEquals(fieldInfoB);
				this.collationEquals = fieldInfoA.collationEquals(fieldInfoB);
			}
			else
			{
				this.simpleEquals = false;
				this.typeEquals = false;
				this.collationEquals = false;
			}
		}

		public FieldInfo getFieldInfoA()
		{
			return fieldInfoA;
		}

		public FieldInfo getFieldInfoB()
		{
			return fieldInfoB;
		}

		public boolean isSimpleEquals()
		{
			return simpleEquals;
		}

		public boolean isTypeEquals()
		{
			return typeEquals;
		}

		public boolean isCollationEquals()
		{
			return collationEquals;
		}
	}

	public static class KeyDiff extends Diff
	{
		private static final long serialVersionUID = 1L;

		private final KeyInfo keyInfoA;
		private final KeyInfo keyInfoB;

		public KeyDiff(Diff.Mode type, KeyInfo keyInfoA, KeyInfo keyInfoB)
		{
			super(Type.KEY, type);

			this.keyInfoA = keyInfoA;
			this.keyInfoB = keyInfoB;
		}

		public KeyInfo getKeyInfoA()
		{
			return keyInfoA;
		}

		public KeyInfo getKeyInfoB()
		{
			return keyInfoB;
		}
	}

	public static class ProcedureDiff extends Diff
	{
		private static final long serialVersionUID = 1L;

		private final ProcedureInfo procedureInfoA;
		private final ProcedureInfo procedureInfoB;

		public ProcedureDiff(Diff.Mode type, ProcedureInfo tableInfoA, ProcedureInfo tableInfoB)
		{
			super(Diff.Type.PROCEDURE, type);

			this.procedureInfoA = tableInfoA;
			this.procedureInfoB = tableInfoB;
		}

		public ProcedureInfo getProcedureInfoA()
		{
			return procedureInfoA;
		}

		public ProcedureInfo getProcedureInfoB()
		{
			return procedureInfoB;
		}
	}

}
