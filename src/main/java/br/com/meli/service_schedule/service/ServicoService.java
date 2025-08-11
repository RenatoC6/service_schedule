package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.ServicoRequestDto;
import br.com.meli.service_schedule.model.ServicoModel;
import br.com.meli.service_schedule.repository.PrestadorRepository;
import br.com.meli.service_schedule.repository.ServicoRepository;
import br.com.meli.service_schedule.validator.prestador.PrestadorValidator;
import br.com.meli.service_schedule.validator.servico.ServicoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {

  @Autowired
  PrestadorRepository prestadorRepository;

  @Autowired
  ServicoRepository servicoRepository;

  @Autowired
  private List<PrestadorValidator> prestadorValidator;

  @Autowired
  private List<ServicoValidator> servicoValidator;

  public ServicoModel cadastrarServico(ServicoRequestDto dto) {

    this.servicoValidator.forEach(validatorsServ -> validatorsServ.validarServico(dto));

    this.prestadorValidator.forEach(  validatorsPrest -> validatorsPrest.validarPrestador(dto.prestadorId()));

    ServicoModel servico = new ServicoModel();

    servico.setNome(dto.nome());
    servico.setDescricao(dto.descricao());
    servico.setPreco(dto.preco());
    servico.setPrestador(prestadorRepository.findPrestadorById(dto.prestadorId()));

   return servicoRepository.save(servico);
  }
}
