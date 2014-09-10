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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author marko
 */
public final class Utils {

    private static final Random RANDOM = new SecureRandom();
    private static final byte[] SIGNATURE = "SS".getBytes(StandardCharsets.UTF_8);

    public static BigInteger encodeSecret(String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        byte[] res;
        if ((bytes[0] & 0b10000000) >> 7 == 1) {
            res = new byte[bytes.length + 1];
            res[0] = 0;
            System.arraycopy(bytes, 0, res, 1, bytes.length);
        } else {
            res = bytes;
        }
        BigInteger r = new BigInteger(res);
        assert r.compareTo(BigInteger.ZERO) > 0;
        return r;
    }

    @SuppressWarnings("empty-statement")
    public static String decodeSecret(BigInteger secret) {
        byte[] bytes = secret.toByteArray();
        int count;
        for (count = 0; count < bytes.length && bytes[count] == 0; count++);
        byte[] trimmed = new byte[bytes.length - count];
        System.arraycopy(bytes, count, trimmed, 0, trimmed.length);
        return new String(trimmed, StandardCharsets.UTF_8);
    }

    public static BigInteger getFirstPrimeGreaterThan(BigInteger secret) {
        return secret.nextProbablePrime();
    }

    public static BigInteger getRandomPrimeGreaterThan(BigInteger secret) {
        BigInteger res = BigInteger.ZERO;
        while (res.compareTo(secret) <= 0) {
            res = BigInteger.probablePrime(secret.bitLength(), RANDOM);
        }
        return res;
    }

    public static BigInteger getRandomLessThan(BigInteger prime) {
        BigInteger r = null;
        while (r == null || r.compareTo(prime) >= 0) {
            r = new BigInteger(prime.bitLength(), RANDOM);
        }
        return r;
    }

    public static BigInteger[] generateRandomCoefficients(int n, BigInteger elementZero, BigInteger prime) {
        BigInteger[] res = new BigInteger[n];
        res[0] = elementZero;
        for (int i = 1; i < n; i++) {
            res[i] = Utils.getRandomLessThan(prime);
        }
        return res;
    }

    public static byte[] encodeToBinary(SecretShare share) {
        assert share.getN() >= 0;
        assert share.getN() <= 255;
        byte[] shareData = share.getShare().toByteArray();
        byte[] primeData = share.getPrime().toByteArray();
        byte n = new Integer(share.getN()).byteValue();

        int len = 9 + SIGNATURE.length + shareData.length + primeData.length;

        ByteBuffer bb = ByteBuffer.allocate(len);
        bb.put(SIGNATURE);
        bb.put(n);
        bb.putInt(shareData.length);
        bb.put(shareData);
        bb.putInt(primeData.length);
        bb.put(primeData);

        assert bb.position() == bb.capacity();
        assert bb.hasArray();

        return bb.array();
    }

    public static SecretShare decodeFromBinary(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        byte[] signature = new byte[SIGNATURE.length];
        bb.get(signature);
        if (!Arrays.equals(SIGNATURE, signature)) {
            throw new IllegalArgumentException("Invalid data");
        }
        byte n = bb.get();
        int shareDataLen = bb.getInt();
        byte[] shareData = new byte[shareDataLen];
        bb.get(shareData);
        int primeDataLen = bb.getInt();
        byte[] primeData = new byte[primeDataLen];
        bb.get(primeData);

        assert bb.position() == bb.capacity();

        BigInteger share = new BigInteger(shareData);
        BigInteger prime = new BigInteger(primeData);
        assert share.compareTo(BigInteger.ZERO) > 0;
        assert prime.compareTo(BigInteger.ZERO) > 0;
        return new SecretShare(n, share, prime);
    }

    private Utils() {
    }

}
