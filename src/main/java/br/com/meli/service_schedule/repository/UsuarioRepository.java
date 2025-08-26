package br.com.meli.service_schedule.repository;

import br.com.meli.service_schedule.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    boolean existsUsuarioByEmail(String email);
    
    Optional<UsuarioModel> findByEmail(String email);

}
