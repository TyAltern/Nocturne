package me.TyAlternative.com.nocturne.mechanics.particle;

import org.bukkit.Particle;

public class ParticleData {

    /** Type de particule affiché. */
    private final Particle particle;

    /** Nombre de particules affichées par apparition. */
    private final int particle_count;

    /** Rayon d'éparpillement des particules autour du joueur. */
    private final double spread;

    /** Hauteur au-dessus du sol pour centrer les particules sur le corps. */
    private final double height_offset;

    /** Tick (1/20) entre chaque apparition des particules. */
    private final int spawnTickInterval;

    public ParticleData(Builder builder) {
        this.particle = builder.particle;
        this.particle_count = builder.particle_count;
        this.spread = builder.spread;
        this.height_offset = builder.height_offset;
        this.spawnTickInterval = builder.spawnTickInterval;
    }

    public Particle getParticle() {
        return particle;
    }

    public int getParticle_count() {
        return particle_count;
    }

    public double getSpread() {
        return spread;
    }

    public double getHeight_offset() {
        return height_offset;
    }

    public int getSpawnTickInterval() {
        return spawnTickInterval;
    }

    public static class Builder {
        private Particle particle = Particle.WITCH;
        private int particle_count = 15;
        private double spread = 0.6;
        private double height_offset = 1.0;
        private int spawnTickInterval = 20;

        public Builder particle(Particle particle) {
            this.particle = particle;
            return this;
        }
        public Builder particle_count(int particle_count) {
            this.particle_count = particle_count;
            return this;
        }
        public Builder spread(double spread) {
            this.spread = spread;
            return this;
        }
        public Builder height_offset(double height_offset) {
            this.height_offset = height_offset;
            return this;
        }
        public Builder spawnTickInterval(int spawnTickInterval) {
            this.spawnTickInterval = spawnTickInterval;
            return this;
        }
        public ParticleData build() {
            return new ParticleData(this);
        }
    }
}
