package br.edu.ifma.ru.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cardapios", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"data", "tipo_refeicao"})
})
public class Cardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_refeicao", nullable = false)
    private TipoRefeicao tipoRefeicao;

    @Column(name = "prato_principal", nullable = false)
    private String pratoPrincipal;

    @Column(nullable = false)
    private String acompanhamento;

    @Column(nullable = false)
    private String salada;

    @Column(nullable = false)
    private String sobremesa;

    @Column(nullable = false)
    private String suco;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    public Cardapio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public TipoRefeicao getTipoRefeicao() { return tipoRefeicao; }
    public void setTipoRefeicao(TipoRefeicao tipoRefeicao) { this.tipoRefeicao = tipoRefeicao; }

    public String getPratoPrincipal() { return pratoPrincipal; }
    public void setPratoPrincipal(String pratoPrincipal) { this.pratoPrincipal = pratoPrincipal; }

    public String getAcompanhamento() { return acompanhamento; }
    public void setAcompanhamento(String acompanhamento) { this.acompanhamento = acompanhamento; }

    public String getSalada() { return salada; }
    public void setSalada(String salada) { this.salada = salada; }

    public String getSobremesa() { return sobremesa; }
    public void setSobremesa(String sobremesa) { this.sobremesa = sobremesa; }

    public String getSuco() { return suco; }
    public void setSuco(String suco) { this.suco = suco; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
