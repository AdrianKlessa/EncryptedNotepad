package com.example.mynotepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mynotepad.databinding.ActivityMainBinding;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {


    private String enteredPassword="";
    private String savedHash="";
    private String savedNotePlaintext="";
    private String savedNoteEncryptedBase64="";
    private String savedSalt="";
    private String savedIV="";
    private final String hashFilepath="hash.txt";
    private final String noteFilepath="note.txt";
    private final String saltFilepath="salt.txt";
    private final String ivFilepath = "iv.txt";
    private boolean noNote = false; //Marks whether or not we are starting the app for the first time - no password, no note, no salt.
    //Alternatively, we assume the same if one of the files does not exist for whatever reason
    private ActivityMainBinding binding;
    Context context;
    private void init(){
        NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment_content_main);

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();
        Tools.applicationContext=context;

       // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //NavigationUI.setupWithNavController(R.id.nav_host_fragment_content_main, navController);

        setContentView(R.layout.fragment_note);
        setContentView(R.layout.fragment_change_password);
        setContentView(R.layout.fragment_password);
        if(!Tools.checkIfFileExists(hashFilepath)){
            Log.i(null,"No hash");
        }
        if(!Tools.checkIfFileExists(noteFilepath)){
            Log.i(null,"No note");
        }
        if(!Tools.checkIfFileExists(saltFilepath)){
            Log.i(null,"No salt");
        }

        if(!Tools.checkIfFileExists(hashFilepath)||!Tools.checkIfFileExists(noteFilepath)||!Tools.checkIfFileExists(saltFilepath)){
            noNote=true;
        }


        if(noNote){
            setContentView(R.layout.fragment_change_password);
        }

    }


    //Button on the first screen for checking if the password is correct
    public void buttonEnterPassword(View view) {
       /*

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navCo = navHostFragment.getNavController();
        navCo.navigate(R.id.action_fragmentPassword_to_fragmentNote);
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navCo = navHostFragment.getNavController();
        navCo.navigate(R.id.action_fragmentPassword_to_fragmentNote);

        */
        String calculatedHash="";
        try {
            loadNote();
            EditText passwordInput = findViewById(R.id.passwordInput);
            enteredPassword = passwordInput.getText().toString();
            calculatedHash = Tools.stringToBase64(Tools.hashPassword(enteredPassword,savedSalt));

            if(!calculatedHash.equals(savedHash)){
                Toast.makeText(context, "The password appears to be wrong",
                        Toast.LENGTH_LONG).show();
            }else{
                //savedNotePlaintext = new String(Tools.decrypt(enteredPassword,savedSalt,savedIV.getBytes(StandardCharsets.UTF_8),Tools.base64ToString(savedNoteEncryptedBase64).getBytes(StandardCharsets.UTF_8)));
                savedNotePlaintext = Tools.decryptLoaded(enteredPassword,savedSalt,savedIV.getBytes(StandardCharsets.UTF_8),savedNoteEncryptedBase64);
                setContentView(R.layout.fragment_note);
                EditText actualNote = findViewById(R.id.EditTextNote);
                actualNote.setText(savedNotePlaintext);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }





        //Navigation.findNavController(view).navigate(R.id.action_fragmentPassword_to_fragmentNote);
        }




    public void buttonGoToChangePassword(View view) {
        //Navigation.findNavController(view).navigate(R.id.action_fragmentNote_to_fragmentChangePassword);
        EditText actualNote = findViewById(R.id.EditTextNote);
        savedNotePlaintext=actualNote.getText().toString();
        setContentView(R.layout.fragment_change_password);
    }
    //Button for changing password, check if the length is acceptable and then go to the note after changing the pass and re-encrypting the note
    public void buttonChangePassword(View view) {
       // Navigation.findNavController(view).navigate(R.id.action_fragmentChangePassword_to_fragmentNote);

        try {
            EditText passwordTextField = findViewById(R.id.newPasswordEditText);
            String newPassword = passwordTextField.getText().toString();
            if (newPassword.length() >= 15) {
                enteredPassword = newPassword;
                savedSalt = Tools.stringToBase64(Tools.generateSalt());
                savedIV = Tools.stringToBase64(Tools.generateIv().toString());
                savedHash = Tools.stringToBase64(Tools.hashPassword(newPassword, savedSalt));
                saveNote();
                noNote=false;

                setContentView(R.layout.fragment_note);
                EditText actualNote = findViewById(R.id.EditTextNote);
                actualNote.setText(savedNotePlaintext);
            } else {
                Toast.makeText(context, "For security please use a password that is at least 15 characters long",
                        Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void buttonSave (View view){
        EditText actualNote = findViewById(R.id.EditTextNote);
        savedNotePlaintext=actualNote.getText().toString();

        try {
            saveNote();
            cleanMemory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.fragment_password);
    }

    //Overwrites the password hash, the salt and the encrypted (!) note
    private void saveNote() throws Exception{
        try {
            Tools.saveStringToFile(hashFilepath,savedHash);
            Tools.saveStringToFile(ivFilepath,savedIV);
            Tools.saveStringToFile(saltFilepath,savedSalt);
            //savedNoteEncryptedBase64 = Tools.stringToBase64(new String(Tools.encrypt(enteredPassword,savedSalt,savedIV.getBytes(StandardCharsets.UTF_8),savedNotePlaintext)));
            Tools.encryptAndSave(enteredPassword,savedSalt,savedIV.getBytes(StandardCharsets.UTF_8),savedNotePlaintext,noteFilepath);


           // String encryptedInBASE64 = Tools.stringToBase64(savedNotePlaintext);
           // byte[] encrypted = Tools.encrypt(enteredPassword,savedSalt,savedIV.getBytes(StandardCharsets.UTF_8),encryptedInBASE64);
           // savedNoteEncryptedBase64 = Tools.stringToBase64(new String(encrypted,StandardCharsets.UTF_8));
            //Tools.saveStringToFile(noteFilepath,savedNoteEncryptedBase64);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadNote() throws Exception{
        savedHash = Tools.LoadLineFromFile(hashFilepath);
        savedNoteEncryptedBase64 = Tools.LoadLineFromFile(noteFilepath);
        savedSalt = Tools.LoadLineFromFile(saltFilepath);
        savedIV = Tools.LoadLineFromFile(ivFilepath);
    }

    private void cleanMemory(){
        enteredPassword="";
        savedHash="";
        savedNotePlaintext="";
        savedNoteEncryptedBase64="";
        savedSalt="";
        savedIV="";
    }
}