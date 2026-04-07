package me.TyAlternative.com.nocturne.core.phase.impl;

import me.TyAlternative.com.nocturne.api.phase.GamePhase;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import org.jetbrains.annotations.NotNull;

/**
 * Phase d'attente avant le début d'une partie.
 * Durée infinie : seul {@code /nocturne start} peut en sortir.
 */
@SuppressWarnings("unused")
public final class LobbyPhase implements GamePhase {


    @Override
    public void onStart(@NotNull PhaseContext context) {
        // Rien à initialiser en lobby
    }

    @Override
    public void onEnd(@NotNull PhaseContext context) {
        // Nettoyage délégué au NocturneGame.startGame()
    }

    @Override
    public @NotNull PhaseType getType() {
        return PhaseType.LOBBY;
    }

    @Override
    public long getDurationMs(@NotNull PhaseContext context) {
        return Long.MAX_VALUE; // Infini
    }
}
