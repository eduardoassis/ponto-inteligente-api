package com.assis.pontointeligente.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.assis.pontointeligente.api.entities.Lancamento;
import com.assis.pontointeligente.api.repositories.LancamentoRepository;
import com.assis.pontointeligente.api.services.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService{

	public static final Logger log = LoggerFactory.getLogger(LancamentoServiceImpl.class);
	
	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Override
	public Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest pageRequest) {
		log.info("Buscando lancamentos para o funcionario  ID: {}", funcionarioId);
		return this.lancamentoRepository.findByFuncionarioId(funcionarioId, pageRequest);
	}

	@Cacheable("lancamentoPorId")
	@Override
	public Optional<Lancamento> buscarPorId(Long id) {
		log.info("Buscando lancamento por ID: {}", id);
		return Optional.ofNullable(this.lancamentoRepository.findOne(id));
	}

	@CachePut("lancamentoPorId")
	@Override
	public Lancamento persistir(Lancamento lancamento) {
		log.info("Persistindo o lançamento: {}", lancamento);
		return this.lancamentoRepository.save(lancamento);
	}

	@Override
	public void remover(Long id) {
		log.info("Removendo o lançamento ID: {}", id);
		this.lancamentoRepository.delete(id);		
	} 
	
}
