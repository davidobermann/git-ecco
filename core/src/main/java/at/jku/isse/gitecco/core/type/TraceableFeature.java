package at.jku.isse.gitecco.core.type;

import at.jku.isse.gitecco.core.type.Feature;

public class TraceableFeature extends Feature{
    private int externalOcc, internalOcc, transientOcc;

    public TraceableFeature(String name) {
        super(name);
        externalOcc = 0;
        internalOcc = 0;
        transientOcc = 0;
    }

    public TraceableFeature(Feature f) {
        super(f.getName());
        externalOcc = 0;
        internalOcc = 0;
        transientOcc = 0;
    }

    /**
     * Increments the counter corresponding to the type.
     * @param t The type of the feature.
     */
    public TraceableFeature inc(FeatureType t) {
        switch (t) {
            case EXTERNAL:
                externalOcc++;
                break;
            case INTERNAL:
                internalOcc++;
                break;
            case TRANSIENT:
                transientOcc++;
                break;
        }
        return this;
    }

    public int getExternalOcc() {
        return externalOcc;
    }

    public int getInternalOcc() {
        return internalOcc;
    }

    public int getTransientOcc() {
        return transientOcc;
    }

    public int getTotalOcc() {
        return externalOcc + internalOcc + transientOcc;
    }
}