package com.isacofff.clientbase.settings;

//Prepare yourself for bad code

public abstract class Setting<T> {
    protected String name;
    protected T value;

    public Setting(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public static class BooleanSetting extends Setting<Boolean> {
        public BooleanSetting(String name, boolean value) {
            super(name, value);
        }
        
        public void toggle() {
            setValue(!getValue());
        }
    }

    public static class NumberSetting extends Setting<Double> {
        private double min, max, increment;

        public NumberSetting(String name, double value, double min, double max, double increment) {
            super(name, value);
            
            this.min = min;
            this.max = max;
            this.increment = increment;
        }

        public double getMin() { return min; }
        public double getMax() { return max; }
        public double getIncrement() { return increment; }
    }

    public static class ModeSetting extends Setting<String> {
        private String[] modes;
        private int index;

        public ModeSetting(String name, String defaultMode, String... modes) {
            super(name, defaultMode);

            this.modes = modes;
            this.index = 0;
            for (int i = 0; i < modes.length; i++) {
                if (modes[i].equals(defaultMode)) {
                    this.index = i;
                    break;
                }
            }
        }

        public void cycle() {
            if (modes.length == 0) {
                return;
            }
            index = (index + 1) % modes.length;
            setValue(modes[index]);
        }
    }
}
