package app.service;

import app.model.Criterion;

import java.util.List;

public interface CriterionService {
    List<Criterion> findAllCriterions();

    Criterion findCriterionById(Long id);

    Criterion findCriterionByName(String name);

    void save(Criterion criterion);

    void delete(Criterion criterion);

}
