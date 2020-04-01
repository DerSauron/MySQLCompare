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
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 *
 * @author Daniel Volk <mail@volkarts.com>
 */
public class LCSTest
{
	public LCSTest()
	{
	}

	private static TokenList<Character> tokenList(String input)
	{
		return TokenList.tokenizeCharaters(input);
	}

	private static LCS.SubList<Character> subList(char dir, String string)
	{
		return new LCS.SubList<>(dir, tokenList(string));
	}

	static Stream<Arguments> testCompute()
	{
		return Stream.of(
			arguments(tokenList("abcde"), tokenList("abcxe"), Arrays.asList(
				subList(' ', "abc"),
				subList('-', "d"),
				subList('+', "x"),
				subList(' ', "e")
			)),

			arguments(tokenList("abcdefgh"), tokenList("abcvwxyzgh"), Arrays.asList(
				subList(' ', "abc"),
				subList('-', "def"),
				subList('+', "vwxyz"),
				subList(' ', "gh")
			)),

			arguments(tokenList("abcdefgh"), tokenList("vwxyzgh"), Arrays.asList(
				subList('-', "abcdef"),
				subList('+', "vwxyz"),
				subList(' ', "gh")
			)),

			arguments(tokenList("abcdefgh"), tokenList("abcvwxyz"), Arrays.asList(
				subList(' ', "abc"),
				subList('-', "defgh"),
				subList('+', "vwxyz")
			))
		);
	}

	@ParameterizedTest
	@MethodSource
	public void testCompute(TokenList<Character> a, TokenList<Character> b, List<LCS.SubList<Character>> expected)
	{
		LCS<Character> lcs = new LCS<>();

		lcs.compute(a, b);

		assertEquals(expected, lcs.extractSubStrings());
	}

	@Test
	public void testExtractSubStringsFail()
	{
		LCS<Object> lcs = new LCS<>();

		assertThrows(IllegalStateException.class, lcs::extractSubStrings);
	}

	@Test
	public void testDumpFail()
	{
		LCS<Object> lcs = new LCS<>();

		assertThrows(IllegalStateException.class, lcs::dump);
	}
}
