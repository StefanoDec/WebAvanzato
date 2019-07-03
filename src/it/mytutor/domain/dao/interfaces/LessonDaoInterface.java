package it.mytutor.domain.dao.interfaces;

import it.mytutor.domain.Lesson;
import it.mytutor.domain.dao.exception.DatabaseException;


import java.util.List;


public interface LessonDaoInterface {

    List<Lesson> getAllLesson()throws DatabaseException;

    List<Lesson> getLessonByName (String name) throws DatabaseException;

    List<Lesson> getLessonsBySubject(String microSubject) throws DatabaseException;

    Lesson getLessonsByID(int id) throws DatabaseException;

    void modifyLesson(Lesson lesson) throws DatabaseException;


    void createLesson(Lesson lesson)throws DatabaseException;


}