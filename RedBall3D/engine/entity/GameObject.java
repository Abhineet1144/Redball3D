package engine.entity;

import engine.entity.components.Component;

import java.io.Serializable;
import java.util.ArrayList;

public class GameObject implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private ArrayList<Component> components;

    public GameObject(String name) {
        this.name = name;
        components = new ArrayList<>();
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            try {
                if (componentClass.isInstance(c)) {
                    return componentClass.cast(c);
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
                assert false : "Error: Casting Component";
            }
        }
        return null;
    }

    public <T extends Component> boolean removeComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isInstance(c)) {
                components.remove(c);
                return true;
            }
        }
        return false;
    }

    public <T extends Component> T addComponent(Component c) {
        if (c == null) {
            return null;
        }
        if (contains(c)) return null;
        this.components.add(c);
        c.gameObject = this;
        return (T) getComponent(c.getClass());
    }

    public void update(float dt) {
        for (Component c : components) {
            try {
                c.update(dt);
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
            }
        }
    }

    public void start() {
        for (Component c : components) {
            try {
                c.start();
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
            }
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public boolean contains(Component c) {
        for (Component component : components) {
            if (c.getClass().isInstance(component)) {
                return true;
            }
        }
        return false;
    }

    public void removeComponentByName(String className) {
        components.removeIf(c -> c.getClass().getName().equals(className));
    }

    public void setName(String name) {
        this.name = name;
    }
}