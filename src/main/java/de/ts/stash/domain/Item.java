package de.ts.stash.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "items")
@Data
@EqualsAndHashCode(callSuper=true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item extends AbstractAuditableEntity<ApplicationUser, Long> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(optional=false)
	private ItemDefinition definition;

	@Column(nullable = false, precision = 8, scale = 2)
	@Digits(integer = 9, fraction = 2)
	private BigDecimal amount;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Unit unit;

	@Transient
	@JsonIgnore
	public boolean isValid() {
		return true;
	}

	public void update(Item form) {
		
	}
	
}
