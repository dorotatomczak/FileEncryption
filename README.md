# FileEncryption

Żeby działała baza danych:
1. Download the sqljdbc_6.0.8112.100_enu.exe from Microsoft Site
2. Install the exe (read the instructions in the zip path)
3. copy sqljdbc_4.0/enu/auth/x64/sqljdbc_auth.dll to
	Java/jre7/bin and to
	Java/jre7/lib
4. Utworzyć bazę danych o nazwie BskDB, wykonać db/create.sql

Działanie programu:
1. Klient wybiera button "Encrypt" -> metoda encryptFile w MainController.java -> po wybraniu pliku uruchamia się task EncryptTask. Na razie na sztywno są ustawione jego argumenty. Trzeba będzie dorobić pola i inne takie, żeby wczytywać input od użytkownika (tryb, dlugość podbloku dla trybów CFB i OFB).
2. ładowany jest public key obecnie zalogowanego użytkownika (rsa keys są tworzone po rejestracji). To trzeba bedzie zmienic na public key wybranego użytkownika z listy (albo użytkownikow, nwm). Tworzony jest json z danymi do enkrypcji (narazie public key, fileName, tryb)
3. Wysyłany jest json 
4. Serwer odbiera jsona od klienta, tworzy obiekt Blowfish a wraz z nim klucz sesyjny. Tworzy jsona z danymi do dekrypcji dla klienta i tam zamieszcza m.in. zaszyfrowany kluczem publicznym klucz sesyjny. Zapisuje jsona do pliku
5. Serwer wybiera plik i jednocześnie go szyfruje i dopisuje do pliku z jsonem
6. serwer wysyła plik klientowi.
7. Klient odbiera plik (folder encrypted)
8. Klient wybiera button "Decrypt" -> metoda decryptFile w MainController.java -> po wybraniu pliku uruchamia się task DecryptTask. Klient odczytuje jsona z pliku. Deszyfruje klucz sesyjny swoim kluczem prywatnym.

Nie ma deszyfrowania, szyfrowanie najbardziej podstawowe (dla cfb i ofb nie ma tych podbloków ale jeszcze nwm o co z tym chodzi)
Aa, jeszcze trzeba w jsonie zwrotnym dodac info o wielkosci klucza sesyjnego chyba (16 bajtów) - wlasciwie nwm po co to, ale jest napisane w instrukcji
