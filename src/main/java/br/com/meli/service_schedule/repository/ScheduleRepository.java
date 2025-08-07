package br.com.meli.service_schedule.repository;

import br.com.meli.service_schedule.model.ScheduleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleModel, Long> {

    List<ScheduleModel> findByClienteId(Long clienteId);

    List<ScheduleModel> findByPrestadorId(Long prestadorId);

    List<ScheduleModel> findByStatus(ScheduleModel.StatusAgendamento status);

}
