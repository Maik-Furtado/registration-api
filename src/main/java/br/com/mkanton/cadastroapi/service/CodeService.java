package br.com.mkanton.cadastroapi.service;

import br.com.mkanton.cadastroapi.dataaccess.IUserDao;
import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.domain.enums.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Service responsible for generating and validating verification codes,
 * used in user registration or password recovery.
 */
@ApplicationScoped
public  class CodeService {


    @Inject
    private IUserDao userDao;

    Logger logger = LoggerFactory.getLogger(CodeService.class);


    // Length of the numeric code (e.g., 4 digits)
    private static final int CODE_LENGTH = 4;

    // Maximum number of allowed validation attempts
    private static final int MAX_ATTEMPTS = 3;

    // Expiration time in minutes for a verification code
    private static final int EXPIRATION_TIME= 3; // minutes
    // Temporary storage of codes mapped by user ID
    private final Map<Long, CodeData> codeStorage = new ConcurrentHashMap<>();

    /**
     * Generates a random numeric code using SecureRandom.
     * @return the generated numeric code as a String.
     */
    public  String generateNumericCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }



    /**
     * Generates a new code for the given user, stores it temporarily
     * with expiration and resets attempt count.
     * Also sets the user's status to NOT_VALIDATED.
     * @param user the user to generate the code for.
     * @return the generated code.
     */
    public String generateCode(User user){
        String code = generateNumericCode();
        CodeData codeData = new CodeData(code,LocalDateTime.now().plusMinutes(EXPIRATION_TIME));
        codeStorage.put(user.getId(), codeData);
        user.setStatus(Status.NOT_VALIDATED);
        user.setCodeValidate(code);
        userDao.update(user);

        return code;
    }

    /**
     * Validates a given code for the specified user.
     * If the code is correct and within the time limit and attempt limit,
     * the user is considered validated and their status is cleared.
     * @param user the user attempting to validate.
     * @param inputCode the code provided by the user.
     * @return true if the code is correct and valid; false otherwise.
     */
    public boolean validateCode(User user,String inputCode){
        logger.info("Validando código: {}, para user ID: {}", inputCode, user.getId());

        CodeData codeData = codeStorage.get(user.getId());
            if (codeData == null) {
                return false;
            }

            // Check expiration or too many attempts
            if (codeData.attempts >= MAX_ATTEMPTS || LocalDateTime.now().isAfter(codeData.expiration)) {
                codeStorage.remove(user.getId());
                return false;
            }

            codeData.attempts++;

            // Validate code
            if (codeData.code.equals(inputCode)) {
                codeStorage.remove(user.getId());
                return true;
            }

            return false;
        }
    /**
     * Returns the CodeData for a given user ID, if present.
     * @param userId the ID of the user.
     * @return the CodeData or null if not found.
     */
    public CodeData getCodeData(Long userId) {
        return codeStorage.get(userId);
    }





    /**
     * Internal class to store code data with expiration and attempt tracking.
     */
    public static class CodeData {
        private final String code;
        private final LocalDateTime expiration;
         private int attempts;


        CodeData(String code, LocalDateTime expiration) {
            this.code = code;
            this.expiration = expiration;
            this.attempts = 0;
        }


        public String getCode() {
            return code;
        }

        public LocalDateTime getExpiration() {
            return expiration;
        }

        public int getAttempts() {
            return attempts;
        }

        public void incrementAttempts() {
            this.attempts++;
        }


    }


}

