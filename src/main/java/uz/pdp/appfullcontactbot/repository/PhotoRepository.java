package uz.pdp.appfullcontactbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.appfullcontactbot.enums.Status;
import uz.pdp.appfullcontactbot.model.Photo;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findAllByStatus(Status status);
}