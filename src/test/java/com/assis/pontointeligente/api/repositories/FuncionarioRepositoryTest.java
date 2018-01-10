package com.assis.pontointeligente.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.assis.pontointeligente.api.entities.Empresa;
import com.assis.pontointeligente.api.entities.Funcionario;
import com.assis.pontointeligente.api.enums.PerfilEnum;
import com.assis.pontointeligente.api.utils.PasswordUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FuncionarioRepositoryTest {

	@Autowired
	private FuncionarioRepository funcionarioRepository;
	
	@Autowired
	private EmpresaRepository empresaRepository;
	
	private static final String EMAIL = "email@email.com";
	private static final String CPF = "24291173474";
	
	@Before
	public void setUp(){
		Empresa empresa = this.empresaRepository.save(obterDadosEmpresa());
		this.funcionarioRepository.save(obterDadosFuncionario(empresa));
	}
	
	@After
	public final void tearDown() {
		this.empresaRepository.deleteAll();
	}

	@Test
	public void testBuscarFuncionarioPorEmail() {
		Funcionario funcionario = this.funcionarioRepository.findByEmail(EMAIL);
		assertNotNull(funcionario);
		assertEquals(EMAIL, funcionario.getEmail());
	}
	
	@Test
	public void testBuscarFuncionarioPorCpf(){
		Funcionario funcionario = this.funcionarioRepository.findByCpf(CPF);
		assertNotNull(funcionario);
		assertEquals(CPF, funcionario.getCpf());
	}

	@Test
	public void testBuscarFuncionarioPorEmailOuCpf(){
		Funcionario funcionario = this.funcionarioRepository.findByCpfOrEmail(CPF, EMAIL);
		assertNotNull(funcionario);
		assertEquals(EMAIL, funcionario.getEmail());
		assertEquals(CPF, funcionario.getCpf());
	}
	
	@Test
	public void testBuscarFuncionarioPorEmailOuCpfParaEmailInvalido(){
		String emailInvalido = "email@invalido.com";
		Funcionario funcionario = this.funcionarioRepository.findByCpfOrEmail(CPF, emailInvalido);
		assertNotNull(funcionario);
		assertEquals(CPF, funcionario.getCpf());
		assertNotEquals(emailInvalido, funcionario.getEmail());
	}

	@Test
	public void testBuscarFuncionarioPorEmailOuCpfParaCpfInvalido(){
		String cpfInvalido = "12345678901";
		Funcionario funcionario = this.funcionarioRepository.findByCpfOrEmail(cpfInvalido, EMAIL);
		assertNotNull(funcionario);
		assertNotEquals(cpfInvalido, funcionario.getCpf());
		assertEquals(EMAIL, funcionario.getEmail());
	}
	
	private Funcionario obterDadosFuncionario(Empresa empresa) {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome("Fulano de Tal");
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarBCrypt("123456"));
		funcionario.setCpf(CPF);
		funcionario.setEmail(EMAIL);
		funcionario.setEmpresa(empresa);
		return funcionario;
	}

	private Empresa obterDadosEmpresa() {
		Empresa empresa = new Empresa();
		empresa.setRazaoSocial("Empresa de exemplo");
		empresa.setCnpj("51463645000100");
		return empresa;

	}
}
