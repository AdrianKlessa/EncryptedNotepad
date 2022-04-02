# EncryptedNotepad

An Android application that functions as an encrypted notepad. Uses the Bouncy Castle API for encryption and password hashing.

* Uses AES in Counter mode
* IV generated through SecureRandom
* Password hashed with salt for checking whether there is any point in even attempting decryption with the user-provided password
* Password can be changed after the user provides the previous password correctly


![Preview image 1](https://raw.githubusercontent.com/AdrianKlessa/EncryptedNotepad/main/scr1.png)

![Preview image 2](https://raw.githubusercontent.com/AdrianKlessa/EncryptedNotepad/main/scr2.png)

![Preview image 3](https://raw.githubusercontent.com/AdrianKlessa/EncryptedNotepad/main/scr3.png)

![Preview image 4](https://raw.githubusercontent.com/AdrianKlessa/EncryptedNotepad/main/scr4.png)
