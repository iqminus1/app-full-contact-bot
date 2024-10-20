package uz.pdp.appfullcontactbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.appfullcontactbot.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    List<User> findAllBySubscribedAndSubscriptionEndTimeIsBefore(boolean subscribed, LocalDateTime subscriptionEndTime);
//
//    List<User> findAllBySubscribedAndSubscriptionEndTimeIsBetween(boolean subscribed, LocalDateTime start, LocalDateTime end);
//
//    List<User> findAllBySubscribed(boolean subscribed);
//
//    boolean existsByCardNumber(String cardNumber);
//
//    List<User> findAllByAdminAfter(int admin);
}