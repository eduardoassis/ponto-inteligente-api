package com.assis.pontointeligente.api.controllers;

import java.security.NoSuchAlgorithmException;

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

import com.assis.pontointeligente.api.dtos.CadastroPJDto;
import com.assis.pontointeligente.api.entities.Empresa;
import com.assis.pontointeligente.api.entities.Funcionario;
import com.assis.pontointeligente.api.response.Response;
import com.assis.pontointeligente.api.services.EmpresaService;
import com.assis.pontointeligente.api.services.FuncionarioService;

@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")
public class CadastroPJController {

	private static final Logger log = LoggerFactory.getLogger(CadastroPJController.class);
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Autowired
	private EmpresaService empresaService;
	
	public CadastroPJController() {
	}
	
	@PostMapping
	public ResponseEntity<Response<CadastroPJDto>> cadastrar(@Valid @RequestBody CadastroPJDto cadastroPJDto, BindingResult result) throws NoSuchAlgorithmException {
		
		log.info("Cadastro PJ: {}", cadastroPJDto.toString());
		
		Response<CadastroPJDto> response = new Response<CadastroPJDto>();
		
		validarDadosExistentes(cadastroPJDto, result);
		
		Empresa empresa = this.converterDtoParaEmpresa(cadastroPJDto, result);
		Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPJDto, result);
		
		if(result.hasErrors()) {
			log.error("Erro validando dados de cadastro: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		this.empresaService.persistir(empresa);
		funcionario.setEmpresa(empresa);
		this.funcionarioService.persistir(funcionario);
		
		response.setData(this.converterCadastroPJDto(funcionario));
		return ResponseEntity.ok(response);
	}

	/**
	 * Verifica se a empresa ou funcionário já existem na base de dados
	 * @param cadastroPJDto
	 * @param result
	 */
	private void validarDadosExistentes(CadastroPJDto cadastroPJDto, BindingResult result) {
		
		this.empresaService.buscarPorCnpj(cadastroPJDto.getCnpj())
			.ifPresent(empresaService -> result.addError(new ObjectError("empresa", "Empresa já existente")));
		
		this.funcionarioService.buscarPorEmail(cadastroPJDto.getEmail())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente")));
		
		this.funcionarioService.buscarPorCpf(cadastroPJDto.getCpf())
		.ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente")));
		
	}
	
	private CadastroPJDto converterCadastroPJDto(Funcionario funcionario) {
		return null;
	}

	private Empresa converterDtoParaEmpresa(CadastroPJDto cadastroPJDto, BindingResult result) {
		// TODO Auto-generated method stub
		return null;
	}

	private Funcionario converterDtoParaFuncionario(CadastroPJDto cadastroPJDto, BindingResult result) {
		return null;
	}

}