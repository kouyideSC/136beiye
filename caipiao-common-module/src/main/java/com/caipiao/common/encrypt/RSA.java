package com.caipiao.common.encrypt;

import javax.crypto.Cipher;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA工具类
 * Created by kouyi on 2017/9/27.
 */
public class RSA {
    private static final int KEYSIZE = 2048;
    private static final String ENCODE = "UTF-8";
    private static final String ALGORITHM_ = "RSA";
    private static final String PUBLICKEY = "PUBKEY";
    private static final String PRIVATEKEY = "PRIKEY";

    //服务器端公钥
    private static final String SERVER_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkjAkie8JZvZ8VJsXGjGTq4MPohHG488n0SeobpypatmkVheaon77c2cXqrvsfNBLj/Y5A/bu7FP+V7xNgFM9G+R8S/c2wPA7BgxNqWNGeLt9CICZLQ0nenLznbzXDA1danafSwx4c4c/lKm1tbPYZF0ZwBvV4dXCaLAPiScc8ESmn8YHJJR3GMrY5n8F1TZRAmE7f1twEyoRBysF4qse2M8DKIdZVO+sFnJ0Oc8bdp2jBVRvBnAmVLOM7c6qJaXjilhkt7KAEZUF9BUmkfdEJGAo5hQC9d52nEneSawF3RsheCrKFBrXawyeEso+A4gTJGCmD3lXTDip2XOFXMVe8QIDAQAB";
    //客户端私钥
    private static final String CLIENT_PRIKEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCSMCSJ7wlm9nxUmxcaMZOrgw+iEcbjzyfRJ6hunKlq2aRWF5qifvtzZxequ+x80EuP9jkD9u7sU/5XvE2AUz0b5HxL9zbA8DsGDE2pY0Z4u30IgJktDSd6cvOdvNcMDV1qdp9LDHhzhz+UqbW1s9hkXRnAG9Xh1cJosA+JJxzwRKafxgcklHcYytjmfwXVNlECYTt/W3ATKhEHKwXiqx7YzwMoh1lU76wWcnQ5zxt2naMFVG8GcCZUs4ztzqolpeOKWGS3soARlQX0FSaR90QkYCjmFAL13nacSd5JrAXdGyF4KsoUGtdrDJ4Syj4DiBMkYKYPeVdMOKnZc4VcxV7xAgMBAAECggEAO4GXTgJcTGonH/VliQcHOjiGQ42C7TZk2dGP0T66A8GGEHlZO63+wvIDAz+tuvy61WX/vWplxRNHpAUFcyfu5wyxv+Zohk8DZ6nYbwv3IqwG8FAQifMhz8k8+0dkLga+sNiKbO0tlrFGI0iVq9oCSpBPLBDolh4CHcUU/dpLAE3+7k7NsGjZ2dc3eeTA8+7y74SyAfaeqm+/0lv/BKF1v5eeDs9zw89sedg2TLIztGSgKowc+wxJi3dbvsXok8nFgjso/zapkfpDCYH13cmiMBlwqJWlP7d71WqLAXFEEdosqnhj+QLRst/vE8GJw8CUnSGawMlHlRootYzkhsAZhQKBgQD5zmSfpOrY9BTmWHtqEB7Ml2cYr/6S3beZJbVyQzh3JIdGVuzmB1vUVG9jf6CWkis2PA1xdIuQtuR4nvlHlzobl6q+zfGY4MgP2F/wkPPDfp8BOyWb3fG1CgkMu+CQg+8JntrNqIi3/ZbXJSm1XyvfkFqej7pYlsiM/mvpeo7JuwKBgQCV0AyVMbJ7cjqVkNLXdjYciJ0r99frt8K6poypZArUTDIIvY1CdJgcQ1z9nziQBLJoV2yhtAFPXUtFuj9/T0ii+TT7ddpYS70vV7kYBxBrWxvNwokCjzCijCfr+PuMz2LwxrbdDkIwQyw7S7P/5t5hkP0xNdXPtktjuhbqDH0JQwKBgDR4u/4koAfuTS2NTG8c77s92jP/U9P5qoUKvLBBmmy8SYXm4F/5D7rr+XHG7y5xiY4c0x4Pwvkk0Zzcl7QH+fatxvnJPIRGQv/BDXX0nJ9ly3RwvhedaRYEA56fIC566Az3RzKHwiATrkmGztoAIbEWG1LPEe9lzL6A+p3SVofdAoGAYYnEN3rU1tSnWeuhqpCXaHp1wkOqPBk3WmjHWh39gv1c8h3fk63vvy8Io0QAApxcP9zzqFBXLgHy2SIK52uKQjl4imPP1f7x3JoDs6YdxZfbAVsv8w+hPaN89oDz3ljD9Tmbo07+PwftC2oddnYswV8xydFgNM1eRj7JXnblDZsCgYB1+ablWigGWUjeMGLYrDZjF+Jvet6a6FyOH4XV3W3FRnlw8xcAfoEJ286uWih4gQzFfDEVPLdTcZJQltoagOwyzXWJL2q9/ZkqIr0XxEzSrBQVUtuwmEJIbdvgvp8eBCmqm67NWoSJ/qpv3wT5vgv3TtJ8gculQmMJ0NHjN5fW5A==";
    //客户端公钥
    private static final String CLIENT_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuFPdRBaE6OnqQsXvVim5mOmXhXidOa16gLIV9sI2dRZW4ZejlE+zGIsAlbsDYagO+RkjgVeZd4oZ+P7roYNlLtY8YujNkMnhMqHHKAOXUqK6HMBzrCsu/JaMFNsYW7EyEnXnd1Lq15U85O1irXLYfBbrGjKrYlKv5p+3xUYKRRc+PdeYDCKPKLKl9vgZwACVYnYEEuM2ErxdYWRARukSIEOtO69X6C1xlJL+MnQlFMTWLvg2m3pvAHqa/ZMJLwFE9mf+29LnjAJJvPWjMCUGroU0Hjoq+cPTJp5EfPM6jHlIhHzl8oZCHvNRFgOkXz5aFqNbmg5m0vs8vugBGXN61wIDAQAB";
    //服务器端私钥
    private static final String SERVER_PRIKEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC4U91EFoTo6epCxe9WKbmY6ZeFeJ05rXqAshX2wjZ1Flbhl6OUT7MYiwCVuwNhqA75GSOBV5l3ihn4/uuhg2Uu1jxi6M2QyeEyoccoA5dSorocwHOsKy78lowU2xhbsTISded3UurXlTzk7WKtcth8FusaMqtiUq/mn7fFRgpFFz4915gMIo8osqX2+BnAAJVidgQS4zYSvF1hZEBG6RIgQ607r1foLXGUkv4ydCUUxNYu+Dabem8Aepr9kwkvAUT2Z/7b0ueMAkm89aMwJQauhTQeOir5w9MmnkR88zqMeUiEfOXyhkIe81EWA6RfPloWo1uaDmbS+zy+6AEZc3rXAgMBAAECggEACzVGQgqggE0g6niAqhuVZn/lr776yHQM++BYR43do9b853DCCJWkDTz+hxw9A2Ylqu3lY8sA0Oz70VEm4MZNk8CBIX6IaWJtKBgANSWqwOKWMINfRTvG+qLo16fO3lxy7/5tWasYDo3LixwIIQXXtgvqtcgY9byTZ5GUqhEtNLAvpOjFBpxvSROaMrM0qwHd7nTUJLwK/aPZDhdKET3zYofjGQo8AE9qcvGIdqOdXRYnfpAvcMKF5BK5mJWkaaoK4WwJ/uHXUEp7SEWoGBATykKVPSDfiXDYul0ePuHWEr3Cqn0Hq2sM1Lxntmt1yGhlRF5FkmVSzjxZXuQkaDECiQKBgQDooTELhV+vd5BhX9BYpfrpnDWBos6zWmO22USq0FGszXc780YgWNZF3GlJ2mrq8MVAr5TyPApuR4/di7/6wE0TkTVLSGXUuvSD+jM3Nv+I17VA/fWXmMvCFer9zc6WnWxz2Ikjyg/TClZUAWk/bzL610BXAAp2OidLmRj5ApsHewKBgQDK2G6p3CwK9RuiK4adTkemfGPulVPvHWhYSk0vSTx0Y5y8QbVnctrhTiqkVjt2nEnPI80B4xHIqnzH5WDmhObohCrWVdRcPEj3Pfo5Pa40paTgUm7NsHmAUSTQm/578Fh6+bQFdCgLQ47kGh3h5Qw6Y3Sfs1VBgS1j/reg9g5NVQKBgQCwYuhpL+vpg+L5skirrs6dq0/2x5eq7nKFBiDG08XaHMC3uDIH/NQbjJBY6z/+fClUbs2lDllqXOB8v6YFXyISIBwkxPRRZKBaIbALwavn+ob4jJdXJwf4Vd4kK5TlJuBAxoLVnDR7dG8Yqyk+a/ZftC6YyPMlydpZu+/vf0ZFWQKBgQCmnfEIhVmIBp2UL7x7Kfmgvzw2lWJEXxt+qkmMNdP6pnfwCuk++BDiUJqmMk7VtrrJ8tZKXoIVGjU03S0jLLee9jFZrVaqDLrCm54VtpyJkiZEPHHXcsXaqMxcrkXy+BE1sjY05Jyf4/ZTV0CEdOf+bgM1ytCU5c6q/GdJR9OnoQKBgQCAApKXk/G3TmoMtCQXH5RgQj8XKreTZrkO3SZs2icAhizVFIfGW3i/GYzVrgVVbvGNFVfbRkcIc572fcvgwZ3wW14ScBCOAqTrToifNqEzpixYPEN6SEqlqMsLnkkgrLUWQbAO4EiRemr1Gfs+jO6uzltvRdG5BRNodKizoU8cfw==";

