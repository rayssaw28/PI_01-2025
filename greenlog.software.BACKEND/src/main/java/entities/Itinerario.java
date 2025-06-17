package entities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.time.LocalDate;

@Entity
@Table(
    name = "itinerario",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_itinerario_caminhao_data",
        columnNames = {"caminhao_id", "data"}
    )
)
@JsonIdentityInfo(
	    generator = ObjectIdGenerators.PropertyGenerator.class,
	    property = "id")
	public class Itinerario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "rota_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_itinerario_rota")
    )
    private Rota rota;

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "caminhao_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_itinerario_caminhao")
    )
    private Caminhao caminhao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public Rota getRota() {
		return rota;
	}

	public void setRota(Rota rota) {
		this.rota = rota;
	}

	public Caminhao getCaminhao() {
		return caminhao;
	}

	public void setCaminhao(Caminhao caminhao) {
		this.caminhao = caminhao;
	}

    
}