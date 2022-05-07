package app.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.models.CarpetConfiguration;

/**
 * Interface which defines the methods which can be used to get and store CarpetConfigurations from and to the database.
 * 
 * @author Florian Jungermann
 *
 */
public interface CarpetConfigurationRepository extends JpaRepository<CarpetConfiguration, Long> {


    public List<CarpetConfiguration> findByUserID(String userID);
	
}
