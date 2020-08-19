// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.common.encrypt;

import org.apache.commons.lang3.tuple.Pair;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * @author Kael He (kael.he@alo7.com)
 */
public class RASEncrypt {


    private static KeyPairGenerator getKeyPairGenerator(String keyAlgorithm) throws NoSuchAlgorithmException {
        return KeyPairGenerator.getInstance(keyAlgorithm);
    }

    private static Cipher getCipher(String keyAlgorithm) throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(keyAlgorithm);
    }

    private static KeyFactory getKeyFactory(String keyAlgorithm) throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(keyAlgorithm);
    }

    private static RSAPublicKey getPublicKey(String publicKeyString, String keyAlgorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = getKeyFactory(keyAlgorithm);
        // 在生成公密钥的时候，打印出来了如下日志:
        // 公钥加密算法：RSA, 主要编码格式：X.509
        // 密钥加密算法：RSA, 主要编码格式：PKCS#8
        // 可知公钥是用X.509加密的
        return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(Base64Util.decode(publicKeyString)));
    }

    private static RSAPrivateKey getPrivateKey(String privateKeyString, String keyAlgorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
        // 在生成公密钥的时候，打印出来了如下日志:
        // 公钥加密算法：RSA, 主要编码格式：X.509
        // 密钥加密算法：RSA, 主要编码格式：PKCS#8
        // 可知密钥是用PKCS#8加密的
        return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64Util.decode(privateKeyString)));
    }

    public static Pair<String, String> genKeyPair(String keyAlgorithm) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = getKeyPairGenerator(keyAlgorithm);
        // 初始化钥匙对生成器，指定钥匙大小为1024
        keyPairGenerator.initialize(1024);
        // 生成钥匙对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        String publicKeyString = Base64Util.encodeToString(publicKey.getEncoded());
        String privateKeyString = Base64Util.encodeToString(privateKey.getEncoded());
        System.out.println(String.format("公钥加密算法：%s, 主要编码格式：%s",
                publicKey.getAlgorithm(), publicKey.getFormat()));
        System.out.println(String.format("密钥加密算法：%s, 主要编码格式：%s",
                privateKey.getAlgorithm(), privateKey.getFormat()));
        return Pair.of(publicKeyString, privateKeyString);
    }

    public static String encrypt(String text, String publicKeyString, String keyAlgorithm)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        RSAPublicKey publicKey = getPublicKey(publicKeyString, keyAlgorithm);
        // 实例化一个密码器
        Cipher cipher = getCipher(keyAlgorithm);
        // 初始化密码器模式为加密模式，传入钥匙（此时是公钥，当然也可以传入密钥，看使用场景）
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(text.getBytes());
        return Base64Util.encodeToString(encrypted);
    }

    public static String decrypt(String text, String privateKeyString, String keyAlgorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] decodedText = Base64Util.decode(text);
        RSAPrivateKey privateKey = getPrivateKey(privateKeyString, keyAlgorithm);
        // 实例化一个密码器
        Cipher cipher = getCipher(keyAlgorithm);
        // 初始化密码器模式为解密模式，传入钥匙（此时是密钥，当然也可以传入共钥，看使用场景）
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypted = cipher.doFinal(decodedText);
        return Arrays.toString(decrypted);
    }

    public static String sign(String text, String privateKeyString, String signAlgorithm,
                                     String keyAlgorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        // 实例化签名，并指定签名算法
        Signature signature = Signature.getInstance(signAlgorithm);
        // 初始化签名，并传入密钥
        signature.initSign(getPrivateKey(privateKeyString, keyAlgorithm));
        // 传入待签名的文本
        signature.update(text.getBytes());
        byte[] signed = signature.sign();
        return Base64Util.encodeToString(signed);
    }

    public static boolean checkSign(String text, String signedText, String publicKeyString, String signAlgorithm,
                                    String keyAlgorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        // 实例化签名，并指定签名算法
        Signature signature = Signature.getInstance(signAlgorithm);
        // 初始化签名校验器
        signature.initVerify(getPublicKey(publicKeyString, keyAlgorithm));
        // 传入待校验的文本
        signature.update(text.getBytes());
        // 校验
        return signature.verify(Base64Util.decode(signedText));
    }

}
