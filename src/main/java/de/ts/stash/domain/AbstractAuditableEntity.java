package de.ts.stash.domain;

import java.time.LocalDate;

import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode(callSuper=true)
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditableEntity<U, ID> extends AbstractPersistableEntity<ID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@CreatedDate
	LocalDate createdDate;

	@LastModifiedDate
	LocalDate lastModifiedDate;

	@CreatedBy
	@ManyToOne
	@JoinColumn(name = "created_by")
	U createdBy;

	@LastModifiedBy
	@ManyToOne
	@JoinColumn(name = "last_modified_by")
	U lastModifiedBy;
}