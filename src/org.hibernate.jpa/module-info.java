module org.hibernate.jpa {
  requires javax.persistence;
  provides javax.persistence.EntityManager with org.hibernate.jpa.EntityManagerImpl;
}