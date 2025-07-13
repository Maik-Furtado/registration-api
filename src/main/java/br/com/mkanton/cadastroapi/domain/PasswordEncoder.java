package br.com.mkanton.cadastroapi.domain;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordEncoder {

    private final Argon2 argon2;

    public PasswordEncoder() {
        this.argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    }

    public String hashPassword(String password) {
        int parallelism = 2;
        int memory = 65536;
        int iterations = 4;

        return argon2.hash(iterations, memory, parallelism, password.toCharArray());
    }



    public boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            return false;
        }
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        return argon2.verify(hashedPassword, rawPassword.toCharArray());
    }

}
