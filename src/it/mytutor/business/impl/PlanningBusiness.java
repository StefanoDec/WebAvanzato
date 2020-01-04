package it.mytutor.business.impl;

import it.mytutor.business.exceptions.LessonBusinessException;
import it.mytutor.business.exceptions.PlanningBusinessException;
import it.mytutor.business.exceptions.SubjectBusinessException;
import it.mytutor.business.services.PlanningInterface;
import it.mytutor.domain.Lesson;
import it.mytutor.domain.Planning;
import it.mytutor.domain.Subject;
import it.mytutor.domain.Teacher;
import it.mytutor.domain.dao.exception.DatabaseException;
import it.mytutor.domain.dao.implement.LessonDao;
import it.mytutor.domain.dao.implement.PlanningDao;
import it.mytutor.domain.dao.implement.SubjectDao;
import it.mytutor.domain.dao.interfaces.LessonDaoInterface;
import it.mytutor.domain.dao.interfaces.PlanningDaoInterface;
import it.mytutor.domain.dao.interfaces.SubjectDaoInterface;

import java.util.List;

public class PlanningBusiness implements PlanningInterface {

    @Override
    public void creaPlanning(List<Planning> plannings, Teacher teacher) throws PlanningBusinessException, LessonBusinessException, SubjectBusinessException {
        PlanningDaoInterface planningDao = new PlanningDao();
        LessonDaoInterface lessonDao = new LessonDao();
        SubjectDaoInterface subjectDao = new SubjectDao();
        int i;

        try {
            System.out.println(plannings.get(0).getLesson().getSubject());
            subjectDao.createSubject(plannings.get(0).getLesson().getSubject());
        } catch (DatabaseException e) {
            e.printStackTrace();
            throw new SubjectBusinessException("Errore nel'aggiunta del subject nel database");
        }
        System.out.println(plannings.get(0).getLesson().getName());
        Lesson lesson = plannings.get(0).getLesson();
        lesson.setTeacher(teacher);

        Subject subject = plannings.get(0).getLesson().getSubject();
        List<Subject> subjects;

        try {
            subjects = subjectDao.getSubjectsByName(plannings.get(0).getLesson().getSubject().getMacroSubject());
        } catch (DatabaseException e) {
            e.printStackTrace();
            throw new SubjectBusinessException("Errore nel prendere gli oggetti subject dal database");
        }
        for (Subject subject1: subjects) {
            if (subject1.getMicroSubject().equals(plannings.get(0).getLesson().getSubject().getMicroSubject())) {
                subject.setIdSubject(subject1.getIdSubject());
                break;
            }
        }
        lesson.setSubject(subject);
        try {
            i = lessonDao.createLesson(lesson);
            lesson.setIdLesson(i);
        } catch (DatabaseException e) {
            e.printStackTrace();
            throw new LessonBusinessException("Errore nel'aggiunta della Lesson nel database");
        }

        for (Planning planning: plannings) {
            planning.setLesson(lesson);
            try {
                planningDao.createPlanning(planning);
            } catch (DatabaseException e) {
                e.printStackTrace();
                throw new PlanningBusinessException("Errore nel'aggiunta del planning nel database");
            }
        }
    }

    @Override
    public void deletePlannings(List<Planning> plannings) throws PlanningBusinessException {
        PlanningDaoInterface planningDao = new PlanningDao();
        for (Planning planning: plannings){
            try {
                planningDao.deletePlanning(planning);
            } catch (DatabaseException e) {
                e.printStackTrace();
                throw new PlanningBusinessException("Errore nell'aggiunta dei plannings");
            }
        }
    }

    @Override
    public void addPlannings(List<Planning> plannings) throws PlanningBusinessException {
        PlanningDaoInterface planningDao = new PlanningDao();
        for (Planning planning: plannings){
            try {
                planningDao.addPlanning(planning);
            } catch (DatabaseException e) {
                e.printStackTrace();
                throw new PlanningBusinessException("Errore nell'aggiunta dei plannings");
            }
        }
    }

    @Override
    public void updatePlanning(List<Planning> plannings) throws PlanningBusinessException {
        PlanningDaoInterface planningDao = new PlanningDao();
        for (Planning planning: plannings){
            try {
                planningDao.updatePlanning(planning);
            } catch (DatabaseException e) {
                e.printStackTrace();
                throw new PlanningBusinessException("Errore nella modifica del planning");
            }
        }
    }

    @Override
    public List<Planning> FindPlanningByFilter(String macroMateria, String nome, String zona, String microMateria,
                                               String giornoSettimana, String prezzo, String oraInizio, String oraFine) throws PlanningBusinessException {
        PlanningDaoInterface planningDaoInterface = new PlanningDao();

        List<Planning> plannings;
        int macroMateriaRelevant = 0;
        if (macroMateria != null && !macroMateria.isEmpty()) {
            macroMateriaRelevant = 1;
        }
        int nomeRelevant = 0;
        if (nome != null && !nome.isEmpty()) {
            nomeRelevant = 1;
        }
        int zonaRelevant = 0;
        if (zona != null && !zona.isEmpty()) {
            zonaRelevant = 1;
        }
        int microMateriaRelevant = 0;
        if (microMateria != null && !microMateria.isEmpty()) {
            microMateriaRelevant = 1;
        }
        int giornoSettimanaRelevant = 0;
        if (giornoSettimana != null && !giornoSettimana.isEmpty()) {
            giornoSettimanaRelevant = 1;
        }
        int prezzoRelevant = 0;
        if (prezzo != null && !prezzo.isEmpty()) {
            prezzoRelevant = 1;
        }
        int oraInizioRelevant = 0;
        if (oraInizio != null && !oraInizio.isEmpty()) {
            oraInizioRelevant = 1;
        }
        int oraFineaRelevant = 0;
        if (oraFine != null && !oraFine.isEmpty()) {
            oraFineaRelevant = 1;
        }

        try {
            plannings = planningDaoInterface.getPlanningByFilter(macroMateriaRelevant, macroMateria, nomeRelevant, nome,
                    zonaRelevant, zona, microMateriaRelevant, microMateria, giornoSettimanaRelevant, giornoSettimana,
                    prezzoRelevant, prezzo, oraInizioRelevant, oraInizio, oraFineaRelevant, oraFine);
        } catch (DatabaseException e) {
            e.printStackTrace();
            throw new PlanningBusinessException("Errore nel prendere la lista dei planning");
        }
        return plannings;
    }
}
