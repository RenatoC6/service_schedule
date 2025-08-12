package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.ServicoRequestDto;
import br.com.meli.service_schedule.exception.EntidadeNaoEncontradaException;
import br.com.meli.service_schedule.exception.GenericException;
import br.com.meli.service_schedule.model.ServicoModel;
import br.com.meli.service_schedule.repository.ServicoRepository;
import br.com.meli.service_schedule.validator.servico.ServicoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {


    @Autowired
    ServicoRepository servicoRepository;

    @Autowired
    private List<ServicoValidator> servicoValidator;

    public ServicoModel cadastrarServico(ServicoRequestDto dto) {

        if (servicoRepository.existsByNome(dto.nome().toUpperCase())) {
            throw new GenericException("Já existe um serviço cadastrado com o nome: " + dto.nome());
        }

        this.servicoValidator.forEach(validatorsServ -> validatorsServ.validarServico(dto));

        ServicoModel servico = new ServicoModel();

        servico.setNome(dto.nome());
        servico.setDescricao(dto.descricao());
        servico.setPreco(dto.preco());

        return servicoRepository.save(servico);
    }

    public ServicoModel atualizarServico(Long idServicoAtual, ServicoRequestDto dto) {

        ServicoModel servicoAtual = servicoRepository.findById(idServicoAtual)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Servico não encontrado: " + idServicoAtual));

        this.servicoValidator.forEach(validatorsServ -> validatorsServ.validarServico(dto));

        if (!servicoAtual.getNome().equals(dto.nome())) {
            if (servicoRepository.existsByNome(dto.nome().toUpperCase())) {
                throw new GenericException("Já existe um serviço cadastrado com o nome: " + dto.nome());
            }
        }

        servicoAtual.setNome(dto.nome());
        servicoAtual.setDescricao(dto.descricao());
        servicoAtual.setPreco(dto.preco());

        return servicoRepository.save(servicoAtual);
    }

    public void deletarServico(Long idServicoAtual) {

        if(servicoRepository.existsById(idServicoAtual)) {
            servicoRepository.deleteById(idServicoAtual);
            throw new GenericException("Servico deletado com sucesso: " + idServicoAtual);
        } else {
            throw new EntidadeNaoEncontradaException("Servico não encontrado: " + idServicoAtual);
        }

    }

    public List<ServicoModel> listarServicos() {

        return servicoRepository.findAll();
    }
}
