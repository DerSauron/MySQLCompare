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

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Daniel Volk <mail@volkarts.com>
 */
public class TokenListTest
{

	public TokenListTest()
	{
	}

	@Test
	public void testTokenizeCharaters()
	{
		final TokenList<Character> expected = TokenList.fromList(
			Arrays.asList('C', 'h', 'a', 'r', 'a', 'c', 't', 'e', 'r'));

		TokenList<Character> actual = TokenList.tokenizeCharaters("Character");

		assertEquals(expected, actual);
	}

	@Test
	public void testTokenizeLines()
	{
		final TokenList<String> expected = TokenList.fromList(Arrays.asList("I", "am", "a", "list"));

		TokenList<String> actual = TokenList.tokenizeLines("I\nam\na\nlist");

		assertEquals(expected, actual);
	}

	@Test
	public void testTokenizeWords()
	{
		final TokenList<String> expected = TokenList.fromList(
			Arrays.asList("I", " ", "am", " ", "a", " `", "list", "`, ", "or", " ", "what", "?"));

		TokenList<String> actual = TokenList.tokenizeWords("I am a `list`, or what?");

		assertEquals(expected, actual);
	}

	@Test
	public void testSubTokenList()
	{
		final TokenList<String> expected = TokenList.fromList(
			Arrays.asList("am", " ", "a"));

		TokenList<String> whole = TokenList.tokenizeWords("I am a `list`, or what?");
		TokenList<String> actual = whole.subTokenList(2, 5);

		assertEquals(expected, actual);
	}

}
