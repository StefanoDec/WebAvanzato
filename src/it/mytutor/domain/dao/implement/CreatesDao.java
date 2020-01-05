package it.mytutor.domain.dao.implement;

import it.mytutor.domain.*;
import it.mytutor.domain.dao.daofactory.DaoFactory;
import it.mytutor.domain.dao.exception.DatabaseException;
import it.mytutor.domain.dao.interfaces.CreatesDaoInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static it.mytutor.domain.dao.daofactory.DaoFactory.*;

public class CreatesDao implements CreatesDaoInterface {
    private static final String CREATE_CREATES_STATEMENT="insert into Creates(IdUser, IdUser2, IdChat) values(?,?,?)";
    private static final String UPDATE_CREATES_STATEMENT="";
    private static final String GET_CREATES_BY_ID_STATEMENT="select * from Creates c, Chat ch, User u, User u1 where c.IdCreates=? and c.IdChat=ch.IdChat and c.IdUser=u.IdUser and c.IdUser2=u1.IdUser";
    private static final String GET_ALL_CREATES_STATEMENT="select * from Creates c, Chat ch, User u, User u1 where c.IdChat=ch.IdChat and c.IdUser=u.IdUser and c.IdUser2=u1.IdUser";

    public void configureCreates(ResultSet rs, Creates creates,Chat c, List<User> users) throws DatabaseException {
        try{
            User u= new User();
            u.setIdUser(rs.getInt("u.IdUser"));
            u.setEmail(rs.getString("u.Email"));
            u.setRoles(rs.getInt("u.Roles"));
            u.setPassword(rs.getString("u.Password"));
            u.setName(rs.getString("u.Name"));
            u.setSurname(rs.getString("u.Surname"));
            u.setBirthday(rs.getDate("u.Birthday"));
            u.setImage(rs.getString("u.Image"));
            u.setLanguage(rs.getBoolean("u.Language"));
            u.setCreateDate(rs.getTimestamp("u.CreateDate"));
            u.setUpdateDate(rs.getTimestamp("u.UpdateDate"));
            users.add(u);

            u.setIdUser(rs.getInt("u1.IdUser"));
            u.setEmail(rs.getString("u1.Email"));
            u.setRoles(rs.getInt("u1.Roles"));
            u.setPassword(rs.getString("u1.Password"));
            u.setName(rs.getString("u1.Name"));
            u.setSurname(rs.getString("u1.Surname"));
            u.setBirthday(rs.getDate("u1.Birthday"));
            u.setImage(rs.getString("u1.Image"));
            u.setLanguage(rs.getBoolean("u1.Language"));
            u.setCreateDate(rs.getTimestamp("u1.CreateDate"));
            u.setUpdateDate(rs.getTimestamp("u1.UpdateDate"));
            users.add(u);

            c.setIdChat(rs.getInt("ch.IdChat"));
            c.setName(rs.getString("ch.Name"));
            c.setCreateDate(rs.getTimestamp("ch.CreateDate"));
            c.setUpdateDate(rs.getTimestamp("ch.UpdateDate"));

            creates.setIdCreates(rs.getInt("c.IdChat"));
            creates.setCreateDate(rs.getTimestamp("c.CreateDate"));
            creates.setUpdateDate(rs.getTimestamp("c.UpdateDate"));
            creates.setUserListser(users);
            creates.setChat(c);

        } catch (SQLException s) {
            s.printStackTrace();
            throw new DatabaseException("Errore nel creare oggetto Chat");
        }
    }
    public void configureCreatesList(List<Creates> createsList, ResultSet rs) throws DatabaseException {
        try {
            while (rs.next()) {
                Creates c= new Creates();
                Chat ch= new Chat();
                List<User> users= new ArrayList<>();
                configureCreates(rs, c, ch, users);
                createsList.add(c);}
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Errore nel creare Lista oggetti Booking");
        }
    }
    @Override
    public void createCreates(Creates creates) throws DatabaseException {
        Connection connection = getConnection();
        if(connection == null){
            throw new DatabaseException("Connection is null");
        }
        ResultSet rs = null;
        PreparedStatement prs = null;
        try {
            prs = connection.prepareStatement(CREATE_CREATES_STATEMENT);
            if (prs == null){
                throw new DatabaseException("Statement is null");
            }
            prs.setInt(1, creates.getUserListser().get(1).getIdUser());
            prs.setInt(2, creates.getUserListser().get(2).getIdUser());
            prs.setInt(3, creates.getChat().getIdChat());

            prs.executeUpdate();
        } catch(SQLException e) {
            throw new DatabaseException(e.getMessage());
        } finally {
            DaoFactory.closeDbConnection(connection, rs, prs);
        }
    }

    @Override
    public void updateCreates(Creates creates) throws DatabaseException {

    }

    @Override
    public Creates getCreates(int id) throws DatabaseException {
        Creates creates=new Creates();
        List<User> users = new ArrayList<User>();
        Chat c= new Chat();
        Connection connection = getConnection();
        if(connection == null){
            throw new DatabaseException("Connection is null");
        }
        ResultSet rs = null;
        PreparedStatement prs = null;
        try {
            prs = connection.prepareStatement(GET_CREATES_BY_ID_STATEMENT);
            if (prs == null){
                throw new DatabaseException("Statement is null");
            }
            prs.setInt(1, id);
            rs=prs.executeQuery();
            configureCreates(rs,creates,c,users);

        } catch(SQLException e) {
            throw new DatabaseException(e.getMessage());
        } finally {
            DaoFactory.closeDbConnection(connection, rs, prs);
        }
        return creates;
    }

    @Override
    public List<Creates> getAllCreates() throws DatabaseException {
        List<Creates> createsList = new ArrayList<>();
        Connection conn = DaoFactory.getConnection();
        if (conn == null) {
            throw new DatabaseException("Connection is null");
        }
        ResultSet rs = null;
        PreparedStatement prs = null;
        try {
            prs = conn.prepareStatement(GET_ALL_CREATES_STATEMENT);
            if (prs == null) {
                throw new DatabaseException("Statement is null");
            }
            rs = prs.executeQuery();
            configureCreatesList(createsList, rs);


        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(e.getMessage());
        } finally {
            DaoFactory.closeDbConnection(conn, rs, prs);
        }
        return createsList;
    }
}
