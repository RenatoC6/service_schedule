package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.UsuarioRequestDto;
import br.com.meli.service_schedule.dto.UsuarioRequestDtoPrestador;
import br.com.meli.service_schedule.dto.UsuarioRequestDtoCliente;
import br.com.meli.service_schedule.dto.ViaCepDto;
import br.com.meli.service_schedule.exception.EntidadeNaoEncontradaException;
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

        this.validators.forEach(validators -> validators.validarUsuario(dto.user_type(), dto.atividadePrest()));

        validarEmail(dto.email());

        UsuarioModel usuario;

        ViaCepDto viaCepDto = buscarCep(dto.cep());

        if ("prestador".equalsIgnoreCase(dto.user_type())) {
            PrestadorModel prestador = new PrestadorModel();
            prestador.setPassword(dto.password());
            prestador.setNome(dto.nome());
            prestador.setEmail(dto.email());
            atualizaAtributosCep(prestador, viaCepDto);
            prestador.setCreatedAt(java.time.LocalDateTime.now());
            prestador.setAtividadePrest(Atividades.valueOf(dto.atividadePrest().toUpperCase()));
            usuario = prestador;

        } else if ("cliente".equalsIgnoreCase(dto.user_type())) {
            ClienteModel cliente = new ClienteModel();
            cliente.setPassword(dto.password());
            cliente.setNome(dto.nome());
            cliente.setEmail(dto.email());
            atualizaAtributosCep(cliente, viaCepDto);
            cliente.setCreatedAt(java.time.LocalDateTime.now());
            usuario = cliente;
        } else {
            throw new GenericException("Tipo de Usuario(userType),  deve ser 'cliente' ou 'prestador'");
        }

        return usuarioRepository.save(usuario);
    }

    public UsuarioModel atualizarUsuarioPrestador(Long id, UsuarioRequestDtoPrestador dto) {
        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario nao encontrado: " + id));

        if (usuario instanceof ClienteModel) {
            throw new GenericException("Usuario nao e prestador");
        }

        if (!usuario.getEmail().equals(dto.email())) {
            validarEmail(dto.email());
        }
        String userType = "prestador";
        this.validators.forEach(validators -> validators.validarUsuario(userType, dto.atividadePrest()));

        PrestadorModel prestador = (PrestadorModel) usuario;
        prestador.setAtividadePrest(Atividades.valueOf(dto.atividadePrest().toUpperCase()));
        prestador.setNome(dto.nome());
        prestador.setEmail(dto.email());
        prestador.setPassword(dto.password());
        if (dto.cep().equals(usuario.getCep())) {
            ViaCepDto viaCepDto = buscarCep(dto.cep());
            atualizaAtributosCep(prestador, viaCepDto);
        }

        return usuarioRepository.save(usuario);
}

    public UsuarioModel atualizarUsuarioCliente(Long id, UsuarioRequestDtoCliente dto) {
        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario nao encontrado: " + id));

        if (usuario instanceof PrestadorModel) {
            throw new GenericException("Usuario nao e cliente");
        }

        if (!usuario.getEmail().equals(dto.email())) {
            validarEmail(dto.email());
        }
        String userType = "cliente";
        String atividadePrest = "";
        this.validators.forEach(validators -> validators.validarUsuario(userType, atividadePrest));

        ClienteModel cliente = (ClienteModel) usuario;
        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setPassword(dto.password());
        if (dto.cep().equals(usuario.getCep())) {
            ViaCepDto viaCepDto = buscarCep(dto.cep());
            atualizaAtributosCep(cliente, viaCepDto);
        }

        return usuarioRepository.save(usuario);

    }

public List<UsuarioModel> listarUsuarios() {
    return usuarioRepository.findAll();
}

public void deleteUsuario(Long idUsuario) {

    if (usuarioRepository.existsById(idUsuario)) {
        usuarioRepository.deleteById(idUsuario);
        throw new GenericException("Usuario excluido com sucesso: " + idUsuario);
    } else
        throw new EntidadeNaoEncontradaException("Usuario nao encontrado: " + idUsuario);

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

public void atualizaAtributosCep(UsuarioModel usuario, ViaCepDto viaCepDto) {
    usuario.setEndereco(viaCepDto.logradouro());
    usuario.setCep(viaCepDto.cep());
    usuario.setCidade(viaCepDto.localidade());
    usuario.setEstado(viaCepDto.uf());
}

public void validarEmail(String email) {
    if (email == null || !email.contains("@")) {
        throw new GenericException("Email inválido: " + email);
    }

    boolean exists = usuarioRepository.existsUsuarioByEmail(email);
    if (exists) {
        throw new GenericException("Email já cadastrado: " + email);
    }
}

}
