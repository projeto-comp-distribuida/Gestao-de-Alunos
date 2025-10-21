package com.distrischool.student.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entidade Address - Endereço completo
 * Pode ser compartilhado entre Student e Guardian
 */
@Entity
@Table(name = "addresses", indexes = {
    @Index(name = "idx_address_zipcode", columnList = "zipcode")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 255, message = "Logradouro deve ter no máximo 255 caracteres")
    @Column(name = "street", nullable = false, length = 255)
    private String street;

    @NotBlank(message = "Número é obrigatório")
    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    @Column(name = "number", nullable = false, length = 20)
    private String number;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    @Column(name = "complement", length = 100)
    private String complement;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    @Column(name = "neighborhood", nullable = false, length = 100)
    private String neighborhood;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    @Column(name = "state", nullable = false, length = 2)
    private String state;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
    @Column(name = "zipcode", nullable = false, length = 8)
    private String zipcode;

    @Size(max = 100, message = "País deve ter no máximo 100 caracteres")
    @Column(name = "country", length = 100)
    @Builder.Default
    private String country = "Brasil";

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", length = 20)
    @Builder.Default
    private AddressType addressType = AddressType.RESIDENTIAL;

    public enum AddressType {
        RESIDENTIAL,    // Residencial
        COMMERCIAL,     // Comercial
        BILLING,        // Cobrança
        DELIVERY        // Entrega
    }

    /**
     * Retorna o endereço completo formatado
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(street).append(", ").append(number);
        if (complement != null && !complement.isEmpty()) {
            sb.append(" - ").append(complement);
        }
        sb.append(" - ").append(neighborhood);
        sb.append(" - ").append(city).append("/").append(state);
        sb.append(" - CEP: ").append(zipcode);
        return sb.toString();
    }
}

