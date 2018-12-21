package com.ucai.uvegetable.utils;

import java.math.BigInteger;

public class RSAUtils {
    private long p, q;
    private long N, r;
    private long e, d;
    private PubKey pubKey;
    private PriKey priKey;

    public class PubKey {
        long N;
        long e;
        private PubKey (long N, long e) {
            this.N = N;
            this.e = e;
        }
    }

    public class PriKey {
        long N;
        long d;
        private PriKey (long N, long d) {
            this.N = N;
            this.d = d;
        }
    }

    public PubKey getPubKey() {
        if (pubKey == null)
            throw new IllegalStateException("PubKey is null");
        return pubKey;
    }

    public PriKey getPriKey() {
        if (priKey == null)
            throw new IllegalStateException("PriKey is null");
        return priKey;
    }

    private static volatile RSAUtils _rsaUtils = null;

    private RSAUtils() {
        findTwoPrimeNumber();
        if (p != 0L && q != 0L) {
            N = p * q;
            r = (p - 1) * (q - 1);
            calculPublicKey(r);
        }
        pubKey = new PubKey(N, e);
        priKey = new PriKey(N, d);
    }

    // the NEW_RSA Instance;
    public static RSAUtils getInstance() {
        if (_rsaUtils == null) {
            synchronized (RSAUtils.class) {
                if (_rsaUtils == null) {
                    _rsaUtils = new RSAUtils();
                }
            }
        }
        return _rsaUtils;
    }

    // the string to unicode;
    private String stringToUnicode(String string) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            sb.append("/");
            char c = string.charAt(i);
            sb.append(Integer.toString(c));
        }
        return sb.toString();
    }

    // unicode to string;
    private String unicodeToString(String unicode) {
        StringBuilder sb = new StringBuilder();
        String[] unicodes = unicode.split("/");
        for (int i = 1; i < unicodes.length; i++) {
            int uni = Integer.parseInt(unicodes[i]);
            sb.append((char)uni);
        }
        return sb.toString();
    }

    // find two prime number p and q;
    private void findTwoPrimeNumber() {
        long s, p1, q1;
        for (;;) {
            for (;;) {
                s = (int)(Math.random() * 999);
                if(s > 99){
                    if(isPrime(s)){
                        p1= s;
                        break;
                    }
                }
            }
            for (;;) {
                s = (int)(Math.random() * 999);
                if(s > 99){
                    if(isPrime(s)){
                        q1= s;
                        break;
                    }
                }
            }
            if(compTwoNumberIsPrime(p1, q1)){
                p = p1;
                q = q1;
                break;
            }
        }
    }

    // judge this number is a prime;
    private static boolean isPrime(long s){
        for(long i = 2; i < Math.sqrt(s); i ++){
            if(s % i == 0){
                return false;
            }
        }
        return true;
    }

    // judge two number whether are prime;
    private boolean compTwoNumberIsPrime(long p, long q) {
        long t;
        if(p < q){
            t = q;
            q = p;
            p = t;
        }
        while(p % q == 1){
            t = q;
            q = p % q;
            p = t;
        }
        return q == 1;
    }

    // calculate the public key;
    private void calculPublicKey(long r) {
        for (long i = 2; i < r; i ++) {
            if (compTwoNumberIsPrime(i, r)) {
                for (int j = 2; j < r; j++) {
                    if ((j * i) % r == 1) {
                        e = i;
                        d = j;
                        return;
                    }
                }
            }
        }
    }

    // encode message;
    public String encodeMessage (String message, PubKey pubKey) {
        if (pubKey == null)
            throw new IllegalStateException("PubKey is null");
        String uniStr = stringToUnicode(message);
        String[] strs = uniStr.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < strs.length; i++) {
            sb.append("/");
            BigInteger m = BigInteger.valueOf(Long.parseLong(strs[i]));
            BigInteger N = BigInteger.valueOf(pubKey.N);
            BigInteger e = BigInteger.valueOf(pubKey.e);
            BigInteger c = m.modPow(e, N);
            sb.append(c.toString());
        }
        return sb.toString();
    }

    // decode message;
    public String decodeMessage (String message, PriKey priKey) {
        if (priKey == null)
            throw new IllegalStateException("PriKey is null");
        String[] strs = message.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < strs.length; i++) {
            sb.append("/");
            BigInteger c = BigInteger.valueOf(Long.parseLong(strs[i]));
            BigInteger N = BigInteger.valueOf(priKey.N);
            BigInteger d = BigInteger.valueOf(priKey.d);
            BigInteger m = c.modPow(d, N);
            sb.append(m.toString());
        }
        return unicodeToString(sb.toString());
    }
}
