package java.lang.invoke;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Module;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AccessBroker {
  private AccessBroker() {  throw new InternalError(); }
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE, ElementType.PACKAGE, ElementType.MODULE})
  public @interface GrantAccess {
    Class<?>[] frameworks();
  }
  
  static final List<Function<Class<?>, AnnotatedElement>> ANNOTATION_PROVIDERS =
      List.of(type -> type, Class::getPackage, Class::getModule);
  
  private static final ClassValue<Function<Class<?>, Optional<Lookup>>> CACHE = new ClassValue<Function<Class<?>, Optional<Lookup>>>() {
    @Override
    protected Function<Class<?>, Optional<Lookup>> computeValue(Class<?> type) {
      Set<Class<?>> allowedFrameworks = ANNOTATION_PROVIDERS
        .stream()
        .map(provider -> provider.apply(type))
        .map(element -> element.getDeclaredAnnotation(GrantAccess.class))
        .filter(Objects::nonNull)
        .flatMap(access -> Arrays.stream(access.frameworks()))
        .collect(Collectors.toSet());
      
      if (allowedFrameworks.isEmpty()) {
        return __ -> Optional.empty();
      }
      Optional<Lookup> lookup = Optional.of(new Lookup(type));
      return frameworkInterface -> lookup.filter(__ -> allowedFrameworks.contains(frameworkInterface));
    }
  };
  
  public interface AccessFacade {
    public Optional<Lookup> getAccess(Class<?> type);
  }
  
  private static final int MODES = Lookup.PRIVATE | Lookup.PACKAGE | Lookup.PROTECTED | Lookup.PUBLIC;
  private static void checkMode(Lookup lookup) {
    if ((lookup.lookupModes() & MODES) != MODES) {
      throw new IllegalArgumentException("invalid lookup");
    }
  }
  
  public static AccessFacade getAccessFacade(Lookup clientLookup, Class<?> frameworkInterface) {
    checkMode(clientLookup);
    
    Module module = clientLookup.lookupClass().getModule();
    if (!module.getDescriptor().provides().containsKey(frameworkInterface.getName())) {
      throw new IllegalArgumentException("module of lookup class doesn't provide an implementation for framework interface");
    }
    return type -> CACHE.get(type).apply(frameworkInterface);
  }
}
