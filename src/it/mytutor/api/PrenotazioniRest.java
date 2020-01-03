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
import it.mytutor.domain.dao.exception.DatabaseException;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("lezioni/prenotazioni")
public class PrenotazioniRest {

    private BookingInterface bookingService = new BookingBusiness();
    private UserInterface userService = new UserBusiness();

    /**
     * Creazione della prenotazione da parte dello studente
     *
     * @param booking oggetto prenotazione ricevuto dal client
     * @param context ContainerRequestContext da cui prendere l'email dell'utente
     * @return Response Status ACCEPTED
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"STUDENT"})
    public Response creaPrenotazione(Booking booking, ContainerRequestContext context){

        SecurityContext securityContext = context.getSecurityContext();
        String teacherEmail = securityContext.getUserPrincipal().getName();
        Student student;
        try {
            student = (Student) userService.findUserByUsername(teacherEmail);
        } catch (UserException | DatabaseException e) {
            e.printStackTrace();
            throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
        }
        booking.setStudent(student);
        try {
            bookingService.crateBooking(booking);
        } catch (PlanningBusinessException e) {
            e.printStackTrace();
            throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
        }
        return Response.ok(Response.Status.ACCEPTED).entity("prenotazione creata").build();
    }

    /**
     * Rest per l'aggiornamento dello stato della lezione
     * concesso ad entrambe le tipologie di utenti
     *
     *
     * @param pid id booking
     * @param lessonState stato della lezione
     * @return Response Status ACCEPTED
     */
    @Path("{PID}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"TEACHER", "STUDENT"})
    public Response rispostaTheacher(@PathParam("PID") Integer pid, @QueryParam("lesson-state") Integer lessonState){
        try {
            bookingService.updateBooking(bookingService.findBookingById(pid), lessonState);
        } catch (BookingBusinessException e) {
            e.printStackTrace();
            throw new ApiWebApplicationException("Errore interno al server: " + e.getMessage());
        }
        return Response.ok(Response.Status.ACCEPTED).entity("prenotazione aggiornata").build();
    }
}
