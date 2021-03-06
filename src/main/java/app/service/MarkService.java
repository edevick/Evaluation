package app.service;

import app.model.*;

import java.util.List;
import java.util.Map;

public interface MarkService {
    void save(Mark mark);

    List<Mark> findMarkByJuryAndPerformance(Performance performance, User user);

    List<Mark> findMarksByPerformance(Performance performance);

    List<Mark> findAllMarkByCriterion(Criterion criterion);

    int getSummaryMarkByAllPerformancesByConcreteJury(Member member, User jury);

    Map<Member, Integer> getSummaryMarkByAllPerformances();

    Map<Member, Integer> getPlaces(Map<Member, Integer> summaryMark);

    void deleteAllMarks();

    void deleteMark(Mark mark);
}

