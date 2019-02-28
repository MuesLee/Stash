package de.ts.stash.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import de.ts.stash.auth.user.RefreshToken;

@RepositoryRestResource(path = "refreshtokens", collectionResourceRel = "refreshtokens", itemResourceRel = "refreshtoken")
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	
	RefreshToken findByValue(String value);
}
