package de.ts.stash.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.ts.stash.domain.ItemDefinition;

@RepositoryRestResource(path = "itemdefinitions", collectionResourceRel = "itemdefinitions", itemResourceRel = "itemdefinition")
public interface ItemDefinitionRepository extends JpaRepository<ItemDefinition, Long> {

	ItemDefinition findByName(String name);
}
