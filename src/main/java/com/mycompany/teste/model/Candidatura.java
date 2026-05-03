package com.mycompany.teste.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Candidatura {

    private String nomeCompleto;
    private String dataNascimento;
    private String sexo;
    private String nacionalidade;
    private String biPassaporte;
    private String residenciaPais;
    private String provincia;
    private String email;
    private String contactoTelefonico;
    private String nivelEscolaridade;
    private String areaEstudo;
    private String outraAreaEstudo;
    private boolean outraAreaEstudoSelecionada;
    private String cursoTecnico;
    private String instituicao;
    private String paisFormacao;
    private String dataFimCurso;
    private String areasInteresse;
    private String outraAreaInteresse;
    private boolean outraAreaInteresseSelecionada;
    private String objectivosProfissionais;
    private String resumoProfissional;
    private boolean consentimentoAceite;
    private String assinaturaCandidato;
    private String dataAssinatura;

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public String getBiPassaporte() {
        return biPassaporte;
    }

    public void setBiPassaporte(String biPassaporte) {
        this.biPassaporte = biPassaporte;
    }

    public String getResidenciaPais() {
        return residenciaPais;
    }

    public void setResidenciaPais(String residenciaPais) {
        this.residenciaPais = residenciaPais;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactoTelefonico() {
        return contactoTelefonico;
    }

    public void setContactoTelefonico(String contactoTelefonico) {
        this.contactoTelefonico = contactoTelefonico;
    }

    public String getNivelEscolaridade() {
        return nivelEscolaridade;
    }

    public void setNivelEscolaridade(String nivelEscolaridade) {
        this.nivelEscolaridade = nivelEscolaridade;
    }

    public String getAreaEstudo() {
        return areaEstudo;
    }

    public void setAreaEstudo(String areaEstudo) {
        this.areaEstudo = areaEstudo;
    }

    public String getOutraAreaEstudo() {
        return outraAreaEstudo;
    }

    public void setOutraAreaEstudo(String outraAreaEstudo) {
        this.outraAreaEstudo = outraAreaEstudo;
    }

    public boolean isOutraAreaEstudoSelecionada() {
        return outraAreaEstudoSelecionada;
    }

    public void setOutraAreaEstudoSelecionada(boolean outraAreaEstudoSelecionada) {
        this.outraAreaEstudoSelecionada = outraAreaEstudoSelecionada;
    }

    public String getCursoTecnico() {
        return cursoTecnico;
    }

    public void setCursoTecnico(String cursoTecnico) {
        this.cursoTecnico = cursoTecnico;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public String getPaisFormacao() {
        return paisFormacao;
    }

    public void setPaisFormacao(String paisFormacao) {
        this.paisFormacao = paisFormacao;
    }

    public String getDataFimCurso() {
        return dataFimCurso;
    }

    public void setDataFimCurso(String dataFimCurso) {
        this.dataFimCurso = dataFimCurso;
    }

    public String getAreasInteresse() {
        return areasInteresse;
    }

    public void setAreasInteresse(String areasInteresse) {
        this.areasInteresse = areasInteresse;
    }

    public String getOutraAreaInteresse() {
        return outraAreaInteresse;
    }

    public void setOutraAreaInteresse(String outraAreaInteresse) {
        this.outraAreaInteresse = outraAreaInteresse;
    }

    public boolean isOutraAreaInteresseSelecionada() {
        return outraAreaInteresseSelecionada;
    }

    public void setOutraAreaInteresseSelecionada(boolean outraAreaInteresseSelecionada) {
        this.outraAreaInteresseSelecionada = outraAreaInteresseSelecionada;
    }

    public String getObjectivosProfissionais() {
        return objectivosProfissionais;
    }

    public void setObjectivosProfissionais(String objectivosProfissionais) {
        this.objectivosProfissionais = objectivosProfissionais;
    }

    public String getResumoProfissional() {
        return resumoProfissional;
    }

    public void setResumoProfissional(String resumoProfissional) {
        this.resumoProfissional = resumoProfissional;
    }

    public boolean isConsentimentoAceite() {
        return consentimentoAceite;
    }

    public void setConsentimentoAceite(boolean consentimentoAceite) {
        this.consentimentoAceite = consentimentoAceite;
    }

    public String getAssinaturaCandidato() {
        return assinaturaCandidato;
    }

    public void setAssinaturaCandidato(String assinaturaCandidato) {
        this.assinaturaCandidato = assinaturaCandidato;
    }

    public String getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(String dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public Map<String, String> toPersistenceMap() {
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put("Nome Completo", persistedValue(nomeCompleto));
        values.put("Data de Nascimento", persistedValue(dataNascimento));
        values.put("Sexo", persistedValue(sexo));
        values.put("Nacionalidade", persistedValue(nacionalidade));
        values.put("B.I. / Passaporte", persistedValue(biPassaporte));
        values.put("Pais de Residencia", persistedValue(residenciaPais));
        values.put("Provincia", persistedValue(provincia));
        values.put("E-mail", persistedValue(email));
        values.put("Contacto Telefonico", persistedValue(contactoTelefonico));
        values.put("Nivel de Escolaridade", persistedValue(nivelEscolaridade));
        values.put("Area de Estudo", persistedValue(areaEstudo));
        values.put("Outra Area de Estudo", outraAreaEstudoSelecionada ? persistedValue(outraAreaEstudo) : "-" );
        values.put("Curso / Curso Tecnico", persistedValue(cursoTecnico));
        values.put("Instituicao", persistedValue(instituicao));
        values.put("Pais da Formacao", persistedValue(paisFormacao));
        values.put("Data de Conclusao do Curso", persistedValue(dataFimCurso));
        values.put("Areas de Interesse", persistedValue(areasInteresse));
        values.put("Outra Area de Interesse", outraAreaInteresseSelecionada ? persistedValue(outraAreaInteresse) : "-");
        values.put("Objectivos Profissionais", persistedValue(objectivosProfissionais));
        values.put("Resumo Profissional / Curriculum Vitae", persistedValue(resumoProfissional));
        values.put("Consentimento de Dados", consentimentoAceite ? "Sim" : "Nao");
        values.put("Assinatura do Candidato", persistedValue(assinaturaCandidato));
        values.put("Data de Assinatura", persistedValue(dataAssinatura));
        return values;
    }

    private String persistedValue(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
