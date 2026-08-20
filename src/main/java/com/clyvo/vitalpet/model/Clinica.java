package com.clyvo.vitalpet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "clinicas", uniqueConstraints = {
        @UniqueConstraint(name = "uk_clinica_cnpj", columnNames = "cnpj")
})
public class Clinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 120)
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String endereco;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String cidade;

    @NotBlank
    @Size(min = 2, max = 2)
    @Column(nullable = false, length = 2)
    private String estado;

    @NotBlank
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve ter 8 dígitos")
    @Column(nullable = false, length = 9)
    private String cep;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String telefone;

    @NotBlank
    @Email
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String email;

    @NotBlank
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos, sem pontuação")
    @Column(nullable = false, length = 14)
    private String cnpj;

    @Column(nullable = false)
    private boolean ativa = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Veterinario> veterinarios = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        dataCadastro = agora;
        dataAtualizacao = agora;
        ativa = true;
    }

    @PreUpdate
    public void preUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    public List<Veterinario> getVeterinarios() { return veterinarios; }
    public void setVeterinarios(List<Veterinario> veterinarios) { this.veterinarios = veterinarios; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Clinica clinica)) return false;
        return Objects.equals(id, clinica.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
