package com.assis.pontointeligente.api.controllers;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assis.pontointeligente.api.entities.Funcionario;
import com.assis.pontointeligente.api.response.Response;
import com.assis.pontointeligente.api.services.FuncionarioService;
import com.assis.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin("*")
public class FuncionarioController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FuncionarioController.class);
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	public FuncionarioController() {
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Response<FuncionarioDto>> atualizar(@PathVariable Long id, @Valid @RequestBody FuncionarioDto funcionarioDto, 
			BindingResult result) throws NoSuchAlgorithmException {
		
		LOGGER.info("Atualizando funcionário: {}", funcionarioDto.toString());
		
		Response<FuncionarioDto> response = new Response<>();
		
		Optional<Funcionario> funcionario = funcionarioService.buscarPorId(id);
		
		if (!funcionario.isPresent()) {
			LOGGER.info("Funcionário com id {} não encontrado.", id);
			response.getErrors().add("Funcionário com id " + id + " não encontrado");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		
		this.atualizarDadosFuncionario(funcionario.get(), funcionarioDto, result);
		
		if (result.hasErrors()) {
			LOGGER.info("Erro validando funcionário: {}", result.getAllErrors());
			response.getErrors().add("Funcionário com id " + id + " não encontrado");
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		this.funcionarioService.persistir(funcionario.get());
		response.setData(this.converterFuncionarioDto(funcionario.get()));
		
		return ResponseEntity.ok(response);
	}

	/**
	 * Converte um funcionario para FuncionarioDto
	 * 
	 * @param funcionario
	 * @return FuncionarioDto
	 * 
	 */
	private FuncionarioDto converterFuncionarioDto(Funcionario funcionario) {
		
		FuncionarioDto funcionarioDto = new FuncionarioDto();
		
		funcionarioDto.setId(funcionario.getId());
		funcionarioDto.setEmail(funcionario.getEmail());
		funcionarioDto.setNome(funcionario.getNome());
		
		funcionario.getQtdHorasAlmocoOpt()
			.ifPresent(qtdHorasAlmocoOpt -> funcionarioDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmocoOpt))));
		
		funcionario.getQtdHorasTrabalhoDiaOpt()
			.ifPresent(qtdHorasTrabalhoDia -> funcionarioDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabalhoDia))));
		
		funcionario.getValorHoraOpt()
			.ifPresent(valorHora -> funcionarioDto.setValorHora(Optional.of(valorHora.toString())));
		
		return funcionarioDto;
	}

	/**
	 * Atualiza os dados do funcionário com base nos dados encontrados no DTO.
	 * 
	 * @param funcionario
	 * @param funcionarioDto
	 * @param result
	 * @throws NoSuchAlgorithmException
	 */
	private void atualizarDadosFuncionario(Funcionario funcionario, FuncionarioDto funcionarioDto, BindingResult result) throws NoSuchAlgorithmException {
		
		if(!funcionario.getEmail().equals(funcionarioDto.getEmail())) {
			this.funcionarioService.buscarPorEmail(funcionarioDto.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("email", "E-mail já cadastrado.")));
		}

		if(result.hasErrors()) {
			return;
		}
		
		funcionario.setNome(funcionario.getNome());
		funcionario.setEmail(funcionarioDto.getEmail());
		
		funcionario.setQtdHorasAlmoco(null);
		funcionarioDto.getQtdHorasAlmoco()
			.ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));

		funcionario.setQtdHorasTrabalhoDia(null);
		funcionarioDto.getQtdHorasTrabalhoDia()
			.ifPresent(qtdHorasTrabalhoDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabalhoDia)));
		
		funcionario.setValorHora(null);
		funcionarioDto.getValorHora()
			.ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));
		
		if(funcionarioDto.getSenha().isPresent()) {
			funcionario.setSenha(PasswordUtils.gerarBCrypt(funcionarioDto.getSenha().get()));
		}
	}
}