    /**
     * RSA私钥解密
     * @param data
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data) {
        try {
            byte[] keyBytes = Base64Util.decodeToByte(SERVER_PRIKEY);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            //对数据解密
            //公钥生成的密文相同 Cipher cipher = Cipher.getInstance(TRANSFORMATION, new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64Util.decodeToByte(data)), ENCODE);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * RSA公钥加密
     * @param data
     * @return
     */
    public static String encryptByPublicKey(String data) {
        try {
        byte[] keyBytes = Base64Util.decodeToByte(CLIENT_PUBKEY);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        //对数据加密
        //公钥生成的密文相同 Cipher.getInstance(TRANSFORMATION, new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64Util.encodeByte(cipher.doFinal(data.getBytes(ENCODE)));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * RSA私钥签名
     * @param data
     * @return
     * @throws Exception
     */
    public static String signRSA(String data) {
        try {
            byte[] keyBytes = Base64Util.decodeToByte(SERVER_PRIKEY);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_);
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            //对数据签名
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes(ENCODE));
            return Base64Util.encodeByte(signature.sign());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * RSA验证签名
     * @param data
     * @param sign
     * @return
     * @throws Exception
     */
    public static boolean verifySignRSA(String data, String sign) {
        try {
            byte[] keyBytes = Base64Util.decodeToByte(SERVER_PUBKEY);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_);
            PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(data.getBytes(ENCODE));
            return signature.verify(Base64Util.decodeToByte(sign));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成公钥和私钥
     * @param seed
     * @return
     */
    public static Map<String, String> createRSAKey(String seed) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM_);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed.getBytes());
            generator.initialize(KEYSIZE, random);
            KeyPair keyPair = generator.generateKeyPair();

            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            Map<String, String> keyMap = new HashMap<String, String>(2);
            keyMap.put(PUBLICKEY, Base64Util.encodeByte(publicKey.getEncoded()));
            keyMap.put(PRIVATEKEY, Base64Util.encodeByte(privateKey.getEncoded()));
            return keyMap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 使用示例
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String source = "szmpyd20181107sunsyx";//原文
        String seed = "szmpyd201811078899";//种子
        System.out.println("加密前：\n" + source);
        Map<String, String> keyMap = RSA.createRSAKey(seed);//初始化密钥
        String publicKey = keyMap.get(PUBLICKEY);//公钥
        String privateKey = keyMap.get(PRIVATEKEY);//私钥
        //String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuFPdRBaE6OnqQsXvVim5mOmXhXidOa16gLIV9sI2dRZW4ZejlE+zGIsAlbsDYagO+RkjgVeZd4oZ+P7roYNlLtY8YujNkMnhMqHHKAOXUqK6HMBzrCsu/JaMFNsYW7EyEnXnd1Lq15U85O1irXLYfBbrGjKrYlKv5p+3xUYKRRc+PdeYDCKPKLKl9vgZwACVYnYEEuM2ErxdYWRARukSIEOtO69X6C1xlJL+MnQlFMTWLvg2m3pvAHqa/ZMJLwFE9mf+29LnjAJJvPWjMCUGroU0Hjoq+cPTJp5EfPM6jHlIhHzl8oZCHvNRFgOkXz5aFqNbmg5m0vs8vugBGXN61wIDAQAB";
        //String privateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC4U91EFoTo6epCxe9WKbmY6ZeFeJ05rXqAshX2wjZ1Flbhl6OUT7MYiwCVuwNhqA75GSOBV5l3ihn4/uuhg2Uu1jxi6M2QyeEyoccoA5dSorocwHOsKy78lowU2xhbsTISded3UurXlTzk7WKtcth8FusaMqtiUq/mn7fFRgpFFz4915gMIo8osqX2+BnAAJVidgQS4zYSvF1hZEBG6RIgQ607r1foLXGUkv4ydCUUxNYu+Dabem8Aepr9kwkvAUT2Z/7b0ueMAkm89aMwJQauhTQeOir5w9MmnkR88zqMeUiEfOXyhkIe81EWA6RfPloWo1uaDmbS+zy+6AEZc3rXAgMBAAECggEACzVGQgqggE0g6niAqhuVZn/lr776yHQM++BYR43do9b853DCCJWkDTz+hxw9A2Ylqu3lY8sA0Oz70VEm4MZNk8CBIX6IaWJtKBgANSWqwOKWMINfRTvG+qLo16fO3lxy7/5tWasYDo3LixwIIQXXtgvqtcgY9byTZ5GUqhEtNLAvpOjFBpxvSROaMrM0qwHd7nTUJLwK/aPZDhdKET3zYofjGQo8AE9qcvGIdqOdXRYnfpAvcMKF5BK5mJWkaaoK4WwJ/uHXUEp7SEWoGBATykKVPSDfiXDYul0ePuHWEr3Cqn0Hq2sM1Lxntmt1yGhlRF5FkmVSzjxZXuQkaDECiQKBgQDooTELhV+vd5BhX9BYpfrpnDWBos6zWmO22USq0FGszXc780YgWNZF3GlJ2mrq8MVAr5TyPApuR4/di7/6wE0TkTVLSGXUuvSD+jM3Nv+I17VA/fWXmMvCFer9zc6WnWxz2Ikjyg/TClZUAWk/bzL610BXAAp2OidLmRj5ApsHewKBgQDK2G6p3CwK9RuiK4adTkemfGPulVPvHWhYSk0vSTx0Y5y8QbVnctrhTiqkVjt2nEnPI80B4xHIqnzH5WDmhObohCrWVdRcPEj3Pfo5Pa40paTgUm7NsHmAUSTQm/578Fh6+bQFdCgLQ47kGh3h5Qw6Y3Sfs1VBgS1j/reg9g5NVQKBgQCwYuhpL+vpg+L5skirrs6dq0/2x5eq7nKFBiDG08XaHMC3uDIH/NQbjJBY6z/+fClUbs2lDllqXOB8v6YFXyISIBwkxPRRZKBaIbALwavn+ob4jJdXJwf4Vd4kK5TlJuBAxoLVnDR7dG8Yqyk+a/ZftC6YyPMlydpZu+/vf0ZFWQKBgQCmnfEIhVmIBp2UL7x7Kfmgvzw2lWJEXxt+qkmMNdP6pnfwCuk++BDiUJqmMk7VtrrJ8tZKXoIVGjU03S0jLLee9jFZrVaqDLrCm54VtpyJkiZEPHHXcsXaqMxcrkXy+BE1sjY05Jyf4/ZTV0CEdOf+bgM1ytCU5c6q/GdJR9OnoQKBgQCAApKXk/G3TmoMtCQXH5RgQj8XKreTZrkO3SZs2icAhizVFIfGW3i/GYzVrgVVbvGNFVfbRkcIc572fcvgwZ3wW14ScBCOAqTrToifNqEzpixYPEN6SEqlqMsLnkkgrLUWQbAO4EiRemr1Gfs+jO6uzltvRdG5BRNodKizoU8cfw==";
        System.out.println("公钥：\n" + publicKey);
        System.out.println("私钥：\n" + privateKey);

        String encodedStr = RSA.encryptByPublicKey(source);//加密
        System.out.println("加密后：\n" + encodedStr);
        encodedStr = URLEncoder.encode(encodedStr, "UTF-8");
        System.out.println(encodedStr);
        encodedStr = URLDecoder.decode(encodedStr, "UTF-8");
        System.out.println(encodedStr);
        String decodedStr = RSA.decryptByPrivateKey(encodedStr);//解密
        System.out.println("解密后：\n" + decodedStr);

        String sign = RSA.signRSA(source);
        System.out.println("签名后：\n" + sign);
        System.out.println("验证签名结果：\n" + RSA.verifySignRSA(source, sign));

        System.out.println();
        System.out.println(MD5.md5Salt("aaabbbcc"));
        System.out.println("-----------------------------");
        System.out.println(MD5.md5("redsun20171130").substring(0,18));

        String test = decryptByPrivateKey("ccSe/nPuFsT04YdyJEILvYS3ICAQzaLP31vQDaXvC5AAck4Bl3/8ELG5HH4xmqv0FJ4b+Bc8u8MjGPb2VF9SJIiwXGJ9wmsFV6rUYG6VEES7hvs9yXWek/1AdqRBlJq1jju4Wb06Rlb5380qIhrfG3R51gAIUKUL0QP4lC8fTE8JXXBb92zC7IS0LPRF2p5J0KwBM7LtGyuO9Ef/1nsWgpJgpIJX/9HHLI3K4khjDa/pzgjACRnJz09CDBM7OZKj7U7tqGg8SYH4dbq7xWmnvOCM2VC0govF225rLlus5PA3ADJk1yG7KH7z5+/go0PSakbmVoLLY0oK0dN+guahbQ==");
        System.out.println(test);
    }
}
