package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.UsuarioRequestDto;
import br.com.meli.service_schedule.dto.ViaCepDto;
import br.com.meli.service_schedule.exception.GenericException;
import br.com.meli.service_schedule.model.Atividades;
import br.com.meli.service_schedule.model.ClienteModel;
import br.com.meli.service_schedule.model.PrestadorModel;
import br.com.meli.service_schedule.model.UsuarioModel;
import br.com.meli.service_schedule.repository.UsuarioRepository;
import br.com.meli.service_schedule.validator.usuario.UsuarioValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private List<UsuarioValidator> validators;

    public UsuarioModel cadastrarUsuario(UsuarioRequestDto dto) {

        this.validators.forEach(validators -> validators.validarUsuario(dto));

        UsuarioModel usuario;

        ViaCepDto viaCepDto = buscarCep(dto.cep());

        if ("prestador".equalsIgnoreCase(dto.user_type())) {
            PrestadorModel prestador = new PrestadorModel();
            prestador.setPassword(dto.password());
            prestador.setNome(dto.nome());
            prestador.setEmail(dto.email());
            prestador.setEndereco(viaCepDto.logradouro());
            prestador.setCep(viaCepDto.cep());
            prestador.setCidade(viaCepDto.localidade());
            prestador.setEstado(viaCepDto.uf());
            prestador.setCreatedAt(java.time.LocalDateTime.now());
            prestador.setAtividadePrest(Atividades.valueOf(dto.atividadePrest()));
            usuario = prestador;

        } else if ("cliente".equalsIgnoreCase(dto.user_type())) {
            ClienteModel cliente = new ClienteModel();
            cliente.setPassword(dto.password());
            cliente.setNome(dto.nome());
            cliente.setEmail(dto.email());
            cliente.setEndereco(viaCepDto.logradouro());
            cliente.setCep(viaCepDto.cep());
            cliente.setCidade(viaCepDto.localidade());
            cliente.setEstado(viaCepDto.uf());
            cliente.setCreatedAt(java.time.LocalDateTime.now());
            usuario = cliente;
        } else {
            throw new GenericException("Tipo de Usuario(userType),  deve ser 'cliente' ou 'prestador'");
        }

        return usuarioRepository.save(usuario);
    }

    public ViaCepDto buscarCep(String cep) {
        final RestTemplate restTemplate = new RestTemplate();

        if (cep == null || cep.isEmpty() || !cep.matches("\\d{5}-?\\d{3}")) { //  \\d{5}: exatamente 5 dígitos (números) -?: hífen opcional (pode ou não ter um hífen entre os números) \\d{3}: exatamente 3 dígitos
            throw new GenericException("CEP invalido " + cep);
        }
        String url = UriComponentsBuilder
                .fromHttpUrl("https://viacep.com.br/ws/{cep}/json/")
                .buildAndExpand(cep)
                .toUriString();
        ViaCepDto viaCepDto = restTemplate.getForObject(url, ViaCepDto.class);

        if (viaCepDto == null || viaCepDto.erro()) {
            throw new GenericException("CEP invalido " + cep);
        }

        return viaCepDto;
    }

}
