package my.jpa.app;

import java.lang.invoke.AccessBroker.GrantAccess;
import java.util.ServiceLoader;

import javax.persistence.EntityManager;

@GrantAccess(frameworks = EntityManager.class)
public class Bean {
  private void foo() { System.out.println("hello"); }  
  
  public static void main(String[] args) {
    ServiceLoader<EntityManager> loader = ServiceLoader.load(EntityManager.class);
    loader.findFirst().get().call(new Bean());
  }
}
