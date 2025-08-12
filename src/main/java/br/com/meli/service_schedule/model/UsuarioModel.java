package br.com.meli.service_schedule.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "usuarios")
public abstract class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String password;
    private String nome;

    @Column(unique = true)
    private String email;

    private String endereco;
    private String cep;
    private String cidade;
    private String estado;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;


}
