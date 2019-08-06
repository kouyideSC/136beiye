package com.caipiao.common.shuangqian;

import cfca.sadk.algorithm.common.Mechanism;
import cfca.sadk.algorithm.common.PKIException;
import cfca.sadk.lib.crypto.JCrypto;
import cfca.sadk.util.CertUtil;
import cfca.sadk.util.KeyUtil;
import cfca.sadk.util.Signature;
import cfca.sadk.x509.certificate.X509Cert;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Kouyi on 2018/10/25.
 */
public class CertificateUtil {
    private static final String deviceName = JCrypto.JSOFT_LIB;
    private static cfca.sadk.lib.crypto.Session session = null;

    static {
        try {
            JCrypto jCrypto = JCrypto.getInstance();
            jCrypto.initialize(deviceName, null);
            session = jCrypto.openSession(deviceName);
        } catch (PKIException e) {
            e.printStackTrace();
        }
    }


    /******* p1 *********/
    /**
     * p1消息签名
     * @param message
     * @return
     * @throws Exception
     */
    public static String signMessageByP1(String message, String pfxPath, String passWord) throws Exception{
        PrivateKey userPriKey = KeyUtil.getPrivateKeyFromPFX(new FileInputStream(pfxPath), passWord);
        Signature signature = new Signature();
        byte[] base64P7SignedData = signature.p1SignMessage(Mechanism.SHA256_RSA, message.getBytes("UTF-8"), userPriKey, session);
        return new String(base64P7SignedData);
    }

    /**
     * p1消息校验(公钥证书验签)
     * @param beforeSignedData
     * @param afterSignedData
     * @param certPath
     * @return
     * @throws Exception
     */
    public static boolean verifyMessageByP1(String beforeSignedData, String afterSignedData, String certPath) throws Exception{
        X509Cert cert = new X509Cert(new FileInputStream(certPath));
        PublicKey publicKey = cert.getPublicKey();
        Signature signature = new Signature();
        return signature.p1VerifyMessage(Mechanism.SHA256_RSA, beforeSignedData.getBytes("UTF-8"), afterSignedData.getBytes("UTF-8"), publicKey, session);
    }

    /**
     * p1消息校验(公钥字符串验签)
     * @param beforeSignedData
     * @param afterSignedData
     * @param publicKeyStr
     * @return
     * @throws Exception
     */
    public static boolean verifyMessageByP1AndPubKey(String beforeSignedData, String afterSignedData, String publicKeyStr) throws Exception{
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec((new BASE64Decoder()).decodeBuffer(publicKeyStr));
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        Signature signature = new Signature();
        return signature.p1VerifyMessage(Mechanism.SHA256_RSA, beforeSignedData.getBytes("UTF-8"), afterSignedData.getBytes("UTF-8"), publicKey, session);
    }

    /****** p7 ******/
    /**
     * P7 分离式文件签名（签名）
     */
    public static String signData(String toBeSigned, String certPath, String certPass) throws Exception {
        X509Cert cert = CertUtil.getCertFromPFX(certPath, certPass);
        PrivateKey priKey = KeyUtil.getPrivateKeyFromPFX(certPath, certPass);
        Signature signature = new Signature();
        return new String(signature.p7SignMessageDetach(Mechanism.SHA256_RSA, toBeSigned.getBytes("UTF8"), priKey, cert, session), "UTF8");
    }

    /**
     * P7 分离式消息校验（验签）
     */
    public static boolean verifySignature(String data, String signdata) throws Exception {
        Signature signature = new Signature();
        return signature.p7VerifyMessageDetach(data.getBytes("UTF8"), signdata.getBytes("UTF8"), session);
    }

    public static void main(String[] args) throws Exception {
        String pfxPath = "D:\\certificate\\168885_test.pfx";
        String certPath = "D:\\certificate\\168885_test.cer";
        String password = "123123";

        String plaintext = "幸福是你有食物吃,睡觉的地方,有所爱的人。";
        String plaintext1 = "幸福是你有食物吃,睡觉的地方,有所爱的人";

        /******* p1 ******/
        //签名
        String base64P7SignedData = signMessageByP1(plaintext, pfxPath, password);
        //验签
        boolean verifyByp1 = verifyMessageByP1(plaintext, base64P7SignedData, certPath);
        System.out.println("p1-cert:"+verifyByp1);

        //验签
        String publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApSC4H4PvPuS9GJq9chCHq"
                + "PHb+MK2dYRwVlU+9LJHhEA0mbmkhbSyvcakHuvrXtrBCBt5GMSU2BQeZy2IqQoZDJ"
                + "Cn5CHufgMUpyMD7qvRo+GOg3GRC3k506ebb/Od/LL0eMAcCiOcCC7HHiPGP44VtBs"
                + "OgqX22/BSAxyK93bnQbb4+8sc4id0io403rLjBle7vIzrNJtqftuTSQJMm/OmRDvf"
                + "hg0asdUZYCsb3TdhRqO5hblDl/s/5b6gFTYcgPAw9qKdknqAWGqHP/J6i3GDAqedq"
                + "7lFuDvkqSnYnWgVzpv9luWzrvXYOl2K4fvDSl9JIXHUMMz9cELEJjmq7yM+fQIDAQ"
                + "AB";
        boolean verifyByp1PublicKeyStr = verifyMessageByP1AndPubKey(plaintext, base64P7SignedData, publicKeyStr);
        System.out.println("p1-PublicKeyStr:"+verifyByp1PublicKeyStr);

        File file = new File("D:\\certificate\\168885_test.cer");
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate)cf.generateCertificate(new FileInputStream(file));
        PublicKey publicKey = cert.getPublicKey();
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String publicKeyString = base64Encoder.encode(publicKey.getEncoded());
        System.out.println("-----------------公钥--------------------");
        System.out.println(publicKeyString);
        System.out.println("-----------------公钥--------------------");
    }

}
