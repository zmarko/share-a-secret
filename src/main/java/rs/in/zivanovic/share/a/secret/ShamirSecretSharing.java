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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marko
 */
public final class ShamirSecretSharing {

    public static List<SecretShare> split(String secretString, int total, int threshold) {
        BigInteger secret = Utils.encodeSecret(secretString);
        return split(secret, total, threshold);
    }

    public static List<SecretShare> split(BigInteger secretNumber, int total, int threshold) {
        BigInteger prime = Utils.getFirstPrimeGreaterThan(secretNumber);
        BigInteger[] coeffs = Utils.generateRandomCoefficients(total, secretNumber, prime);
        return split(secretNumber, coeffs, total, threshold, prime);
    }

    public static List<SecretShare> split(BigInteger secret, BigInteger[] coefficients, int total, int threshold,
            BigInteger prime) {

        if (secret.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("Secret must be positive integer");
        }

        if (prime.compareTo(secret) <= 0) {
            throw new IllegalArgumentException("Prime must be greater than secret");
        }

        if (coefficients.length < threshold) {
            throw new IllegalArgumentException("Not enough coefficients, need " + threshold + ", has " +
                                               coefficients.length);
        }

        if (total < threshold) {
            throw new IllegalArgumentException("Total number of shares must be greater than or equal threshold");
        }

        List<SecretShare> shares = new ArrayList<>();

        for (int i = 1; i <= total; i++) {
            BigInteger x = BigInteger.valueOf(i);
            BigInteger v = coefficients[0];
            for (int c = 1; c < threshold; c++) {
                v = v.add(x.modPow(BigInteger.valueOf(c), prime).multiply(coefficients[c]).mod(prime)).mod(prime);
            }
            shares.add(new SecretShare(i, v, prime));
        }
        return shares;
    }

    public static String joinToUtf8String(List<SecretShare> shares) {
        return Utils.decodeSecret(join(shares));
    }

    public static BigInteger join(List<SecretShare> shares) {
        if (!checkSamePrimes(shares)) {
            throw new IllegalArgumentException("Shares not from the same series");
        }
        BigInteger res = BigInteger.ZERO;
        for (int i = 0; i < shares.size(); i++) {
            BigInteger n = BigInteger.ONE;
            BigInteger d = BigInteger.ONE;
            BigInteger prime = shares.get(i).getPrime();
            for (int j = 0; j < shares.size(); j++) {
                if (i != j) {
                    BigInteger sp = BigInteger.valueOf(shares.get(i).getN());
                    BigInteger np = BigInteger.valueOf(shares.get(j).getN());
                    n = n.multiply(np.negate()).mod(prime);
                    d = d.multiply(sp.subtract(np)).mod(prime);
                }
            }
            BigInteger v = shares.get(i).getShare();
            res = res.add(prime).add(v.multiply(n).multiply(d.modInverse(prime))).mod(prime);
        }
        return res;
    }

    private static boolean checkSamePrimes(List<SecretShare> shares) {
        boolean ret = true;
        BigInteger prime = null;
        for (SecretShare share : shares) {
            if (prime == null) {
                prime = share.getPrime();
            } else if (!prime.equals(share.getPrime())) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    private ShamirSecretSharing() {
    }

}
