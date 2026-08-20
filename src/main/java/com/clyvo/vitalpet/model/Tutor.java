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
@Table(name = "tutores", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tutor_cpf", columnNames = "cpf"),
        @UniqueConstraint(name = "uk_tutor_email", columnNames = "email")
})
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 120)
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String email;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String telefone;

    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos, sem pontuação")
    @Column(nullable = false, length = 11)
    private String cpf;

    @Size(max = 180)
    @Column(length = 180)
    private String endereco;

    @Size(max = 80)
    @Column(length = 80)
    private String cidade;

    @Size(min = 2, max = 2)
    @Column(length = 2)
    private String estado;

    @Pattern(regexp = "^$|\\d{5}-?\\d{3}", message = "CEP deve ter 8 dígitos")
    @Column(length = 9)
    private String cep;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Pet> pets = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        dataCadastro = agora;
        dataAtualizacao = agora;
        ativo = true;
    }

    @PreUpdate
    public void preUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    public List<Pet> getPets() { return pets; }
    public void setPets(List<Pet> pets) { this.pets = pets; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tutor tutor)) return false;
        return Objects.equals(id, tutor.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
