package com.assis.pontointeligente.api.dtos;

public class EmpresaDto {

	private Long id;
	private String razaSocial;
	private String cnpj;

	public EmpresaDto() {
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getRazaSocial() {
		return razaSocial;
	}
	
	public void setRazaSocial(String razaSocial) {
		this.razaSocial = razaSocial;
	}
	
	public String getCnpj() {
		return cnpj;
	}
	
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	
	@Override
	public String toString() {
		return "Empresa [=id" + id + ", razaoSocial=" + razaSocial + ", cnpj=" + cnpj + "]";
	}
}
