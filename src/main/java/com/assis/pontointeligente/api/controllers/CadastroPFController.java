package com.assis.pontointeligente.api.controllers;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assis.pontointeligente.api.dtos.CadastroPFDto;
import com.assis.pontointeligente.api.entities.Empresa;
import com.assis.pontointeligente.api.entities.Funcionario;
import com.assis.pontointeligente.api.enums.PerfilEnum;
import com.assis.pontointeligente.api.response.Response;
import com.assis.pontointeligente.api.services.EmpresaService;
import com.assis.pontointeligente.api.services.FuncionarioService;
import com.assis.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/cadastrar-pf")
@CrossOrigin("*")
public class CadastroPFController {

	private static final Logger log = LoggerFactory.getLogger(CadastroPFController.class);
	
	@Autowired
	private EmpresaService empresaService;
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	public CadastroPFController() {
	}

	/**
	 * Cadastra um funcionario pessoa fisica no sistema.
	 * 
	 * @param cadastroPFDto
	 * @param result
	 * @return ResponseEntity<Response<CadastroPFDto>>
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<CadastroPFDto>> cadastrar(@Valid @RequestBody CadastroPFDto cadastroPFDto, BindingResult result) throws NoSuchAlgorithmException{
	
		log.info("Cadastro PF: {}", cadastroPFDto.toString());
		Response<CadastroPFDto> response = new Response<CadastroPFDto>();
		
		validarDadosExistentes(cadastroPFDto, result);
		Funcionario funcionario = converterDtoParaFuncionario(cadastroPFDto, result);
		
		if(result.hasErrors()) {
			log.error("Erro validando dados de cadastro PF: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());
		empresa.ifPresent(e -> funcionario.setEmpresa(e));
		this.funcionarioService.persistir(funcionario);
		
		response.setData(converterCadastroPFDto(funcionario));
		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * Popula o DTO de cadastro  com os dados do funcionario e empresa.
	 * 
	 * @param funcionario
	 * @return CadastroPFDto
	 */
	private CadastroPFDto converterCadastroPFDto(Funcionario funcionario) {
		
		CadastroPFDto cadastroPFDto = new CadastroPFDto();
		
		cadastroPFDto.setId(funcionario.getId());
		cadastroPFDto.setCnpj(funcionario.getEmpresa().getCnpj());
		cadastroPFDto.setCpf(funcionario.getCpf());
		cadastroPFDto.setEmail(funcionario.getEmail());
		cadastroPFDto.setNome(funcionario.getNome());
		
		funcionario.getQtdHorasTrabalhoDiaOpt()
			.ifPresent(qtdHorasTrabalhoDiaOpt -> cadastroPFDto.setQtdHorasTrabalhoDia(Optional.ofNullable(Float.toString(qtdHorasTrabalhoDiaOpt))));
		
		funcionario.getQtdHorasAlmocoOpt()
			.ifPresent(qtdHorasAlmocoOpt -> cadastroPFDto.setQtdHorasAlmoco(Optional.ofNullable(qtdHorasAlmocoOpt.toString())));
		
		funcionario.getValorHoraOpt().ifPresent(valorHoraOpt -> cadastroPFDto.setValorHora(Optional.ofNullable(valorHoraOpt.toString())));
		
		return cadastroPFDto;
	}

	/**
	 * 
	 * Converte os dados do DTO para funcionario.
	 * 
	 * @param cadastroPFDto
	 * @param result
	 * @return Funcionario
	 * @throws NoSuchAlgorithmException
	 */
	private Funcionario converterDtoParaFuncionario(CadastroPFDto cadastroPFDto, BindingResult result) throws NoSuchAlgorithmException {
		
		Funcionario funcionario = new Funcionario();

		funcionario.setNome(cadastroPFDto.getNome());
		funcionario.setCpf(cadastroPFDto.getCpf());
		funcionario.setEmail(cadastroPFDto.getEmail());
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPFDto.getSenha()));
		
		cadastroPFDto.getQtdHorasTrabalhoDia()
					.ifPresent(qtdHorasTrabalhoDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabalhoDia)));
		
		cadastroPFDto.getQtdHorasAlmoco()
					.ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
		
		cadastroPFDto.getValorHora()
					.ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));
		
		return funcionario;
	}

	/**
	 * Verifica se a empresa e esta cadastrada e se o funcrionario nao existe na base de dados.
	 * 
	 * @param cadastroPFDto
	 * @param result
	 */
	private void validarDadosExistentes(CadastroPFDto cadastroPFDto, BindingResult result) {
		
		Optional<Empresa> empresa = empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());
		
		if(!empresa.isPresent()) {
			result.addError(new ObjectError("empresa", "Empresa não cadastrada."));			
		}
		
		this.funcionarioService.buscarPorCpf(cadastroPFDto.getCpf())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente.")));
		
		this.funcionarioService.buscarPorEmail(cadastroPFDto.getEmail())
		.ifPresent(func -> result.addError(new ObjectError("funcionario", "E-mail já existente.")));
	}
}
