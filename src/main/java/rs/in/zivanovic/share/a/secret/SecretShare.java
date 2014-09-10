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
import java.util.Objects;

/**
 *
 * @author marko
 */
public final class SecretShare {

    private final int n;
    private final BigInteger share;
    private final BigInteger prime;

    public SecretShare(int n, BigInteger share, BigInteger prime) {
        this.n = n;
        this.share = share;
        this.prime = prime;
    }

    public int getN() {
        return n;
    }

    public BigInteger getShare() {
        return share;
    }

    public BigInteger getPrime() {
        return prime;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.n;
        hash = 79 * hash + Objects.hashCode(this.share);
        hash = 79 * hash + Objects.hashCode(this.prime);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SecretShare other = (SecretShare) obj;
        if (this.n != other.n) {
            return false;
        }
        if (!Objects.equals(this.share, other.share)) {
            return false;
        }
        if (!Objects.equals(this.prime, other.prime)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SecretShare{" + "n=" + n + ", share=" + share + ", prime=" + prime + '}';
    }

}
