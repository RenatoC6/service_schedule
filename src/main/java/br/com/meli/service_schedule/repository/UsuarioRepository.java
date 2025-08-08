package br.com.meli.service_schedule.repository;

import br.com.meli.service_schedule.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    UsuarioModel findUsuarioModelById(Long id);

    boolean existsByEmail(String email);


}
