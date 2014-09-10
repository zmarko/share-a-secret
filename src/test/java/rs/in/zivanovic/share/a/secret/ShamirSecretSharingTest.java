/*
 * Copyright (c) 2014, Marko Živanović
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package rs.in.zivanovic.share.a.secret;

import java.math.BigInteger;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author marko
 */
public class ShamirSecretSharingTest {

    private static final BigInteger[] TEST_COEFFICIENTS = new BigInteger[3];
    private static final BigInteger TEST_PRIME = BigInteger.valueOf(1613);
    private static final BigInteger TEST_SECRET = BigInteger.valueOf(1234);
    private static final List<SecretShare> SHARES;

    static {
        TEST_COEFFICIENTS[0] = TEST_SECRET;
        TEST_COEFFICIENTS[1] = BigInteger.valueOf(166);
        TEST_COEFFICIENTS[2] = BigInteger.valueOf(94);
        SHARES = ShamirSecretSharing.split(TEST_SECRET, TEST_COEFFICIENTS, 6, 3, TEST_PRIME);
    }

    @Test
    public void testSplit() {
        System.out.println("split");
        assertTrue(SHARES.get(0).getShare().compareTo(BigInteger.valueOf(1494)) == 0);
        assertTrue(SHARES.get(1).getShare().compareTo(BigInteger.valueOf(329)) == 0);
        assertTrue(SHARES.get(2).getShare().compareTo(BigInteger.valueOf(965)) == 0);
        assertTrue(SHARES.get(3).getShare().compareTo(BigInteger.valueOf(176)) == 0);
        assertTrue(SHARES.get(4).getShare().compareTo(BigInteger.valueOf(1188)) == 0);
        assertTrue(SHARES.get(5).getShare().compareTo(BigInteger.valueOf(775)) == 0);
    }

    @Test
    public void testJoinInsufficientShares() {
        System.out.println("joinInsufficientShares");
        List<SecretShare> shares = new ArrayList<>();
        shares.add(SHARES.get(0));
        shares.add(SHARES.get(5));
        BigInteger joined = ShamirSecretSharing.join(shares);
        assertTrue(joined.compareTo(TEST_SECRET) != 0);
    }

    @Test
    public void testJoinSufficientShares() {
        System.out.println("joinSufficientShares");
        List<SecretShare> shares = new ArrayList<>();
        shares.add(SHARES.get(3));
        shares.add(SHARES.get(5));
        shares.add(SHARES.get(1));
        BigInteger joined = ShamirSecretSharing.join(shares);
        assertTrue(joined.compareTo(TEST_SECRET) == 0);
    }

    @Test
    public void testLongStrings() {
        System.out.println("longStrings");
        String s = "Quick brown fox jumps over the lazy dog.";
        test(s, 6, 5);

        s = "Текст на ћирилици, чучко, џеки, Ђорђе, ...";
        test(s, 6, 5);

        s = "A sada da vidimo istu duzinu, ali, obican ";
        test(s, 6, 5);

        s = "ATTACK AT DAWN";
        test(s, 6, 5);
    }

    private void test(String s, int total, int required) {
        List<SecretShare> shares = ShamirSecretSharing.split(s, total, required);
        List<SecretShare> someShares = new ArrayList<>();
        for (int i = required - 1; i >= 0; i--) {
            someShares.add(shares.get(i));
        }
        String joined = ShamirSecretSharing.joinToUtf8String(someShares);
        assertTrue(joined.equals(s));
    }

}
