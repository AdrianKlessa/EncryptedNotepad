package com.example.mynotepad;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.example.mynotepad", appContext.getPackageName());
    }

    @Test
    public void base64BackAndForth(){
        String testString = "This is a test string";
        boolean result = testString.equals(Tools.base64ToString(Tools.stringToBase64(testString)));
        assertEquals(true,result);

    }

    @Test
    public void encryptionDecryptionTest() throws Exception {
        String testString = "This is a test string";
        String textInBase64 = Tools.stringToBase64(testString);
        String password ="secret";
        String salt = Tools.generateSalt();
        byte[] iv = Tools.generateIv();
        byte[] encrypted = Tools.encrypt(password,salt, iv, textInBase64);
        String decrypted = Tools.decrypt(password, salt,iv, encrypted);
        String decryptedText= Tools.base64ToString(decrypted);
        boolean result = testString.equals(decryptedText);
        assertEquals(true,result);

    }
}