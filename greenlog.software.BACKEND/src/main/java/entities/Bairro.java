package entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "bairro")
@JsonIgnoreProperties({ "pontosColeta", "ruasOrigem", "ruasDestino" })
public class Bairro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;


    @OneToMany(mappedBy = "bairro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PontoColeta> pontosColeta;

    @OneToMany(mappedBy = "bairroOrigem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rua> ruasOrigem;

    @OneToMany(mappedBy = "bairroDestino", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rua> ruasDestino;



    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<PontoColeta> getPontosColeta() {
        return pontosColeta;
    }
    public void setPontosColeta(List<PontoColeta> pontosColeta) {
        this.pontosColeta = pontosColeta;
    }

    public List<Rua> getRuasOrigem() {
        return ruasOrigem;
    }
    public void setRuasOrigem(List<Rua> ruasOrigem) {
        this.ruasOrigem = ruasOrigem;
    }

    public List<Rua> getRuasDestino() {
        return ruasDestino;
    }
    public void setRuasDestino(List<Rua> ruasDestino) {
        this.ruasDestino = ruasDestino;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bairro)) return false;
        Bairro that = (Bairro) o;
        return id != null && id.equals(that.getId());
    }
    

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    // toString para facilitar a depuração
    @Override
    public String toString() {
        return "Bairro{id=" + id + ", nome='" + nome + "'}";
    }
}