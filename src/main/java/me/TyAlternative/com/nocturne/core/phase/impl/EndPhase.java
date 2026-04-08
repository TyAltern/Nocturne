package me.TyAlternative.com.nocturne.core.phase.impl;

import me.TyAlternative.com.nocturne.api.phase.GamePhase;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class EndPhase implements GamePhase {

    private static final long DURATION_MS = 10_000L;

    @Override
    public void onStart(@NotNull PhaseContext context) {
        // TODO : afficher l'écran de fin
    }

    @Override
    public void onEnd(@NotNull PhaseContext context) {
        // Le nettoyage final est effectué par NocturneGame.stopGame()

    }

    @Override
    public @NotNull PhaseType getType() {
        return PhaseType.END;
    }

    @Override
    public long getDurationMs(@NotNull PhaseContext context) {
        return DURATION_MS;
    }
}
