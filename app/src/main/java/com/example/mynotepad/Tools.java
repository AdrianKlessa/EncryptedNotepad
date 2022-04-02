package com.example.mynotepad;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;

public class Tools {

    public static int blockSize = 16;
    static int iterations = 2000;
    static int keyLength = 256;
    public static String currentPassword;
    public static Context applicationContext;
    public static int seedBytes = 20;
    public static int hashBytes = 20;
    public static boolean checkIfFileExists(String filepath){
        File contextPath =  applicationContext.getFilesDir();

        File f = new File(contextPath,filepath);
        return f.isFile();
    }

    //Uses the current context - use relative path
    public static void saveStringToFile(String filepath, String data) throws IOException {
        File contextPath =  applicationContext.getFilesDir();
        File f = new File(contextPath, filepath);


        if(!f.exists()){
            f.createNewFile();
        }
        FileOutputStream stream = new FileOutputStream(f);
        stream.write(data.getBytes(StandardCharsets.UTF_8));
        stream.flush();
        stream.close();
    }
    //Uses the current context - use relative path
    public static String LoadLineFromFile(String filepath) throws Exception{
        File contextPath =  applicationContext.getFilesDir();
        File f = new File(contextPath, filepath);
        Scanner myScanner = new Scanner(f);
        return myScanner.nextLine();

    }
    //Remember to use BASE64
    public static String decrypt(String password, String salt, byte[] iv, byte[] encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
        System.out.println("Decryption input: "+new String(encryptedData));
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt.getBytes(StandardCharsets.UTF_8),
                iterations,
                keyLength
        );

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        SecretKey key = factory.generateSecret(spec);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] original = cipher.doFinal(encryptedData);
        System.out.println("Decrypted text: "+new String(original, "UTF8"));
        return new String(original, "UTF8");
    }
    //Remember to use BASE64
    public static byte[] encrypt(String password, String salt, byte[] iv, String originalString) throws Exception{
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt.getBytes("UTF8"),
                iterations,
                keyLength
        );


        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKey key = factory.generateSecret(spec);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(originalString.getBytes("UTF8"));
        System.out.println("Encrypted text: "+new String(encrypted, "UTF8"));
        return encrypted;
        //return new String(encrypted,"UTF8");
    }

    public static String stringToBase64(String string){
        System.out.println("Input string 1: "+string);
        System.out.println(Base64.encodeToString(string.getBytes(),Base64.NO_WRAP));
        return Base64.encodeToString(string.getBytes(StandardCharsets.UTF_8),Base64.NO_WRAP);

    }
    public static String base64ToString(String string){
        System.out.println("Input string 2: "+string);
        byte[] decodedString = Base64.decode(string, Base64.NO_WRAP);
        System.out.println("Decoded string: "+new String(decodedString));
        return new String(decodedString, StandardCharsets.UTF_8);
    }

    public static String generateSalt(){
        SecureRandom rng = new SecureRandom();
        byte[] salt = rng.generateSeed(seedBytes);
        return Base64.encodeToString(salt,Base64.NO_WRAP);
    }

    public static String hashPassword(String password, String salt) throws Exception{
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt.getBytes(StandardCharsets.UTF_8));
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        String base64EncodedHash = Base64.encodeToString(hashedPassword,Base64.NO_WRAP);
        return base64EncodedHash;
    }

    public static byte[] generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public static void encryptAndSave(String password, String salt, byte[] iv, String originalString, String filepath) throws Exception{
        byte[] encrypted = Tools.encrypt(password,salt,iv,originalString);
        String inBase64 = Base64.encodeToString(encrypted,Base64.NO_WRAP);
        Tools.saveStringToFile(filepath,inBase64);
    }

    public static String decryptLoaded(String password, String salt, byte[] iv, String encryptedInBase64) throws Exception{
        byte[] encrypted = Base64.decode(encryptedInBase64,Base64.NO_WRAP);
        String decrypted = decrypt(password,salt,iv,encrypted);
        return decrypted;
    }
}
