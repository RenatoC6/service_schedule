package br.com.meli.service_schedule.repository;

import br.com.meli.service_schedule.model.ScheduleModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<ScheduleModel, Long> {

}
