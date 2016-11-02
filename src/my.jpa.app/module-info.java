import java.lang.invoke.AccessBroker.GrantAccess;
import javax.persistence.EntityManager;

@GrantAccess(frameworks = EntityManager.class)
module my.jpa.app {
  requires javax.persistence;
  
  // no export, no open !
  
  uses javax.persistence.EntityManager;
}