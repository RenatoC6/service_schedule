package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.UsuarioRequestDto;
import br.com.meli.service_schedule.exception.GenericException;
import br.com.meli.service_schedule.model.ClienteModel;
import br.com.meli.service_schedule.model.PrestadorModel;
import br.com.meli.service_schedule.model.UsuarioModel;
import br.com.meli.service_schedule.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;


    public UsuarioModel cadastrarUsuario(UsuarioRequestDto dto) {

        UsuarioModel usuario;

        if ("prestador".equalsIgnoreCase(dto.user_type())) {
            if (dto.atividadePrest() == null) {
                throw new GenericException("Para prestador, atividadePrest é obrigatória.");
            }
            PrestadorModel prestador = new PrestadorModel();
            prestador.setPassword(dto.password());
            prestador.setNome(dto.nome());
            prestador.setEmail(dto.email());
            prestador.setEndereco(dto.endereco());
            prestador.setCep(dto.cep());
            prestador.setCidade(dto.cidade());
            prestador.setEstado(dto.estado());
            prestador.setCreatedAt(java.time.LocalDateTime.now());

            // Transforma String para Enum (lança IllegalArgumentException se for inválido)
            prestador.setStatus(PrestadorModel.AtividadePrest.valueOf(dto.atividadePrest()));
            usuario = prestador;

        } else if ("cliente".equalsIgnoreCase(dto.user_type())) {
            ClienteModel cliente = new ClienteModel();
            cliente.setPassword(dto.password());
            cliente.setNome(dto.nome());
            cliente.setEmail(dto.email());
            cliente.setEndereco(dto.endereco());
            cliente.setCep(dto.cep());
            cliente.setCidade(dto.cidade());
            cliente.setEstado(dto.estado());
            cliente.setCreatedAt(java.time.LocalDateTime.now());
            usuario = cliente;
        } else {
            throw new GenericException("Tipo de Usuario(userType),  deve ser 'cliente' ou 'prestador'");
        }

        return usuarioRepository.save(usuario);
    }
}
