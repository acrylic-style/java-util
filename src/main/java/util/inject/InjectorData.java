package util.inject;

class InjectorData {
    private final Class<?> interfaceClass;
    private final String baseClass;

    public InjectorData(Class<?> interfaceClass, String baseClass) {
        this.interfaceClass = interfaceClass;
        this.baseClass = baseClass;
    }

    public Class<?> getInterfaceClass() { return interfaceClass; }

    public String getBaseClass() { return baseClass.replaceAll("\\.", "/"); }
}
