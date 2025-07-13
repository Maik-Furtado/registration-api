package br.com.mkanton.cadastroapi.dataaccess;

import br.com.mkanton.cadastroapi.domain.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.*;

@ApplicationScoped
public class UserDao implements IUserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    @Inject
    private EntityManager em;

    @Override
        public void insert(User user) {
            try{
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        }catch(Exception e){
                if(em.getTransaction().isActive()){
                em.getTransaction().rollback();}

                logger.error("Error insert user", e);
            }
        }
    @Override
        public void update(User user) {
            try{
                em.getTransaction().begin();
                em.merge(user);
                em.getTransaction().commit();
            }catch(Exception e){
                if(em.getTransaction().isActive()){
                    em.getTransaction().rollback();
                }
                logger.error("Error update user", e);
            }
        }

    @Override
        public void delete(Long id) {
            try {
                em.getTransaction().begin();
                User user = em.find(User.class, id);
                if (user == null) {
                    em.getTransaction().rollback();
                    throw new IllegalArgumentException("User not found");
                }
                em.remove(user);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
               logger.error("Error delete user", e);
            }
        }
    @Override
            public List<User> findAll() {
                try{
                    return em.createQuery("select u from User u", User.class).getResultList();
                }catch(Exception e){
                    logger.error("Error find all users", e);
                    return null;
                }
            }
    @Override
    public User findById(Long id) {
        try{
            return em.find(User.class, id);
        }catch(Exception e){
            logger.error("Error find user", e);
            return null;
        }
    }
    @Override
    public List<User> findByEmail(String emailPart) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(:email)", User.class)
                    .setParameter("email", "%" + emailPart + "%")
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error while searching users by email", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<User> findByUsername(String usernamePart) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(:username)", User.class)
                    .setParameter("username", "%" + usernamePart + "%")
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error while searching users by username", e);
            return Collections.emptyList();
        }
    }
    //Method for facilitate login logic.
    @Override
    public User findByEmailOrUsername(String value) {
        return em.createQuery(
                        "SELECT u FROM User u WHERE (u.email = :value OR u.username = :value)",
                        User.class)
                .setParameter("value", value)
                .getSingleResult();
    }


    @Override
    public User findExactByEmail(String email) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.debug("No user found with email: {}", email);
            logger.error("Error while searching users by email", e);
            return null;
        }

    }


}
