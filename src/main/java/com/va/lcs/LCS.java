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
package com.va.lcs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import static java.lang.Integer.max;

public class LCS<T>
{
	private TokenList<T> a;
	private TokenList<T> b;
	private int m;
	private int n;

	private int[][] table;

	public LCS()
	{
		this.table = null;
	}

	public void compute(TokenList<T> a, TokenList<T> b)
	{
		this.a = a;
		this.b = b;
		this.m = a.size();
		this.n = b.size();

		table = new int[m + 1][n + 1];

		for (int i = 1; i <= m; i++)
		{
			for (int j = 1; j <= n; j++)
			{
				if (Objects.equals(a.get(i - 1), b.get(j - 1)))
				{
					table[i][j] = table[i - 1][j - 1] + 1;
				}
				else
				{
					table[i][j] = max(table[i - 1][j], table[i][j - 1]);
				}
			}
		}
	}

	public List<SubList<T>> extractSubStrings()
	{
		return extractSubLists();
	}

	public List<SubList<T>> extractSubLists()
	{
		checkState();

		ArrayList<Character> diffs = new ArrayList<>();

		int changes = computeDiff(diffs, m, n);

		if (changes == 0)
		{
			return Collections.emptyList();
		}

		ArrayList<SubList<T>> subLists = new ArrayList<>();
		char currDir = diffs.get(0);
		int start = 0;
		Ref<Integer> startA = new Ref<>(0);
		Ref<Integer> startB = new Ref<>(0);

		for (int i = 1; i < diffs.size(); i++)
		{
			if (diffs.get(i) != currDir)
			{
				appendSubString(subLists, currDir, startA, startB, i - start);

				currDir = diffs.get(i);
				start = i;
			}
		}

		if (start < diffs.size())
		{
			appendSubString(subLists, currDir, startA, startB, diffs.size() - start);
		}

		return subLists;
	}

	private int computeDiff(ArrayList<Character> diffs, int i, int j)
	{
		int changes = 0;

		while (true)
		{
			if ((i > 0) && (j > 0) && Objects.equals(a.get(i - 1), b.get(j - 1)))
			{
				diffs.add(' ');

				i--;
				j--;
			}
			else if ((j > 0) && ((i == 0) || (table[i][j - 1] >= table[i - 1][j])))
			{
				diffs.add('+');

				changes++;
				j--;
			}
			else if ((i > 0) && ((j == 0) || (table[i][j - 1] < table[i - 1][j])))
			{
				diffs.add('-');

				changes++;
				i--;
			}
			else
			{
				break;
			}
		}

		for (int e = 0; e < diffs.size() / 2; e++)
		{
			Character temp = diffs.get(e);
			diffs.set(e, diffs.get(diffs.size() - e - 1));
			diffs.set(diffs.size() - e - 1, temp);
		}

		return changes;
	}

	private void appendSubString(ArrayList<SubList<T>> subStrings, char dir, Ref<Integer> startA, Ref<Integer> startB, int length)
	{
		if (dir == '+')
		{
			subStrings.add(new SubList<>(dir, b.subTokenList(startB.value, startB.value + length)));
			startB.value += length;
		}
		else
		{
			subStrings.add(new SubList<>(dir, a.subTokenList(startA.value, startA.value + length)));
			startA.value += length;
			if (dir == ' ')
			{
				startB.value += length;
			}
		}
	}

	public void dump()
	{
		checkState();

		for (int[] table1 : table)
		{
			for (int j = 0; j < table1.length; j++)
			{
				System.out.format("%3d", table1[j]);
			}
			System.out.println();
		}

	}

	private void checkState()
	{
		if (table == null)
		{
			throw new IllegalStateException("compute() was not called");
		}
	}

	public static class SubList<X>
	{
		public final char dir;
		public final TokenList<X> list;

		SubList(char dir, TokenList<X> list)
		{
			this.dir = dir;
			this.list = list;
		}

		public String glue()
		{
			return glue("");
		}

		public String glue(String delim)
		{
			final StringBuilder output = new StringBuilder();

			this.list.forEach((item) -> output.append(item.toString()));

			return output.toString();
		}

		@Override
		public String toString()
		{
			return dir + list.toString();
		}

		@Override
		public int hashCode()
		{
			int hash = 7;
			hash = 53 * hash + this.dir;
			hash = 53 * hash + Objects.hashCode(this.list);
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
			final SubList<?> other = (SubList<?>)obj;
			if (this.dir != other.dir)
			{
				return false;
			}
			return Objects.equals(this.list, other.list);
		}
	}

	private static class Ref<X>
	{
		public X value;

		public Ref(X value)
		{
			this.value = value;
		}
	}
}
