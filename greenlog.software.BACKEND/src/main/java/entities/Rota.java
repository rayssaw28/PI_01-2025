package entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "rota")
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
public class Rota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double distanciaTotal;

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "caminhao_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_rota_caminhao")
    )
    private Caminhao caminhao;

    @ManyToMany
    @JoinTable(
        name = "rota_ruas",
        joinColumns = @JoinColumn(name = "rota_id"),
        inverseJoinColumns = @JoinColumn(name = "rua_id"))
    @OrderColumn(name = "ordem_rua")
    private List<Rua> ruas;

    @ManyToMany
    @JoinTable(
        name = "rota_bairros",
        joinColumns = @JoinColumn(name = "rota_id"),
        inverseJoinColumns = @JoinColumn(name = "bairro_id"))
    @OrderColumn(name = "ordem_bairro")
    private List<Bairro> bairros;


    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_residuo", nullable = false)
    private TipoResiduo tipoResiduo;

    // --- Getters e Setters atualizados ---
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getDistanciaTotal() {
		return distanciaTotal;
	}

	public void setDistanciaTotal(double distanciaTotal) {
		this.distanciaTotal = distanciaTotal;
	}

	public Caminhao getCaminhao() {
		return caminhao;
	}

	public void setCaminhao(Caminhao caminhao) {
		this.caminhao = caminhao;
	}

	public List<Rua> getRuas() {
		return ruas;
	}

	public void setRuas(List<Rua> ruas) {
		this.ruas = ruas;
	}

	public List<Bairro> getBairros() {
		return bairros;
	}

	public void setBairros(List<Bairro> bairros) {
		this.bairros = bairros;
	}

    public TipoResiduo getTipoResiduo() {
        return tipoResiduo;
    }

    public void setTipoResiduo(TipoResiduo tipoResiduo) {
        this.tipoResiduo = tipoResiduo;
    }
}