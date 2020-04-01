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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TokenList<T> implements List<TokenList.Token<T>>
{
	private final List<Token<T>> tokens;

	private TokenList(final List<Token<T>> tokens)
	{
		this.tokens = tokens;
	}

	@Override
	public Iterator<Token<T>> iterator()
	{
		return tokens.iterator();
	}

	@Override
	public int size()
	{
		return tokens.size();
	}

	@Override
	public boolean isEmpty()
	{
		return tokens.isEmpty();
	}

	@Override
	public boolean contains(Object o)
	{
		return tokens.contains(o);
	}

	@Override
	public Object[] toArray()
	{
		return tokens.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return tokens.toArray(a);
	}

	@Override
	public boolean add(Token<T> e)
	{
		return tokens.add(e);
	}

	@Override
	public boolean remove(Object o)
	{
		return tokens.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return tokens.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Token<T>> c)
	{
		return tokens.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Token<T>> c)
	{
		return tokens.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		return tokens.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		return tokens.retainAll(c);
	}

	@Override
	public void clear()
	{
		tokens.clear();
	}

	@Override
	public boolean equals(Object o)
	{
		return tokens.equals(o);
	}

	@Override
	public int hashCode()
	{
		return tokens.hashCode();
	}

	@Override
	public Token<T> get(int index)
	{
		return tokens.get(index);
	}

	@Override
	public Token<T> set(int index, Token<T> element)
	{
		return tokens.set(index, element);
	}

	@Override
	public void add(int index, Token<T> element)
	{
		tokens.add(index, element);
	}

	@Override
	public Token<T> remove(int index)
	{
		return tokens.remove(index);
	}

	@Override
	public int indexOf(Object o)
	{
		return tokens.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o)
	{
		return tokens.lastIndexOf(o);
	}

	@Override
	public ListIterator<Token<T>> listIterator()
	{
		return tokens.listIterator();
	}

	@Override
	public ListIterator<Token<T>> listIterator(int index)
	{
		return tokens.listIterator(index);
	}

	@Override
	public List<Token<T>> subList(int fromIndex, int toIndex)
	{
		return tokens.subList(fromIndex, toIndex);
	}

	public static <X> TokenList<X> fromList(final List<X> items)
	{
		List<Token<X>> tokens = items.stream().map((item) ->
		{
			return new Token<>(item);
		}).collect(Collectors.toList());
		return new TokenList<>(tokens);
	}

	public static TokenList<Character> tokenizeCharaters(String input)
	{
		ArrayList<Token<Character>> tokens = new ArrayList<>();

		for (int i = 0; i < input.length(); i++)
		{
			tokens.add(new Token<>(input.charAt(i)));
		}

		return new TokenList<>(tokens);
	}

	public static TokenList<String> tokenizeLines(String input)
	{
		return genericStringTokenizer(input, "\r\n|\r|\n");
	}

	public static TokenList<String> tokenizeWords(String input)
	{
		return genericStringTokenizer(input, "\\b");
	}

	private static TokenList<String> genericStringTokenizer(String input, String pattern)
	{
		ArrayList<Token<String>> tokens = new ArrayList<>();

		Scanner scanner = new Scanner(input);
		scanner.useDelimiter(Pattern.compile(pattern));
		while (scanner.hasNext())
		{
			tokens.add(new Token<>(scanner.next()));
		}

		return new TokenList<>(tokens);
	}

	public TokenList<T> subTokenList(int fromIndex, int toIndex)
	{
		return new TokenList<>(subList(fromIndex, toIndex));
	}

	@Override
	public String toString()
	{
		final StringBuilder output = new StringBuilder();
		output.append("[");
		boolean first = true;
		for (Token<T> t : this)
		{
			if (!first)
			{
				output.append(", ");
			}

			output.append(t.toString());

			first = false;
		}
		output.append("]");
		return output.toString();
	}

	public static class Token<TT>
	{
		public final TT value;

		public Token(TT value)
		{
			this.value = value;
		}

		@Override
		public int hashCode()
		{
			int hash = 3;
			hash = 11 * hash + Objects.hashCode(this.value);
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
			final Token<?> other = (Token<?>)obj;
			return Objects.equals(this.value, other.value);
		}

		@Override
		public String toString()
		{
			return value.toString();
		}
	}
}
