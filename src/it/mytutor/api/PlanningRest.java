package it.mytutor.api;

import it.mytutor.api.test.ApiWebApplicationException;
import it.mytutor.business.exceptions.*;
import it.mytutor.business.impl.PlanningBusiness;
import it.mytutor.business.impl.UserBusiness;
import it.mytutor.business.services.PlanningInterface;
import it.mytutor.business.services.UserInterface;
import it.mytutor.domain.Planning;
import it.mytutor.domain.Student;
import it.mytutor.domain.Teacher;
import it.mytutor.domain.User;
import it.mytutor.domain.dao.exception.DatabaseException;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;


@Path("lessons/plannings")
public class PlanningRest {

    @Context
    private SecurityContext securityContext;

    private PlanningInterface planningService = new PlanningBusiness();
    private UserInterface userService = new UserBusiness();

    /**
     * Rest Creazione del planning concesa solamente al professore
     *
     * @param plannings Lista di Pianificazioni della prenotazione della lezione
     * @return Response Status ACCEPTED
     */
    @Path("create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"TEACHER"})
    public Response creaPlanning(List<Planning> plannings) {

        String teacherEmail = securityContext.getUserPrincipal().getName();
        Teacher teacher;
        try {
            teacher = (Teacher) userService.findUserByUsername(teacherEmail);
        } catch (UserException | DatabaseException e) {
            e.printStackTrace();
            throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
        }
        try {
            planningService.creaPlanning(plannings, teacher);
        } catch (PlanningBusinessException | SubjectBusinessException | LessonBusinessException e) {
            e.printStackTrace();
            throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
        }
        return Response.ok(Response.Status.ACCEPTED).entity("Lezione pianificata").build();
    }

    /**
     * modifica dei planning
     *
     * @param plannings lista di planning
     * @return Response Status ACCEPTED
     */
    @Path("modify")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"TEACHER"})
    public Response updateLesson(List<Planning> plannings) {
        try {
            planningService.updatePlanning(plannings);
        } catch (PlanningBusinessException e) {
            e.printStackTrace();
        }
        return Response.ok(Response.Status.ACCEPTED).entity("Pianificazione modificata").build();
    }

    /**
     * aggiunta dei planning
     *
     * @param plannings lista di planning
     * @return Response Status ACCEPTED
     */
    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"TEACHER"})
    public Response addPlannings(List<Planning> plannings) {
        try {
            planningService.addPlannings(plannings);
        } catch (PlanningBusinessException e) {
            e.printStackTrace();
            throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
        }
        return Response.ok(Response.Status.ACCEPTED).entity("Pianificazione pianificata").build();
    }

    @Path("delete")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deletePlannings(List<Planning> plannings) {
        try {
            planningService.deletePlannings(plannings);
        } catch (PlanningBusinessException e) {
            e.printStackTrace();
            throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
        }
        return Response.ok(Response.Status.ACCEPTED).entity("Pianificazione eliminata").build();
    }

    /*@Path("{PID}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlanning(@PathParam("PID") Integer pid){
        List<Planning> plannings = new ArrayList<>();
        plannings = planningService.findPlanningsById(pid);
        return Response.ok(plannings).build();
    }*/

    /**
     * @param macroMateria
     * @param nome
     * @param zona
     * @param microMateria
     * @param giornoSettimana
     * @param prezzo
     * @param oraInizio
     * @param oraFine
     * @return
     */
    @Path("research")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"STUDENT"})
    public Response getPrenotazioniRicerca(@QueryParam("macro-materia") String macroMateria,
                                           @QueryParam("nome") String nome,
                                           @QueryParam("zona") String zona,
                                           @QueryParam("micro-materia") String microMateria,
                                           @QueryParam("giorno-settimana") String giornoSettimana,
                                           @QueryParam("prezzo") String prezzo,
                                           @QueryParam("ora-inizio") String oraInizio,
                                           @QueryParam("ora-fine") String oraFine) {
        List<Planning> plannings;
        try {
            plannings = planningService.FindPlanningByFilter(macroMateria, nome, zona, microMateria, giornoSettimana, prezzo, oraInizio, oraFine);
        } catch (DatabaseException | PlanningBusinessException | BookingBusinessException e) {
            e.printStackTrace();
            throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
        }
        return Response.ok(plannings).build();
    }

    /**
     * @param idLesson id della lezione in oggetto
     * @param bookedUp 
     * @return
     */
    @Path("{LID}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"STUDENT, TEACHER"})
    public Response getPlanningsForALesson(@PathParam("LID") Integer idLesson, @QueryParam("booked-up") String bookedUp) {
        List<Planning> plannings;
        String userEmail = securityContext.getUserPrincipal().getName();
        User user;
        try {
            user = (User) userService.findUserByUsername(userEmail);
        } catch (UserException e) {
            e.printStackTrace();
            throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
        } catch (DatabaseException e) {
            e.printStackTrace();
            throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
        }
        if (user instanceof Student && bookedUp != null && !bookedUp.isEmpty()) {
            String studentEmail = securityContext.getUserPrincipal().getName();
            Student student;
            try {
                student = (Student) userService.findUserByUsername(studentEmail);
            } catch (UserException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());

            } catch (DatabaseException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
            try {
                plannings = planningService.findAllPlanningBookedUpByLessonId(idLesson, student.getIdStudent());
            } catch (PlanningBusinessException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
        } else {
            try {
                plannings = planningService.findAllPlanningByLessonId(idLesson);
            } catch (PlanningBusinessException e) {
                e.printStackTrace();
                throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
            }
        }
        return Response.ok(plannings).build();
    }

/*    @Path("{LID}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"STUDENT, TEACHER"})
    public Response getPlanningsBookedForALesson(@PathParam("LID") Integer idLesson, @) {
        List<Planning> plannings = new ArrayList<>();
        plannings = planningService.findAllPlanningByLessonId();
    }*/


    //TODO rest per liste dei campi in storico lezione per professore e per studente per Booking LessonState 2,3,4

}
