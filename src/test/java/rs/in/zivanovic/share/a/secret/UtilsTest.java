/*
 * Copyright (c) 2014, marko
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
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author marko
 */
public class UtilsTest {

    private static final String s1 = "Hello World!";
    private static final String s2 = "The quick brown fox jumps over the lazy dog.";
    private static final String s3 = "Текст на ћирилици.";

    public UtilsTest() {
    }

    @Test
    public void testGetFirstPrimeGreaterThan() {
        System.out.println("getFirstPrimeGreaterThan");
        BigInteger si1 = Utils.encodeSecret(s1);
        BigInteger si2 = Utils.encodeSecret(s2);
        BigInteger p1 = Utils.getFirstPrimeGreaterThan(si1);
        BigInteger p2 = Utils.getFirstPrimeGreaterThan(si2);
        assertTrue(p1.compareTo(si1) > 0);
        assertTrue(p2.compareTo(si2) > 0);
    }

    @Test
    public void testGetRandomPrimeGreaterThan() {
        System.out.println("getRandomPrimeGreaterThan");
        BigInteger si1 = Utils.encodeSecret(s1);
        BigInteger si2 = Utils.encodeSecret(s2);
        BigInteger p1 = Utils.getRandomPrimeGreaterThan(si1);
        BigInteger p2 = Utils.getRandomPrimeGreaterThan(si2);
        assertTrue(p1.compareTo(si1) > 0);
        assertTrue(p2.compareTo(si2) > 0);
    }

    @Test
    public void testGetRandomLessThan() {
        System.out.println("getRandomLessThan");
        BigInteger i = BigInteger.valueOf(100);
        BigInteger r = Utils.getRandomLessThan(i);
        assertTrue(r.compareTo(i) < 0);
        i = BigInteger.valueOf(100_000_000);
        r = Utils.getRandomLessThan(i);
        assertTrue(r.compareTo(i) < 0);
    }

    @Test
    public void testBinaryCodec() {
        System.out.println("binaryCodec");
        SecretShare src = new SecretShare(3, new BigInteger("100"), new BigInteger("200"));
        byte[] data = Utils.encodeToBinary(src);
        SecretShare dst = Utils.decodeFromBinary(data);
        assertTrue(src.equals(dst));
    }

    @Test
    public void testSecretCodec() {
        System.out.println("secretCodec");
        String s = "Текст на ћирилици, чучко, џеки, Ђорђе, ...";
        BigInteger bi = Utils.encodeSecret(s);
        String d = Utils.decodeSecret(bi);
        assertTrue(s.equals(d));
    }

}
