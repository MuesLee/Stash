package de.ts.stash.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.ts.stash.domain.ApplicationUser;

@RepositoryRestResource(path = "users", collectionResourceRel = "users", itemResourceRel = "user")
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

	ApplicationUser findByUsername(String username);
}
