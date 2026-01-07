package aicoach.service;

import aicoach.dao.UserDao;
import aicoach.model.User;
import aicoach.util.Crypto;
import aicoach.util.Validators;

/**
 * @file AuthService.java
 * @brief Logica de autentificare si inregistrare utilizatori.
 *
 * Valideaza datele de intrare, verifica parola prin compararea hash-ului si foloseste UserDao pentru acces la DB.
 */
public final class AuthService {

    /** DAO pentru operatii pe tabela users (cautare/inserare) */
    private final UserDao userDao = new UserDao();

    /**
     * Autentifica un utilizator pe baza emailului si parolei.
     *
     * @param email Emailul introdus de utilizator.
     * @param password Parola introdusa de utilizator
     * @return User daca autentificarea reuseste, altfel null.
     * @throws IllegalArgumentException Daca emailul/parola sunt invalide
     */
    public User login(String email, String password) {
        Validators.require(Validators.isEmail(email), "Email invalid.");
        Validators.require(password != null && password.length() >= 4, "Parola prea scurta.");
        User u = userDao.findByEmail(email.trim());
        if (u == null) return null;
        String hash = Crypto.sha256(password);
        if (!hash.equals(u.passwordHash())) return null;
        return u;
    }

    /**
     * Inregistreaza un utilizator nou si il returneaza din DB.
     *
     * @param email Emailul pentru cont.
     * @param password Parola pentru cont.
     * @return User creat (citit din DB dupa inserare).
     * @throws IllegalArgumentException Daca emailul/parola sunt invalide sau emailul este deja folosit.
     */
    public User register(String email, String password) {
        Validators.require(Validators.isEmail(email), "Email invalid.");
        Validators.require(password != null && password.length() >= 4, "Parola prea scurta (minim 4).");
        String e = email.trim();
        if (userDao.findByEmail(e) != null) throw new IllegalArgumentException("Email deja folosit.");
        long id = userDao.insert(e, Crypto.sha256(password));
        return userDao.findById(id);
    }
}
