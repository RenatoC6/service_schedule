package br.com.meli.service_schedule.repository;

import br.com.meli.service_schedule.model.PrestadorModel;
import br.com.meli.service_schedule.model.ScheduleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleModel, Long> {

    List<ScheduleModel> findByPrestadorModel(PrestadorModel prestadorModel);

}
