package de.ts.stash.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "itemdefintions")
@Data
@EqualsAndHashCode(callSuper=true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDefinition extends AbstractAuditableEntity<ApplicationUser, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(nullable= false)
	private String name;


	public void update(ItemDefinition item) {
		this.name = item.name;
	}

	@Transient
	@JsonIgnore
	public boolean isValid() {
		if (getId() == null)
			return false;
		if (this.name == null || this.name.equals(""))
			return false;

		return true;
	}

}
