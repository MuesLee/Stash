package de.ts.stash.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

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

	@Column(nullable= false)
	private String name;
	
	@Column(nullable= false, precision=8, scale=2)
	@Digits(integer=9, fraction=2)                    
	private BigDecimal amount;
	
	@Column(nullable= false)
	@Enumerated(EnumType.STRING)
	private Unit unit;
	
	
}
