package com.clyvo.vitalpet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataHora;

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String tipo;

    @Size(max = 1000)
    @Column(length = 1000)
    private String sintomas;

    @Size(max = 1000)
    @Column(length = 1000)
    private String diagnostico;

    @Size(max = 1000)
    @Column(length = 1000)
    private String tratamento;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String status = "AGENDADA";

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @OneToOne(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Acompanhamento acompanhamento;

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        dataCadastro = agora;
        dataAtualizacao = agora;
        if (status == null || status.isBlank()) {
            status = "AGENDADA";
        }
    }

    @PreUpdate
    public void preUpdate() { dataAtualizacao = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public String getTratamento() { return tratamento; }
    public void setTratamento(String tratamento) { this.tratamento = tratamento; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }
    public Veterinario getVeterinario() { return veterinario; }
    public void setVeterinario(Veterinario veterinario) { this.veterinario = veterinario; }
    public Acompanhamento getAcompanhamento() { return acompanhamento; }
    public void setAcompanhamento(Acompanhamento acompanhamento) { this.acompanhamento = acompanhamento; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Consulta consulta)) return false;
        return Objects.equals(id, consulta.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
