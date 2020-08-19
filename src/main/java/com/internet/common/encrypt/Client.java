// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.common.encrypt;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Kael He (kael.he@alo7.com)
 */
public class Client {
    public static void main(String[] args) throws Exception {
        String keyAlgorithm = "RSA";
        String signAlgorithm = "SHA256withRSA";
        String encryptText = "这是我的加密文本";
        String signText = "这是我的签名文本";
        Pair<String, String> keyPair = RASEncrypt.genKeyPair(keyAlgorithm);
        String publicKeyString = keyPair.getLeft();
        String privateKeyString = keyPair.getRight();
        String encryptedText = RASEncrypt.encrypt(encryptText, publicKeyString, keyAlgorithm);
        String decryptedText = RASEncrypt.decrypt(encryptedText, privateKeyString, keyAlgorithm);
        System.out.println(String.format("加密后的模样: %s", encryptedText));
        assert encryptText.equals(decryptedText);
        System.out.println("加解密成功！");
        String signedText = RASEncrypt.sign(signText, privateKeyString, signAlgorithm, keyAlgorithm);
        System.out.println(String.format("签名后的模样: %s", signedText));
        boolean check = RASEncrypt.checkSign(signText, signedText, publicKeyString, signAlgorithm, keyAlgorithm);
        System.out.println(String.format("校验签名: %s", check));
    }
}
