package br.com.meli.service_schedule.repository;

import br.com.meli.service_schedule.model.AgendaPrestadorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface AgendaPrestadorRepository extends JpaRepository<AgendaPrestadorModel, Long> {


    // Busca horários conflitantes para o mesmo prestador num intervalo
    List<AgendaPrestadorModel> findByPrestadorModelIdAndDataHoraDisponivelBetween(
            Long prestadorId, LocalDateTime dataInicio, LocalDateTime dataFim);


    @Query(value = "SELECT * FROM agenda_prestador WHERE status = :status", nativeQuery = true)
    List<AgendaPrestadorModel> findByStatusDisponivel(@Param("status") String status);

   AgendaPrestadorModel findAgendaPrestadorModelsById(Long idAgenda);
}
