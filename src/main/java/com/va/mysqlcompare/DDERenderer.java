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

import com.va.mysqlcompare.CompareResult.Diff;
import com.va.mysqlcompare.CompareResult.FieldDiff;
import com.va.mysqlcompare.CompareResult.KeyDiff;
import com.va.mysqlcompare.CompareResult.ProcedureDiff;
import com.va.mysqlcompare.CompareResult.TableDiff;
import com.va.mysqlcompare.CompareResult.ViewDiff;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.va.mysqlcompare.CompareResult.Diff.Mode.DIFFERENT;
import static com.va.mysqlcompare.CompareResult.Diff.Mode.LEFT_ONLY;
import static com.va.mysqlcompare.CompareResult.Diff.Mode.RIGHT_ONLY;

public class DDERenderer
{
	private static final Logger LOG = LoggerFactory.getLogger(DDERenderer.class);

	private final List<CompareResult.Diff> differences;

	public DDERenderer(List<CompareResult.Diff> differences)
	{
		this.differences = differences;
	}

	public void render(OutputWriter writer)
	{
		render(writer, false);
	}

	public void render(OutputWriter writer, boolean reverse)
	{
		differences.forEach((diff) ->
		{
			renderSingle(diff, writer, reverse);
		});
	}

	public void renderSingle(Diff diff, OutputWriter writer)
	{
		renderSingle(diff, writer, false);
	}

	public void renderSingle(Diff diff, OutputWriter writer, boolean reverse)
	{
		switch (diff.getType())
		{
			case TABLE:
				renderTableDiff(writer, (TableDiff)diff, reverse);
				break;
			case VIEW:
				renderViewDiff(writer, (ViewDiff)diff, reverse);
				break;
			case FIELD:
				renderFieldDiff(writer, (FieldDiff)diff, reverse);
				break;
			case KEY:
				renderKeyDiff(writer, (KeyDiff)diff, reverse);
				break;
			case PROCEDURE:
				renderProcedureDiff(writer, (ProcedureDiff)diff, reverse);
				break;
		}
	}

	private void renderTableDiff(OutputWriter writer, TableDiff tableDiff, boolean reverse)
	{
		switch (tableDiff.getMode())
		{
			case DIFFERENT:
				renderTableOptions(writer, tableDiff, reverse);
				break;
			case LEFT_ONLY:
			{
				writer.println(reverse
					? "DROP TABLE `" + tableDiff.getTableInfoA().getName() + "`;"
					: tableDiff.getTableInfoA().getCreateStatement() + ";");
				break;
			}
			case RIGHT_ONLY:
				writer.println(reverse
					? tableDiff.getTableInfoB().getCreateStatement() + ";"
					: "DROP TABLE `" + tableDiff.getTableInfoB().getName() + "`;");
				break;
		}
	}

