module my.jpa.app {
  requires javax.persistence;
  
  // no export, no open !
  
  uses javax.persistence.EntityManager;
}