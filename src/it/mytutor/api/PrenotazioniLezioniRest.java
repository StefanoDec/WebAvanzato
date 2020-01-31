package it.mytutor.api;


import it.mytutor.api.test.ApiWebApplicationException;
import it.mytutor.business.exceptions.BookingBusinessException;
import it.mytutor.business.exceptions.PlanningBusinessException;
import it.mytutor.business.exceptions.UserException;
import it.mytutor.business.impl.BookingBusiness;
import it.mytutor.business.impl.UserBusiness;
import it.mytutor.business.services.BookingInterface;
import it.mytutor.business.services.UserInterface;
import it.mytutor.domain.Booking;
import it.mytutor.domain.Student;
import it.mytutor.domain.Teacher;
import it.mytutor.domain.User;
import it.mytutor.domain.dao.exception.DatabaseException;
import it.mytutor.domain.dao.implement.StudentDao;
import it.mytutor.domain.dao.implement.TeacherDao;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Path("bookings-lessons")
public class PrenotazioniLezioniRest {
    @Context
    private SecurityContext securityContext;

    private BookingInterface bookingService = new BookingBusiness();
    private UserInterface userService = new UserBusiness();


    /**
     * Rest della HomePage di entrambe le tipologie di utente
     *
     * @return lista di Bookings
     */
    @Path("home")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"TEACHER", "STUDENT"})
    public Response getPrenotazioni() {
        List<Booking> bookings = new ArrayList<>();
        User user;
        String email = securityContext.getUserPrincipal().getName();
        try {
            user = (User) userService.findUserByUsername(email);
        } catch (UserException | DatabaseException e) {
            e.printStackTrace();
            throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
        }

        if (user.getRoles() == 1) {
            StudentDao studentDao = new StudentDao();
            Student student;
            try {
                student = studentDao.getStudentByIdUser(user.getIdUser());
            } catch (DatabaseException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
            try {
                bookings = bookingService.findAllBookingByStudnet(student);
            } catch (DatabaseException | PlanningBusinessException | BookingBusinessException | UserException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
        } else if (user.getRoles() == 2) {
            TeacherDao teacherDao = new TeacherDao();
            Teacher teacher;
            try {
                teacher = teacherDao.getTeacherByUserID(user.getIdUser());
            } catch (DatabaseException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
            try {
                bookings = bookingService.findAllBookingByTeacher(teacher);
            } catch (DatabaseException | PlanningBusinessException | BookingBusinessException | UserException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
        }
        return Response.ok(bookings).build();
    }

    /**
     * Rest della pagina dello storico con il filtro di entrambe le tipologie di utente
     *
     * @param macroMateria macro materia
     * @param nomeLezione  nome della lezione
     * @param data         data di planning
     * @param idUser       id dello studente prenotato alla lezione
     * @param stato        stato deolla prenotazione (0,1,2,3,4)
     * @return lista di Bookings
     */
    @Path("history")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"STUDENT", "TEACHER"})
    public Response getStoricoTeachFilter(@QueryParam("macro-materia") String macroMateria,
                                          @QueryParam("micro-materia") String microMateria,
                                          @QueryParam("nome-lezione") String nomeLezione,
                                          @QueryParam("data") String data,
                                          @QueryParam("id-utente") String idUser,
                                          @QueryParam("stato") String stato) {
        List<Booking> bookings = new ArrayList<>();
        User user;
        String email = securityContext.getUserPrincipal().getName();
        try {
            user = (User) userService.findUserByUsername(email);
        } catch (UserException | DatabaseException e) {
            e.printStackTrace();
            throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
        }
        if (user.getRoles() == 1) {
            StudentDao studentDao = new StudentDao();
            Student student;
            try {
                student = studentDao.getStudentByIdUser(user.getIdUser());
            } catch (DatabaseException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
            try {
                bookings = bookingService.findAllBookingByStudnetAndFilter(student, macroMateria, nomeLezione, microMateria, data, idUser, stato);
            } catch (UserException | ParseException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
        } else if (user.getRoles() == 2) {
            TeacherDao teacherDao = new TeacherDao();
            Teacher teacher;
            try {
                teacher = teacherDao.getTeacherByUserID(user.getIdUser());
            } catch (DatabaseException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
            try {
                bookings = bookingService.findAllBookingByTeacherAndFilter(teacher, macroMateria, nomeLezione, microMateria, data, idUser, stato);
            } catch (UserException | ParseException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
        }
        return Response.ok(bookings).build();
    }
    @Path("count")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"STUDENT", "TEACHER"})
    public Response countbooking() {
        List<Booking> bookings = new ArrayList<>();
        User user;
        String email = securityContext.getUserPrincipal().getName();
        try {
            user = (User) userService.findUserByUsername(email);
        } catch (UserException | DatabaseException e) {
            e.printStackTrace();
            throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
        }
        if (user.getRoles() == 1) {
            StudentDao studentDao = new StudentDao();
            Student student;
            try {
                student = studentDao.getStudentByIdUser(user.getIdUser());
            } catch (DatabaseException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
            try {
                bookings = bookingService.findAllbookedUpByStudent(student);
            } catch (BookingBusinessException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
        } else if (user.getRoles() == 2) {
            TeacherDao teacherDao = new TeacherDao();
            Teacher teacher;
            try {
                teacher = teacherDao.getTeacherByUserID(user.getIdUser());
            } catch (DatabaseException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
            try {
                bookings = bookingService.findAllbookedUpByTeacher(teacher);
            } catch (BookingBusinessException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
        }
        return Response.ok(bookings.size()).build();
    }
}

