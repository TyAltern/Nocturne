package me.TyAlternative.com.nocturne.mechanics.anonymity;

public class NameTag {
    String prefix;
    String customName;
    String suffix;
    boolean hidden;
    boolean seeThrough;

    NameTag(Builder builder) {
        this.prefix = builder.prefix;
        this.customName = builder.customName;
        this.suffix = builder.suffix;
        this.hidden = builder.hidden;
        this.seeThrough = builder.seeThrough;
    }

    public String getTotalName() {
        return (this.prefix == null? "" : this.prefix) + (this.customName == null? "" : this.customName) + (this.suffix == null? "" : this.suffix);
    }

    public static class Builder {
        private String prefix = "";
        private String customName = "";
        private String suffix = "";
        private boolean hidden = false;
        private boolean seeThrough = false;

        public Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }
        public Builder customName(String customName) {
            this.customName = customName;
            return this;
        }
        public Builder suffix(String suffix) {
            this.suffix = suffix;
            return this;
        }
        public Builder hidden(boolean hidden) {
            this.hidden = hidden;
            return this;
        }
        public Builder seeThrough(boolean seeThrough) {
            this.seeThrough = seeThrough;
            return this;
        }

        public NameTag build() {
            return new NameTag(this);
        }

    }
}
