# EncryptedNotepad

An Android application that functions as an encrypted notepad. Uses the Bouncy Castle API for encryption and password hashing.

* Uses AES in Counter mode
* IV generated through SecureRandom
* Password hashed with salt for checking whether there is any point in even attempting decryption with the user-provided password
* Password can be changed after the user provides the previous password correctly