	private void renderTableOptions(OutputWriter writer, TableDiff tableDiff, boolean reverse)
	{
		TableInfo t1;
		TableInfo t2;

		if (!reverse)
		{
			t1 = tableDiff.getTableInfoA();
			t2 = tableDiff.getTableInfoB();
		}
		else
		{
			t1 = tableDiff.getTableInfoB();
			t2 = tableDiff.getTableInfoA();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE `").append(t1.getName()).append("` ");

		if (!t1.getEngine().equals(t2.getEngine()))
		{
			sb.append("ENGINE=").append(t1.getEngine());
		}

		if (!t1.getCharset().equals(t2.getCharset()))
		{
			sb.append("CHARSET=").append(t1.getCharset());
		}

		if (!t1.getCollation().equals(t2.getCollation()))
		{
			sb.append("COLLATE=").append(t1.getCollation());
		}

		sb.append(";");

		writer.println(sb.toString());
	}

	private void renderViewDiff(OutputWriter writer, ViewDiff viewDiff, boolean reverse)
	{
		switch (viewDiff.getMode())
		{
			case LEFT_ONLY:
				renderViewInfo(writer, viewDiff.getViewInfoA(), reverse);
				break;

			case RIGHT_ONLY:
				renderViewInfo(writer, viewDiff.getViewInfoB(), !reverse);
				break;

			case DIFFERENT:
			{
				ViewInfo viewInfo
					= !reverse ? viewDiff.getViewInfoA() : viewDiff.getViewInfoB();
				renderViewInfo(writer, viewInfo, true);
				renderViewInfo(writer, viewInfo, false);
				break;
			}
		}
	}

	private void renderViewInfo(OutputWriter writer, ViewInfo viewDiff, boolean drop)
	{
		if (drop)
		{
			writer.println("DROP VIEW `" + viewDiff.getName() + "`;");
		}
		else
		{
			writer.println(viewDiff.getCreateStatement() + ";");
		}
	}

	private void renderFieldDiff(OutputWriter writer, FieldDiff fieldDiff, boolean reverse)
	{
		switch (fieldDiff.getMode())
		{
			case LEFT_ONLY:
			{
				StringBuilder query = new StringBuilder();
				query.append("ALTER TABLE `")
					.append(fieldDiff.getFieldInfoA().getTableName());
				if (reverse)
				{
					query.append("` DROP COLUMN `")
						.append(fieldDiff.getFieldInfoA().getName())
						.append("`");
				}
				else
				{
					query.append("` ADD COLUMN ")
						.append(buildColumnDef(fieldDiff.getFieldInfoA(), true));
				}
				query.append(";");
				writer.println(query.toString());
				break;
			}
			case RIGHT_ONLY:
			{
				StringBuilder query = new StringBuilder();
				query.append("ALTER TABLE `")
					.append(fieldDiff.getFieldInfoB().getTableName());
				if (reverse)
				{
					query.append("` ADD COLUMN ")
						.append(buildColumnDef(fieldDiff.getFieldInfoB(), true));
				}
				else
				{
					query.append("` DROP COLUMN `")
						.append(fieldDiff.getFieldInfoB().getName())
						.append("`");
				}
				query.append(";");
				writer.println(query.toString());
				break;
			}
			case DIFFERENT:
			{
				StringBuilder query = new StringBuilder();
				query.append("ALTER TABLE `")
					.append(fieldDiff.getFieldInfoA().getTableName())
					.append("` MODIFY COLUMN ");
				if (reverse)
				{
					query.append(buildColumnDef(fieldDiff.getFieldInfoB(), true));
				}
				else
				{
					query.append(buildColumnDef(fieldDiff.getFieldInfoA(), true));
				}
				query.append(";");
				writer.println(query.toString());
				break;
			}
		}
	}

	private String buildColumnDef(FieldInfo fieldInfo, boolean singleTerm)
	{
		StringBuilder colDef = new StringBuilder();
		colDef.append("`")
			.append(fieldInfo.getName())
			.append("` ")
			.append(fieldInfo.getType());

		if (fieldInfo.getLength() != null)
		{
			colDef.append("(").append(fieldInfo.getLength()).append(")");
		}
		colDef.append(" ");

		if (fieldInfo.getCollation() != null)
		{
			colDef.append("COLLATE ").append(fieldInfo.getCollation());
		}
		colDef.append(" ");

		if (!fieldInfo.isNull())
		{
			colDef.append("NOT ");
		}
		colDef.append("NULL ");

		if (fieldInfo.isNull() || (fieldInfo.getDefault() != null))
		{
			colDef.append("DEFAULT ");
			try
			{
				if (fieldInfo.getDefault() == null)
				{
					colDef.append("NULL ");
				}
				else
				{
					Double.parseDouble(fieldInfo.getDefault());
					colDef.append(fieldInfo.getDefault()).append(" ");
				}
			}
			catch (NumberFormatException e)
			{
				colDef.append("'").append(fieldInfo.getDefault()).append("' ");
			}
		}
		if (fieldInfo.isAutoIncrement())
		{
			colDef.append("AUTO_INCREMENT ");
		}
		if (singleTerm)
		{
			if (fieldInfo.getPreviousFieldName() == null)
			{
				colDef.append("FIRST ");
			}
			else
			{
				colDef.append("AFTER `").append(fieldInfo.getPreviousFieldName()).append("` ");
			}
		}
		return colDef.toString();
	}

	private void renderKeyDiff(OutputWriter writer, KeyDiff keyDiff, boolean reverse)
	{
		switch (keyDiff.getMode())
		{
			case LEFT_ONLY:
			{
				writer.println(reverse
					? getDropKeyQuery(keyDiff.getKeyInfoA())
					: getCreateKeyQuery(keyDiff.getKeyInfoA()));
				break;
			}
			case RIGHT_ONLY:
			{
				writer.println(reverse
					? getCreateKeyQuery(keyDiff.getKeyInfoB())
					: getDropKeyQuery(keyDiff.getKeyInfoB()));
				break;
			}
			case DIFFERENT:
			{
				if (reverse)
				{
					writer.println(getDropKeyQuery(keyDiff.getKeyInfoA()));
					writer.println(getCreateKeyQuery(keyDiff.getKeyInfoB()));
				}
				else
				{
					writer.println(getDropKeyQuery(keyDiff.getKeyInfoB()));
					writer.println(getCreateKeyQuery(keyDiff.getKeyInfoA()));
				}
				break;
			}
		}
	}

	private void renderProcedureDiff(OutputWriter writer, ProcedureDiff procedureDiff, boolean reverse)
	{
		switch (procedureDiff.getMode())
		{
			case LEFT_ONLY:
				renderProcedureInfo(writer, procedureDiff.getProcedureInfoA(), reverse);
				break;

			case RIGHT_ONLY:
				renderProcedureInfo(writer, procedureDiff.getProcedureInfoB(), !reverse);
				break;

			case DIFFERENT:
			{
				ProcedureInfo procedureInfo
					= !reverse ? procedureDiff.getProcedureInfoA() : procedureDiff.getProcedureInfoB();
				renderProcedureInfo(writer, procedureInfo, true);
				renderProcedureInfo(writer, procedureInfo, false);
				break;
			}
		}
	}

	private void renderProcedureInfo(OutputWriter writer, ProcedureInfo procedureDiff, boolean drop)
	{
		if (drop)
		{
			writer.println("DROP " + procedureDiff.getType() + " `" + procedureDiff.getName() + "`;");
		}
		else
		{
			writer.println("DELIMITER $$\n" + procedureDiff.getCreateStatement() + "$$\nDELIMITER ;");
		}
	}

	private String getCreateKeyQuery(KeyInfo keyInfo)
	{
		StringBuilder query = new StringBuilder();
		query.append("ALTER TABLE `")
			.append(keyInfo.getTableName())
			.append("` ADD ")
			.append(getKeyType(keyInfo))
			.append(" ")
			.append(buildKeyDef(keyInfo))
			.append(";");
		return query.toString();
	}

	private String getDropKeyQuery(KeyInfo keyInfo)
	{
		StringBuilder query = new StringBuilder();
		query.append("DROP INDEX `")
			.append(keyInfo.getName())
			.append("` ON `")
			.append(keyInfo.getTableName())
			.append("`;");
		return query.toString();
	}

	private String getKeyType(KeyInfo keyInfo)
	{
		if (keyInfo.getName().equals("PRIMARY"))
		{
			return "PRIMARY KEY";
		}
		else if (!keyInfo.isUnique())
		{
			return "INDEX";
		}
		else
		{
			return "UNIQUE INDEX";
		}
	}

	private String buildKeyDef(KeyInfo keyInfo)
	{
		StringBuilder keyDef = new StringBuilder();
		if (!keyInfo.getName().equals("PRIMARY"))
		{
			keyDef.append(keyInfo.getName())
				.append(" ");
		}
		keyDef.append("(");
		int i = 0;
		for (KeyInfo.KeyField col : keyInfo.getFields())
		{
			if (i > 0)
			{
				keyDef.append(", ");
			}
			keyDef.append(col.toString());
			i++;
		}
		keyDef.append(")");
		return keyDef.toString();
	}

	public static interface OutputWriter
	{

		void println(String string);

		void print(String string);
	}
}
