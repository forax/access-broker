package org.hibernate.jpa;

import java.lang.invoke.AccessBroker;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.AccessBroker.AccessFacade;
import java.lang.invoke.MethodHandles.Lookup;

import javax.persistence.EntityManager;

public class EntityManagerImpl implements EntityManager {
  public void call(Object bean) {
    // access a bean members from a class of the EntityManager implementation module
    AccessFacade facade = AccessBroker.getAccessFacade(MethodHandles.lookup(), EntityManager.class);
    Lookup lookup = facade.getAccess(bean.getClass()).get();
    MethodHandle mh;
    try {
      mh = lookup.findVirtual(bean.getClass(), "foo", MethodType.methodType(void.class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
    try {
      mh.invoke(bean);
    } catch (Throwable e) {
      throw new AssertionError(e);
    }
  }
}
