package dmit2015.repository;

import dmit2015.entity.Phone;
import dmit2015.repository.AbstractJpaRepository;
import dmit2015.entity.Phone;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
@Transactional
public class PhoneRepository extends AbstractJpaRepository<Phone, Long> {

    public PhoneRepository() {
        super(Phone.class);
    }

    public List<Phone> findAllByUsername(String username) {
        return getEntityManager().createQuery("select o from Phone o where o.username = :usernameValue", Phone.class)
                .setParameter("usernameValue", username)
                .getResultList();
    }
}