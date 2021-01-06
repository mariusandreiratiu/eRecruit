/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rec.erecruit.ejb;

import com.rec.erecruit.common.UserDetails;
import com.rec.erecruit.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author popa_
 */
@Stateless
public class UserBean {

    private static final Logger LOG = Logger.getLogger(UserBean.class.getName());

    @PersistenceContext
    private EntityManager em;
    
    public boolean checkDuplicatesDB(String Username){
        boolean flag= false;
        
        List<UserDetails> users= getAllUsers();
        
        for (UserDetails user : users){
            if (user.getUsername().equals(Username)){
                flag=true;
            }
        }
        return flag;
    }
    
    public void createUser(String FirstName,String LastName, String PhoneNo, String MobileNo, String Mail, String JobTitle, String Description, String PasswordSha256){
        User user= new User();
        
        int lungime=1;
        String UsernameGenerat;
        
        if(LastName.length()>5){
            UsernameGenerat= LastName.substring(0, 5) + FirstName.substring(0,lungime);
        } else{
            UsernameGenerat= LastName + FirstName.substring(0,lungime);
        }
        
        while(checkDuplicatesDB(UsernameGenerat)){
            lungime++;
            
            if(LastName.length()>5){
                UsernameGenerat= LastName.substring(0, 5) + FirstName.substring(0,lungime);
            } else{
                UsernameGenerat= LastName + FirstName.substring(0,lungime);
            }
        }
        
        
        user.setNume(LastName);
        user.setPrenume(FirstName);
        user.setNrTel(PhoneNo);
        user.setNrMobil(MobileNo);
        user.setMail(Mail);
        user.setFunctie(JobTitle);
        user.setDescriere(Description);
        user.setPassword(PasswordSha256);
        user.setUsername(UsernameGenerat);
        
        
        em.persist(user);
    }
    
    public List<UserDetails> getAllUsers(){
        LOG.info("getAllUsers");
        try{
            Query query=em.createQuery("SELECT u FROM User u");
            List<User> users=(List<User>) query.getResultList();
            return copyUsersToDetails(users);
        }
        catch(Exception ex){
            throw new EJBException(ex);
        }
    }
    
    private List<UserDetails> copyUsersToDetails(List<User> users){
        List<UserDetails> detailsList = new ArrayList<>();
        for(User user: users){
            UserDetails userDetials=new UserDetails(user.getId(),user.getNume(),user.getPrenume(),user.getNrTel(),user.getNrMobil(),user.getMail(),user.getFunctie(),user.getDescriere(),user.getUsername(),user.getPassword());
            detailsList.add(userDetials);
            
        }
        return detailsList;
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}