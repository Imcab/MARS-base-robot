package mars.source.models.multimodules;

import java.util.LinkedHashMap;
import java.util.Map;

import mars.source.models.singlemodule.IO;
import mars.source.models.singlemodule.ModularSubsystem;

/**
 * Un Actor (IO) que no maneja hardware real, sino que maneja una colección de subsistemas.
 */
public abstract class CompositeIO<D extends CompositeData<D>> implements IO<D> {
    
    // El famoso Mapa. Usamos LinkedHashMap para mantener el orden en el que agregamos a los hijos.
    private final Map<String, ModularSubsystem<?, ?>> children = new LinkedHashMap<>();

    /**
     * Registra un nuevo subsistema en el equipo.
     */
    public void registerChild(ModularSubsystem<?, ?> child) {
        children.put(child.tableName, child);
    }

    /**
     * Recupera un hijo usando su nombre, con casteo automático (Type Safety).
     */
    @SuppressWarnings("unchecked")
    public <S extends ModularSubsystem<?, ?>> S getChild(String key) {
        return (S) children.get(key);
    }

    /**
     * Devuelve todos los hijos por si necesitas iterarlos (ej. para un paro de emergencia).
     */
    public Iterable<ModularSubsystem<?, ?>> getAllChildren() {
        return children.values();
    }

    // El método updateInputs viene de tu interfaz IO original.
    // La clase concreta decidirá cómo extraer los datos del mapa y meterlos en 'D inputs'.
    @Override
    public abstract void updateInputs(D inputs);
}