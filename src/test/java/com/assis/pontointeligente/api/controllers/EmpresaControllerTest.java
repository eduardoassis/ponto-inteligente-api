package com.assis.pontointeligente.api.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.assis.pontointeligente.api.entities.Empresa;
import com.assis.pontointeligente.api.services.EmpresaService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmpresaControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private EmpresaService empresaService;
	
	private static final String URL_BUSCAR_EMPRESA_CNPJ = "/api/empresas/";
	private static final String CNPJ = "51463645000100";
	private static final String RAZAO_SOCIAL = "Assis TI LTDA";
	private static final Long ID = 1L;
	
	@Test
	public void testeBuscarEmpresaPorCnpjInvalido() throws Exception {
		BDDMockito.given(this.empresaService.buscarPorCnpj(Mockito.anyString())).willReturn(Optional.empty());
		
		mockMvc.perform(MockMvcRequestBuilders
				.get(URL_BUSCAR_EMPRESA_CNPJ + CNPJ)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errors").value("Empresa não encontrada para o CNPJ " + CNPJ));
	}
	
	@Test
	public void testeBuscarEmpresaPorCnpjValido() throws Exception {
		
		BDDMockito.given(this.empresaService.buscarPorCnpj(Mockito.anyString()))
				.willReturn(this.obterDadosEmpresa());
	
		mockMvc.perform(MockMvcRequestBuilders.get(URL_BUSCAR_EMPRESA_CNPJ + CNPJ).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(ID))
				.andExpect(jsonPath("$.data.razaoSocial", equalTo(RAZAO_SOCIAL)))
				.andExpect(jsonPath("$.data.cnpj", equalTo(CNPJ)))
				.andExpect(jsonPath("$.errors").isEmpty());
		
	}

	private Optional<Empresa> obterDadosEmpresa() {
		Empresa empresa = new Empresa();
		empresa.setCnpj(CNPJ);
		empresa.setRazaoSocial(RAZAO_SOCIAL);
		empresa.setId(ID);
		return Optional.of(empresa);
	}
}
