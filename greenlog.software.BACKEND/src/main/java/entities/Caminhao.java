package entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "caminhao")
public class Caminhao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A placa do caminhão é obrigatória.")
    @Size(min = 6, max = 8, message = "A placa deve ter entre 6 e 8 caracteres.")
    @Column(nullable = false, unique = true)
    private String placa;

    @NotBlank(message = "O nome do motorista é obrigatório.")
    @Column(nullable = false)
    private String motorista;

    @Positive(message = "A capacidade deve ser um número maior que zero.")
    @Column(nullable = false)
    private double capacidade;
    

    @ElementCollection(targetClass = TipoResiduo.class)
    @CollectionTable(
        name = "caminhao_residuos",
        joinColumns = @JoinColumn(name = "caminhao_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_residuo", nullable = false)
    private Set<TipoResiduo> tiposResiduos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getMotorista() {
		return motorista;
	}

	public void setMotorista(String motorista) {
		this.motorista = motorista;
	}

	public double getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(double capacidade) {
		this.capacidade = capacidade;
	}

	public Set<TipoResiduo> getTiposResiduos() {
		return tiposResiduos;
	}

	public void setTiposResiduos(Set<TipoResiduo> tiposResiduos) {
		this.tiposResiduos = tiposResiduos;
	}

  
    
}
