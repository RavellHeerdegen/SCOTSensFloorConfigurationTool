package app.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import app.models.CarpetAction;

/**
 * Interface which defines the methods which can be used to get and store CarpetConfigurations from and to the database.
 * 
 * @author Florian Jungermann
 *
 */
public interface CarpetActionRepository extends JpaRepository<CarpetAction, Long> {

}
