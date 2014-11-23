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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point into application.
 *
 * @author marko
 */
public class Main {

    public static void main(String[] args) {
        if (args.length == 1 && args[0].toLowerCase().equals("-d")) {
            decodeFromStdin();
        } else if (args.length == 3 && args[0].toLowerCase().equals("-e")) {
            encodeToStdout(Integer.valueOf(args[1]), Integer.valueOf(args[2]));
        } else {
            showHelp();
        }
    }

    private static void decodeFromStdin() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            List<SecretShare> shares = new ArrayList<>();
            System.out.println("Enter a number of shares, one per line, that you'd like to try to join.");
            System.out.println("Finish with empty line.");
            while (true) {
                String line = br.readLine();
                if (line != null && !line.isEmpty()) {
                    try {
                        int s = line.indexOf("U1");
                        if (s >= 0) {
                            SecretShare share = Utils.decodeFromBinary(Base64.decode(line.substring(s)));
                            shares.add(share);
                        } else {
                            System.err.println("Invalid data");
                        }
                    } catch (IllegalArgumentException ex) {
                        System.err.println(ex.getMessage());
                    }
                } else {
                    break;
                }
            }
            String secret = ShamirSecretSharing.joinToUtf8String(shares);
            System.out.println("Secret = " + secret);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static void encodeToStdout(int totalShares, int thresholdShares) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line = br.readLine();
            List<SecretShare> shares = ShamirSecretSharing.split(line, totalShares, thresholdShares);
            for (SecretShare share : shares) {
                System.out.println(Base64.encodeBytes(Utils.encodeToBinary(share)));
                System.out.println();
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static void showHelp() {
        System.err.println("Share-A-Secret 1.0.0-SNAPSHOT");
        System.err.println("This application uses Shamir's secret sharing algorithm to split the secret data into");
        System.err.println("a desired number of shares. In order to retrieve the secret data, at least a required");
        System.err.println("minimum number of shares must be present.");
        System.err.println();
        System.err.println("All data is read from standard input and sent to standard output.");
        System.err.println();
        System.err.println("To encode: java -jar sas.jar -e <n> <t>");
        System.err.println("To decode: java -jar sas.jar -d");
        System.err.println();
        System.err.println(" n - total number of shares to generate");
        System.err.println(" t - minimum number of shares required to retrieve the secret.");
        System.err.println();
    }

}
