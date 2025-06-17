package entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "rua")
public class Rua {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(
        name = "bairro_origem_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_rua_bairro_origem")
    )
    @JsonIgnoreProperties({ "pontosColeta", "ruasOrigem", "ruasDestino" })
    private Bairro bairroOrigem;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(
        name = "bairro_destino_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_rua_bairro_destino")
    )
    @JsonIgnoreProperties({ "pontosColeta", "ruasOrigem", "ruasDestino" })
    private Bairro bairroDestino;

    @Column(nullable = false)
    private double distancia;
    
    @Override
    public String toString() {

        return "Rua{id=" + id + ", origem=" + (bairroOrigem != null ? bairroOrigem.getId() : "null") + ", destino=" + (bairroDestino != null ? bairroDestino.getId() : "null") + "}";
    }



    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Bairro getBairroOrigem() {
        return bairroOrigem;
    }
    public void setBairroOrigem(Bairro bairroOrigem) {
        this.bairroOrigem = bairroOrigem;
    }

    public Bairro getBairroDestino() {
        return bairroDestino;
    }
    public void setBairroDestino(Bairro bairroDestino) {
        this.bairroDestino = bairroDestino;
    }

    public double getDistancia() {
        return distancia;
    }
    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rua)) return false;
        Rua that = (Rua) o;
        return id != null && id.equals(that.getId());
    }
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
