package aicoach.service;

import aicoach.dao.UserDao;
import aicoach.model.User;
import aicoach.util.Crypto;
import aicoach.util.Validators;

/**
 * @file authservice.java
 * @brief logica de autentificare si inregistrare utilizatori.
 *
 * valideaza datele de intrare verifica parola prin compararea hash-ului si foloseste userdao pentru acces la db.
 */
public final class AuthService {

    /** dao pentru operatii pe tabela users (cautare/inserare) */
    private final UserDao userDao = new UserDao();

    /**
     * autentifica un utilizator pe baza emailului si parolei.
     *
     * @param email emailul introdus de utilizator.
     * @param password parola introdusa de utilizator
     * @return user daca autentificarea reuseste altfel null.
     * @throws illegalargumentexception daca emailul/parola sunt invalide
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
     * inregistreaza un utilizator nou si il returneaza din db.
     *
     * @param email emailul pentru cont.
     * @param password parola pentru cont.
     * @return user creat (citit din db dupa inserare).
     * @throws illegalargumentexception daca emailul/parola sunt invalide sau emailul este deja folosit.
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
