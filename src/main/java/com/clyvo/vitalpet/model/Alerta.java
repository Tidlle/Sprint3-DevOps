package com.clyvo.vitalpet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String tipo;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String titulo;

    @NotBlank
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String descricao;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String prioridade;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String status = "PENDENTE";

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataAlerta;

    private LocalDateTime dataResolucao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acompanhamento_id")
    private Acompanhamento acompanhamento;

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        dataCadastro = agora;
        dataAtualizacao = agora;
        if (dataAlerta == null) {
            dataAlerta = agora;
        }
        if (status == null || status.isBlank()) {
            status = "PENDENTE";
        }
    }

    @PreUpdate
    public void preUpdate() { dataAtualizacao = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getDataAlerta() { return dataAlerta; }
    public void setDataAlerta(LocalDateTime dataAlerta) { this.dataAlerta = dataAlerta; }
    public LocalDateTime getDataResolucao() { return dataResolucao; }
    public void setDataResolucao(LocalDateTime dataResolucao) { this.dataResolucao = dataResolucao; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }
    public Acompanhamento getAcompanhamento() { return acompanhamento; }
    public void setAcompanhamento(Acompanhamento acompanhamento) { this.acompanhamento = acompanhamento; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alerta alerta)) return false;
        return Objects.equals(id, alerta.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
