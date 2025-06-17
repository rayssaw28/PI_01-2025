package entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "ponto_coleta")
public class PontoColeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private String responsavel;

    @Column(nullable = false)
    private String contato;

    @Column(nullable = false)
    private String endereco;

    @ElementCollection(targetClass = TipoResiduo.class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "ponto_residuos",
        joinColumns = @JoinColumn(name = "ponto_coleta_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_residuo", nullable = false)
    private Set<TipoResiduo> tiposResiduos = new HashSet<>();

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(
        name = "bairro_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_ponto_bairro"))
    private Bairro bairro;  // agora serializa normalmente

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }

    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public Set<TipoResiduo> getTiposResiduos() { return tiposResiduos; }
    public void setTiposResiduos(Set<TipoResiduo> tiposResiduos) { this.tiposResiduos = tiposResiduos; }

    public Bairro getBairro() { return bairro; }
    public void setBairro(Bairro bairro) { this.bairro = bairro; }

    // equals() e hashCode() baseados no ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PontoColeta)) return false;
        PontoColeta that = (PontoColeta) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
